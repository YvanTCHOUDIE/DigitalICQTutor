package com.yvantchoudie.digitalicqtutor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ICQQuestionListActivity extends AbstractActivity {


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDbRoot;

    private EditText mTxtICQTitle;
    private Spinner mSpRightAnswer;

    private String mIcqID;
    private String mIcqTitle;
    private ICQ mICQ;
    private int mIcqNb_questions;
    private String mUserProfile;
    private TextView mTvBackToICQListLabel;
    private ImageView mIvBackToICQListLabel;
    private TextView mTvSaveLabel;
    private ImageView mIvSaveLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.icq_question_list_activity_menu, menu);

        MenuItem insertMenu = menu.findItem(R.id.insert_icq_question_menu);
        if (mIcqID == null || mIcqID.equals("") || mIcqTitle == null || mIcqTitle.equals("")) {
            insertMenu.setVisible(false);
        } else {
            insertMenu.setVisible(true);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {


            case R.id.icq_save_menu_item:
                if (saveICQ()) {
                    Toast.makeText(this, "MCQ saved", Toast.LENGTH_LONG).show();
                    clean();
                    backToList();
                }
                return true;

            case R.id.insert_icq_question_menu:

                Intent intent = new Intent(ICQQuestionListActivity.this, ICQQuestionEditActivity.class);

                intent.putExtra(ICQ.ICQ_ID, mIcqID);
                intent.putExtra(ICQ.ICQ_TITLE, mIcqTitle);
                intent.putExtra(User.TUTOR_ID, super.getTutorId());
                intent.putExtra(ICQ.ICQ_NB_QUESTIONS, mIcqNb_questions);

                startActivity(intent);

                return true;

            case R.id.logout_icq_question_menu:
                FirebaseUtil.getInstance().logout(ICQQuestionListActivity.this);
                return true;

            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {

        super.onPause();
        FirebaseUtil.getInstance().detachListener();

    }

    @Override
    protected void onResume() {

        super.onResume();
        setContentView(R.layout.activity_icq_question_list);
        showMenu();

        mTxtICQTitle = (EditText) findViewById(R.id.txtICQTitle);
        mTvBackToICQListLabel = (TextView) findViewById(R.id.tvBackToICQListLabel);
        mIvBackToICQListLabel = (ImageView) findViewById(R.id.ivBackToICQListLabel);
        mTvSaveLabel = (TextView) findViewById(R.id.tvSaveLabel);
        mIvSaveLabel = (ImageView) findViewById(R.id.ivSaveLabel);

        mTvBackToICQListLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ICQQuestionListActivity.this, ICQListActivity.class);

                intent.putExtra(User.USER_PROFILE, mUserProfile);

                startActivity(intent);

            }
        });

        mIvBackToICQListLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ICQQuestionListActivity.this, ICQListActivity.class);

                intent.putExtra(User.USER_PROFILE, mUserProfile);

                startActivity(intent);

            }
        });

        mTvSaveLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (saveICQ()) {
                    Toast.makeText(view.getContext(), "MCQ saved", Toast.LENGTH_LONG).show();
                    clean();
                    backToList();
                }
                return;

            }
        });

        mIvSaveLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (saveICQ()) {
                    Toast.makeText(view.getContext(), "MCQ saved", Toast.LENGTH_LONG).show();
                    clean();
                    backToList();
                }
                return;

            }
        });


        Intent intent = getIntent();
        String userProfile = intent.getStringExtra(User.USER_PROFILE);

        if (userProfile != null && userProfile.equals(User.USER_PROFILE_TUTOR)) {
            mUserProfile = userProfile;
        }

        mDbRoot = FirebaseUtil.getInstance().openFirebaseReference("digitalicqtutor", this).getDatabaseReference();
        continueOnResume();
        FirebaseUtil.getInstance().attachListener();

    }

    @Override
    protected void continueOnResume() {

        if (super.getTutorId() == null || super.getTutorId().equals("")) {

            if (FirebaseUtil.getInstance().checkIfSignedIn(this)) {
                super.setTutorId(FirebaseUtil.getInstance().synchronize(this).getUserId());
            }
        }

        if (super.getTutorId() == null || super.getTutorId().equals("")) {

            Log.d("Tutor details status", "**********Tutor details missed, ICQQuestionListActivity**********");

        } else {

            Intent intent = getIntent();
            String icqID = intent.getStringExtra(ICQ.ICQ_ID);
            String icqTitle = intent.getStringExtra(ICQ.ICQ_TITLE);
            int icqNb_questions = intent.getIntExtra(ICQ.ICQ_NB_QUESTIONS, ICQ.ICQ_NB_QUESTIONS_DEFAULT);


            FirebaseUtil.getInstance().openChild("icq", this).openChild(super.getTutorId(), this);
            mFirebaseDatabase = FirebaseUtil.getInstance().getFirebaseDatabase();
            mDatabaseReference = FirebaseUtil.getInstance().getDatabaseReference();

            if (icqID != null && icqTitle != null && icqNb_questions != ICQ.ICQ_NB_QUESTIONS_DEFAULT) {

                mIcqID = icqID;
                mIcqTitle = icqTitle;
                mIcqNb_questions = icqNb_questions;

                mICQ = new ICQ(mIcqID, mIcqTitle, super.getTutorId(), mIcqNb_questions);
                mTxtICQTitle.setText(mIcqTitle);

                showMenu();


                FirebaseUtil.getInstance().openChild(getIcqID(), this);

                RecyclerView rvICQQuestions = (RecyclerView) findViewById(R.id.rvICQQuestions);
                final ICQQuestionAdapter icqQuestionAdapter = new ICQQuestionAdapter(mIcqID, mIcqTitle, super.getTutorId(), mIcqNb_questions);
                rvICQQuestions.setAdapter(icqQuestionAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                rvICQQuestions.setLayoutManager(linearLayoutManager);

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FirebaseUtil.getInstance().RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Log.d("Auth : ", "User logged in");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                super.setTutorId(FirebaseUtil.getInstance().synchronize(this).getUserId());
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_LONG).show();
                continueOnResume();

            } else {
                // Sign in failed. If response is null the user canceled the
                Toast.makeText(this, "Login failed. You must login to continue!", Toast.LENGTH_LONG).show();
                Intent backIntent = new Intent(ICQQuestionListActivity.this, DigitalICQTutorActivity.class);
                startActivity(backIntent);
            }
        }
    }

    public void showMenu() {

        invalidateOptionsMenu();
    }

    private boolean saveICQ() {

        if (mICQ == null) {
            mICQ = new ICQ();
            mICQ.setAuthor(mTutorId); //HERE HERE
        }

        if (mTxtICQTitle.getText().toString().equals("")) {

            mTxtICQTitle.requestFocus();
            Toast.makeText(this, "Please provide the MCQ title", Toast.LENGTH_SHORT).show();
            return false;
        }

        mICQ.setTitle(mTxtICQTitle.getText().toString());
        mICQ.normalize();

        if (mICQ.getId() == null || mICQ.getId().equals("")) {

            mICQ.setNb_questions(0);

            ICQSummary icqSummary = new ICQSummary(mICQ);

            String newIcqID = mDatabaseReference.push().getKey();
            mDatabaseReference.child(newIcqID).setValue(mICQ);

            icqSummary.setIcq_id(newIcqID);

            DatabaseReference tmpDBref = mDbRoot.child("icq_codes").child(newIcqID);
            tmpDBref.setValue(icqSummary);

            mIcqID = newIcqID;

        } else {
            /*Avoid the below, because the below, when over-write the object,
            will not re-write the subject (various icqquestions), because the java object does not
            necessarily contains the various sub-objects
             */
            // mDatabaseReference.child(mICQ.getId()).setValue(mICQ);
            //*/

            //Rather consider the below
            mDatabaseReference.child(mICQ.getId()).child("title").setValue(mICQ.getTitle());
            mIcqID = mICQ.getId();
        }

        mIcqTitle = mICQ.getTitle();
        mIcqNb_questions = mICQ.getNb_questions();
        return true;
    }

    private boolean deleteICQQuestion() {

        if (mICQ != null && mICQ.getId() != null) {
            mDatabaseReference.child(mICQ.getId()).removeValue();
            return true;
        } else {
            Toast.makeText(this, "You cannot delete an unsaved MCQ", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
    private void backToList() {

        //Intent intent = new Intent(ICQQuestionListActivity.this, ICQQuestionListActivity.class);
        Intent intent = new Intent(ICQQuestionListActivity.this, ICQListActivity.class);

        intent.putExtra(User.USER_PROFILE, mUserProfile);

        intent.putExtra(ICQ.ICQ_ID, mIcqID);
        intent.putExtra(ICQ.ICQ_TITLE, mIcqTitle);
        intent.putExtra(User.TUTOR_ID, super.getTutorId());
        intent.putExtra(ICQ.ICQ_NB_QUESTIONS, mIcqNb_questions);

        startActivity(intent);
    }

    private void clean() {

        mTxtICQTitle.setText("");
        mTxtICQTitle.requestFocus();
    }


    public String getIcqID() {
        return mIcqID;
    }

    public void setIcqID(String icqID) {
        this.mIcqID = icqID;
    }

    public String getIcqTitle() {
        return mIcqTitle;
    }

    public void setIcqTitle(String icqTitle) {
        this.mIcqTitle = icqTitle;
    }
}