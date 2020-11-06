package com.yvantchoudie.digitalicqtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DigitalICQTutorActivity extends AppCompatActivity {

    private String mProfile;
    private Button mButtonStudent;
    private Button mButtonTutor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {

        super.onResume();
        setContentView(R.layout.activity_digital_icq_tutor);

        mButtonStudent = findViewById(R.id.btStudent);
        mButtonTutor = findViewById(R.id.btTutor);

        mButtonStudent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mProfile = User.USER_PROFILE_STUDENT;
                Intent intent = new Intent(DigitalICQTutorActivity.this, InfoMessageActivity.class);
                intent.putExtra(User.USER_PROFILE, mProfile);
                startActivity(intent);

            }

        });

        mButtonTutor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mProfile = User.USER_PROFILE_TUTOR;

                Intent intent = new Intent(DigitalICQTutorActivity.this, ICQListActivity.class);

                intent.putExtra(User.USER_PROFILE, mProfile);

                startActivity(intent);

            }

        });

    }
}