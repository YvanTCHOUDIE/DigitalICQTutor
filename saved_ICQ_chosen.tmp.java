package com.yvantchoudie.digitalicqtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InfoMessageActivity extends AppCompatActivity {

    private String mUserProfile;
    private Button mButtonNEXT;
    private String mIcqCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_message);
    }

    @Override
    protected void onResume() {

        super.onResume();
        setContentView(R.layout.activity_digital_icq_tutor);

        Intent intent = getIntent();
        String icqCODE = intent.getStringExtra(User.ICQ_CODE);
        mUserProfile = User.USER_PROFILE_STUDENT;
        if (icqCODE != null) {
            mIcqCode = icqCODE;
        }


        mButtonNEXT = findViewById(R.id.btNext);

        mButtonNEXT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mUserProfile = User.USER_PROFILE_STUDENT;
                Intent intent = new Intent(InfoMessageActivity.this, ChoosingICQActivity.class);
                intent.putExtra(User.USER_PROFILE, mUserProfile);
                intent.putExtra(User.ICQ_CODE, mIcqCode);
                startActivity(intent);

            }

        });

    }
}
