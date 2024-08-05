package com.aceroute.mobile.software.component.reference;

import java.util.HashMap;

/**
 * Created by xelium on 9/12/16.
 */
public class CustHistoryTokenGroup {

    HashMap<Long, CustHistoryToken> custHistTokenGrp;

    public HashMap<Long, CustHistoryToken> getCustHistTokenGrp() {
        return custHistTokenGrp;
    }

    public void setCustHistTokenGrp(HashMap<Long, CustHistoryToken> custHistTokenGrp) {
        this.custHistTokenGrp = custHistTokenGrp;
    }

}
