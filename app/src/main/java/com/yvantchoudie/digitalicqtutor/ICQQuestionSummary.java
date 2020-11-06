package com.yvantchoudie.digitalicqtutor;

import android.util.Log;

import java.io.Serializable;

public class ICQQuestionSummary implements Serializable {

    private String mIcqquestion_id;
    private String mIcq_id;
    private String mTutor_id;

    public ICQQuestionSummary(ICQQuestion icqQuestion) {

        super();

        setIcqquestion_id(icqQuestion.getId());
        setIcq_id(icqQuestion.getIcqID());
        setTutor_id(icqQuestion.getTutorID());
    }

    public String getIcqquestion_id() {
        return mIcqquestion_id;
    }

    public void setIcqquestion_id(String icqquestion_id) {
        mIcqquestion_id = icqquestion_id;
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