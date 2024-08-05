package com.aceroute.mobile.software.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;


public class CustomDoneEditText extends EditText {

    public CustomDoneEditText(Context context)
    {
        super(context);
    }

    public CustomDoneEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomDoneEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs)
    {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        //outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        outAttrs.imeOptions &= EditorInfo.IME_ACTION_DONE;
        return conn;
    }
}