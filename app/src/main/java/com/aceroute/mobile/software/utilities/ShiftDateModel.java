package com.aceroute.mobile.software.utilities;

import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by xelium on 27/10/16.
 */
public class ShiftDateModel {
    public Date date;
    public Shifts shift;
    public ArrayList<Shifts> unshiftlist;
    public ArrayList<Shifts> extrashiftlist;

    public Worker getWorker() {
        return worker;
    }

    public Worker worker;

    public ShiftDateModel(Date date) {
        this.date = date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setShiftlist(Shifts shift) {
        this.shift = shift;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public int getUnAvialCount() {
        if(unshiftlist==null){
            return 0;
        }
        return unshiftlist.size();
    }

    private void setList(){
        if(unshiftlist==null){
            unshiftlist=new ArrayList<Shifts>();
        }
    }

    private void setExtraList(){
        if(extrashiftlist==null){
            extrashiftlist=new ArrayList<Shifts>();
        }
    }

    public void addUnShiftListObject(Shifts unavil) {
        setList();
        unshiftlist.add(unavil);
        if (unshiftlist.size()>1)
            Collections.sort(unshiftlist, new ShiftByTimesComparer());

    }

    public void addExtraShiftListObject(Shifts extraLst) {
        setExtraList();
        extrashiftlist.add(extraLst);
        if (extrashiftlist.size()>1)
            Collections.sort(extrashiftlist, new ShiftByTimesComparer());

    }

    public class ShiftByTimesComparer implements Comparator<Shifts> {
        @Override
        public int compare(Shifts x, Shifts y) {
            // TODO: Handle null x or y values
            int startComparison = compare(x.sMin, y.sMin);
            return startComparison != 0 ? startComparison
                    : compare(x.sMin, y.sMin);
        }

        // I don't know why this isn't in Long...
        private  int compare(int a, int b) {
            if(a>b){
                return 1;
            }else if(a<b){
                return -1;
            }else{
                return 0;
            }

        }
    }


}
