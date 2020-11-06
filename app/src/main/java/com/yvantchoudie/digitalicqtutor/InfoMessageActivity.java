package com.yvantchoudie.digitalicqtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InfoMessageActivity extends AppCompatActivity {

    private String mUserProfile;
    private Button mButtonNEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {

        super.onResume();
        setContentView(R.layout.activity_info_message);

        Intent intent = getIntent();
        String userProfile = intent.getStringExtra(User.USER_PROFILE);
        mUserProfile = User.USER_PROFILE_STUDENT;
        if (userProfile != null) {
            mUserProfile = userProfile;
        } else {
            mUserProfile = User.USER_PROFILE_STUDENT;
        }


        mButtonNEXT = findViewById(R.id.btNext);

        mButtonNEXT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mUserProfile = User.USER_PROFILE_STUDENT;

                Intent intent = new Intent(InfoMessageActivity.this, ChoosingICQActivity.class);

                intent.putExtra(User.USER_PROFILE, mUserProfile);

                startActivity(intent);

            }

        });

    }
}