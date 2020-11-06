package com.yvantchoudie.digitalicqtutor;

import java.io.Serializable;

public class ICQSummary implements Serializable {

    private String mIcq_id;
    private String mTutor_id;

    public ICQSummary(ICQ icq) {

        super();

        setIcq_id(icq.getId() );
        setTutor_id(icq.getAuthor());
    }


    public String getIcq_id() {
        return mIcq_id;
    }

    public void setIcq_id(String icq_id) {
        mIcq_id = icq_id;
    }

    public String getTutor_id() {
        return mTutor_id;
    }

    public void setTutor_id(String tutor_id) {
        mTutor_id = tutor_id;
    }
}