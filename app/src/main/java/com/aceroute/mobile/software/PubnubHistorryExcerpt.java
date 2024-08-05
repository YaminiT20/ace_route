package com.aceroute.mobile.software;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubError;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by yash on 18/2/16.
 */

    /**
     * You can use this class to iterate over historical PubNub messages. The messages are retrieved transparently while you
     * iterate over the history excerpt. This class and the returned iterators are thread-safe.
     *
     * See {@link #main(String[])} for a usage example.
     */
    public class PubnubHistorryExcerpt implements Iterable<Object> {

        /**
         * This main method contains a usage example for this class. It downloads last 3 hour's messages of the MtGox BTC/USD ticker
         * channel from PubNub and outputs the timestamp and USD value which are found in the messages.
         */
        public static void main(String[] args) throws JSONException {

            String PUBNUB_CHANNEL_MTGOX_TICKER_BTCUSD = "d5f06780-30a8-4a48-a2f8-7ed181b4a13f";

           // PubNub pubnub = new PubNub(null, PUBNUB_SUBSCRIBE_KEY_MTGOX);
            PNConfiguration pnConfiguration = null;
            pnConfiguration.setSubscribeKey("sub-c-424c2436-49c8-11e5-b018-0619f8945a4f");
            //pnConfiguration.setPublishKey("PublishKey");
            pnConfiguration.setSecure(true);
            PubNub pubnub = new PubNub(pnConfiguration);

            long ONE_HOUR_IN_MILLIS = 60 * 60 * 1000;

            long end = System.currentTimeMillis();
            long start = end - 3 * ONE_HOUR_IN_MILLIS;

            // convert from milliseconds as time unit (10^-3 seconds) to
            // pubnub's better-than-microsecond precision time units (10^-7 seconds)
            start *= 10000;
            end *= 10000;

            PubnubHistorryExcerpt history = new PubnubHistorryExcerpt(pubnub, PUBNUB_CHANNEL_MTGOX_TICKER_BTCUSD, start, end);

          //  DefaultDateFormat dateFormat = DefaultDateFormat.create();

            for (Object message : history) {
                JSONObject messageJson = (JSONObject) message;

                JSONObject ticker = messageJson.getJSONObject("ticker");
                long instant = ticker.getLong("now");
                BigDecimal value = new BigDecimal(ticker.getJSONObject("last_local").getString("value"));

                instant /= 1000; // convert from microseconds to milliseconds

               // System.out.println(dateFormat.format(instant) + ": " + value);
            }

            System.exit(0);
        }

        /**
         * This is the maximum number of messages to fetch in one batch. If you fetch many messages, higher numbers improve
         * performance. Setting this to a value higher than 100 doesn't have an effect, because Pubnub currently doesn't
         * support fetching more than this many messages at once.
         */
        private static final int BATCH_SIZE = 100;

        private final PubNub pubnub;

        private final String channel;

        private final long start;

        private final long end;

        /**
         * Constructs a new excerpt over which you can iterate. Insances represent an excerpt. No retrieval operations are
         * started unless you call iterator().next() for the first time.
         *
         * @param pubnub
         *            The Pubnub connection to use for retrieving messages.
         * @param channel
         *            The channel for which to retrieve historical messages.
         * @param start
         *            The beginning of the time interval for which to retrieve messages, in pubnub's time units (10^-7
         *            seconds, so milliseconds * 10000) since 1970-01-01 00:00:00).
         * @param end
         *            The end of the time interval for which to retrieve messages, in pubnub's time units (10^-7 seconds, so
         *            milliseconds * 10000) since 1970-01-01 00:00:00).
         */
        private PubnubHistorryExcerpt(PubNub pubnub, String channel, long start, long end) {
            this.pubnub = pubnub;
            this.channel = channel;
            this.start = start;
            this.end = end;
        }

        public Iterator<Object> iterator() {
            return new Iter();
        }

        private class Iter implements Iterator<Object> {

            /**
             * This list_cal is used as a fifo buffer for messages retrieves through this iterator. It also acts as the main
             * synchronization lock for synchronizing access between threads accessing this class as an iterator and threads
             * calling back from the Pubnub API.
             */
            private LinkedList<Object> buffer = new LinkedList<Object>();

            /**
             * This field stores the end of the time range of the previous batch retrieval, in Pubnub time units (10th of a
             * microsecond, so milliseconds*10000). For the following batch retrieval, this is used as the start time for
             * retrieving the following messages.
             */
            private long prevBatchTimeRangeEnd = PubnubHistorryExcerpt.this.start;

            /**
             * Retrieval of messages is handled asynchronously. That means that exceptions which are thrown during retrieval
             * can't automatically be propagated through to the code which invokes <code>next()</code> or
             * <code>hasNext()</code> . Therefor, such an exception is stored temporarily in this field and then re-thrown
             * from within <code>next()</code> or <code>hasNext()</code>.
             */
            private Exception caughtDuringRetrieval = null;

            /**
             * This object is used to wait on and to notify about updates of the buffer.
             */
            private Object notifier = new Object();

            /**
             * Because of spurious wakeups that can happen during wait(), this field is necessary to tell the waiting thread
             * if retrieval is still running.
             */
            private boolean retrieving = false;

            /**
             * The callback object to use for retrieving messages. This is stored in a field here for re-use. This is a
             * compromise between performance and low memory footprint, slightly in favor of performance.
             */
            private InternalCallback internalCallback = new InternalCallback();

            private void retrieveNextBatch() {
                synchronized (notifier) {
                    this.retrieving = true;

                    // String startStr = DefaultDateFormat.create().format(prevBatchTimeRangeEnd / 10000);
                    // String endStr = DefaultDateFormat.create().format(end / 10000);
                    // System.out.println("fetching from " + startStr + " till " + endStr);

                    if (Iter.this.prevBatchTimeRangeEnd < PubnubHistorryExcerpt.this.end) {
                        /*PubnubHistorryExcerpt.this.pubnub.history( //
                                PubnubHistorryExcerpt.this.channel, //
                                Iter.this.prevBatchTimeRangeEnd, //
                                PubnubHistorryExcerpt.this.end, //
                                BATCH_SIZE, //
                                false, //
                                Iter.this.internalCallback //
                        );
*/
                        waitUntilNextBatchRetrievalFinished();
                    }
                }
            }

            private void waitUntilNextBatchRetrievalFinished() {
                while (this.retrieving) {
                    try {
                        this.notifier.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            private class InternalCallback extends SubscribeCallback {


                public void successCallback(String channel, Object message) {
                    synchronized (Iter.this.notifier) {
                        try {
                            processSuccessCallback(channel, message);
                        } catch (Exception e) {
                            Iter.this.caughtDuringRetrieval = e;
                        } finally {
                            Iter.this.retrieving = false;
                            Iter.this.notifier.notifyAll();
                        }
                    }
                }

                public void errorCallback(String channel, PubNubError error) {
                    Iter.this.caughtDuringRetrieval = new Exception("" + //
                            error.getClass().getName() + ": " + //
                            error.getErrorString() /*+ //
                            " (code=" + error.errorCode + "; extendedCode=" + error.errorCodeExtended */+ ")");

                    Iter.this.caughtDuringRetrieval.fillInStackTrace();
                }

                @Override
                public void status(PubNub pubNub, PNStatus pnStatus) {

                }

                @Override
                public void message(PubNub pubNub, PNMessageResult pnMessageResult) {

                }

                @Override
                public void presence(PubNub pubNub, PNPresenceEventResult pnPresenceEventResult) {

                }
            }

            private void processSuccessCallback(String channel, Object message) throws JSONException {
                if (message == null)
                    throw new NullPointerException("retrieved message is null");

                if (!(message instanceof JSONArray))
                    throw new RuntimeException("retrieved message is not a " + JSONArray.class.getName());

                JSONArray historyMessage = (JSONArray) message;

                // System.out.println(historyMessage.toString(2));

                JSONArray messageList = extractMessageList(historyMessage);

                long batchTimeRangeEnd = extractBatchTimeRangeEnd(historyMessage);
                if (batchTimeRangeEnd > 0)
                    Iter.this.prevBatchTimeRangeEnd = batchTimeRangeEnd;
                else
                    Iter.this.prevBatchTimeRangeEnd = end;

                processMessageList(messageList);
            }

            private void processMessageList(JSONArray messageList) {
                int i = 0;

                for (; i < messageList.length(); i++) {
                    JSONObject message;

                    try {
                        message = messageList.getJSONObject(i);
                    } catch (JSONException e) {
                        String str;
                        try {
                            str = messageList.toString(2);
                        } catch (JSONException secondaryE) {
                            str = "(couldn't convert messageList to String because of " + secondaryE.toString() + ")";
                        }
                        throw new RuntimeException("couldn't extract message at index " + i + " from messageList (messageList:\n" + str
                                + "\n(end of messageList)\n)", e);
                    }

                    Iter.this.buffer.add(message);
                }
            }

            private long extractBatchTimeRangeEnd(JSONArray historyMessage) {
                long batchTimeRangeEnd;
                try {
                    batchTimeRangeEnd = historyMessage.getLong(2);
                } catch (JSONException e) {
                    String str = safeConvertHistoryMessageToString(historyMessage);
                    throw new RuntimeException("could not extract element 2 (batchTimeRangeEnd) of retrieved historyMessage (historyMessage:\n" + str
                            + "\n(end of historyMessage)\n)", e);
                }
                return batchTimeRangeEnd;
            }

            private String safeConvertHistoryMessageToString(JSONArray historyMessage) {
                String str;
                try {
                    str = historyMessage.toString(2);
                } catch (JSONException secondaryE) {
                    str = "(couldn't convert historyMessage to String because of " + secondaryE.toString() + ")";
                }
                return str;
            }

            private JSONArray extractMessageList(JSONArray historyMessage) {
                JSONArray messageArJson;
                try {
                    messageArJson = historyMessage.getJSONArray(0);
                } catch (JSONException e) {
                    String str = safeConvertHistoryMessageToString(historyMessage);
                    throw new RuntimeException("could not extract element 0 (messageList) of retrieved historyMessage (historyMessage:\n" + str
                            + "\n(end of historyMessage)\n)", e);
                }
                return messageArJson;
            }

            public boolean hasNext() {
                synchronized (Iter.this.buffer) {
                    ensureNotInExceptionState();

                    if (Iter.this.buffer.isEmpty())
                        retrieveNextBatch();

                    return !Iter.this.buffer.isEmpty();
                }
            }

            public Object next() {
                synchronized (Iter.this.buffer) {
                    if (!hasNext()) {
                        throw new NoSuchElementException("there are no more elements in this iterator");
                    }

                    Object result = Iter.this.buffer.removeFirst();

                    return result;
                }
            }

            private void ensureNotInExceptionState() {
                if (caughtDuringRetrieval != null) {
                    throw new RuntimeException("an exception was caught already by a previous attempt to access this iterator", caughtDuringRetrieval);
                }
            }

            public void remove() {
                throw new UnsupportedOperationException(getClass().getName() + " doesn't support remove()");
            }
        }
    }

