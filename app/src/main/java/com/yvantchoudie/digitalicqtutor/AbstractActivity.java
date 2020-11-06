package com.yvantchoudie.digitalicqtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public abstract class AbstractActivity extends AppCompatActivity {

    protected String mTutorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public String getTutorId() {
        return mTutorId;
    }

    public void setTutorId(String tutorId) {
        mTutorId = tutorId;
    }

    protected abstract void continueOnResume();
}