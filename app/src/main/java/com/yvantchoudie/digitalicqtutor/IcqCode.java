package com.yvantchoudie.digitalicqtutor;

public class IcqCode {

    private String mIcq_id;
    private String mTutor_id;

    public IcqCode() {

    }

    public IcqCode(String icq_id, String tutor_id) {
        setIcq_id(icq_id);
        setTutor_id(tutor_id);
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
