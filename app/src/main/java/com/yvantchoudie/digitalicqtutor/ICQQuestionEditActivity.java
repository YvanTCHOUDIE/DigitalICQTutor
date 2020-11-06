package com.yvantchoudie.digitalicqtutor;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ICQQuestionEditActivity extends AbstractActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDbRoot;

    private EditText mTxtICQQuestion;
    private EditText mTxtOptionA;
    private EditText mTxtOptionB;
    private EditText mTxtOptionC;
    private EditText mTxtOptionD;
    private Spinner mSpRightAnswer;

    private ICQQuestion mICQQuestion;
    private String mIcqID;
    private String mIcqTitle;
    private int mIcqNb_questions;
    private String mUserProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.icq_question_edit_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.icq_question_save_menu_item:
                if (saveICQQuestion()) {
                    Toast.makeText(this, "MCQ Question saved", Toast.LENGTH_LONG).show();
                    clean();
                    backToList();
                }
                return true;
            case R.id.icq_question_delete_menu_item:
                if(deleteICQQuestion()) {
                    Toast.makeText(this, "MCQ Question deleted", Toast.LENGTH_LONG).show();
                    clean();
                    backToList();
                }
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onResume() {

        super.onResume();
        setContentView(R.layout.activity_icq_question_edit);

        mTxtICQQuestion = (EditText) findViewById(R.id.txtICQQuestion);
        mTxtOptionA = (EditText) findViewById(R.id.txtOptionA);
        mTxtOptionB = (EditText) findViewById(R.id.txtOptionB);
        mTxtOptionC = (EditText) findViewById(R.id.txtOptionC);
        mTxtOptionD = (EditText) findViewById(R.id.txtOptionD);
        mSpRightAnswer = (Spinner) findViewById(R.id.spRightAnswer);

        Intent intent = getIntent();
        String userProfile = intent.getStringExtra(User.USER_PROFILE);

        if (userProfile != null && userProfile.equals(User.USER_PROFILE_TUTOR)) {
            setUserProfile(userProfile);
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

            Log.d("Tutor details status", "**********Tutor details missed, ICQQuestionEditActivity**********");

        } else {

            Intent intent = getIntent();
            String icqID = intent.getStringExtra(ICQ.ICQ_ID);
            String icqTitle = intent.getStringExtra(ICQ.ICQ_TITLE);
            int icqNb_questions = intent.getIntExtra(ICQ.ICQ_NB_QUESTIONS, ICQ.ICQ_NB_QUESTIONS_DEFAULT);

            ICQQuestion icqQuestion = (ICQQuestion) intent.getSerializableExtra(ICQQuestion.ICQ_QUESTION_OBJECT);

            int rightAnswerPosition = 0;

            if (icqQuestion != null  && icqNb_questions != ICQ.ICQ_NB_QUESTIONS_DEFAULT) {

                mIcqID = icqQuestion.getIcqID();
                mIcqTitle =  icqQuestion.getIcqTitle();
                mIcqNb_questions = icqNb_questions;
                mICQQuestion = icqQuestion;


                mTxtICQQuestion.setText(mICQQuestion.getQuestion());
                mTxtOptionA.setText(mICQQuestion.getOptionA());
                mTxtOptionB.setText(mICQQuestion.getOptionB());
                mTxtOptionC.setText(mICQQuestion.getOptionC());
                mTxtOptionD.setText(mICQQuestion.getOptionD());

                rightAnswerPosition = mICQQuestion.normailze().getRightAnswerPosition();


            } else if (icqID != null && icqTitle != null  && icqNb_questions != ICQ.ICQ_NB_QUESTIONS_DEFAULT) {

                mIcqID = icqID;
                mIcqTitle =  icqTitle;
                mIcqNb_questions = icqNb_questions;

                rightAnswerPosition = 0;

                mICQQuestion = new ICQQuestion();
                mICQQuestion.setIcqID(mIcqID);
                mICQQuestion.setIcqTitle(mIcqTitle);
                mICQQuestion.setTutorID(mTutorId);

            }

            if (getIcqID() != null && getIcqTitle() != null) {

                FirebaseUtil.getInstance().openChild("icq", this).openChild(super.getTutorId(), this).openChild(getIcqID(), this);
                mFirebaseDatabase = FirebaseUtil.getInstance().getFirebaseDatabase();
                mDatabaseReference = FirebaseUtil.getInstance().getDatabaseReference();

                List<String> icqQuestionOptions = initializeICQQuestionOptions();
                ArrayAdapter<String> adapterICQquestionOptions =
                        new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, icqQuestionOptions);
                adapterICQquestionOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpRightAnswer.setAdapter(adapterICQquestionOptions);

                mSpRightAnswer.setSelection(rightAnswerPosition);

            }

        }

    }

    private boolean saveICQQuestion() {

        if (mICQQuestion == null) {
            mICQQuestion = new ICQQuestion();
            mICQQuestion.setIcqID(mIcqID);
            mICQQuestion.setIcqTitle(mIcqTitle);
            mICQQuestion.setTutorID(mTutorId);
        }

        if (mTxtICQQuestion.getText().toString().equals("")) {

            mTxtICQQuestion.requestFocus();
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;

        } else if (mTxtOptionA.getText().toString().equals("")) {

            mTxtOptionA.requestFocus();
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;

        }  else if (mTxtOptionB.getText().toString().equals("")) {

            mTxtOptionB.requestFocus();
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mTxtOptionC.getText().toString().equals("")) {

            mTxtOptionC.requestFocus();
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mTxtOptionD.getText().toString().equals("")) {

            mTxtOptionD.requestFocus();
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        mICQQuestion.setQuestion(mTxtICQQuestion.getText().toString());
        mICQQuestion.setOptionA(mTxtOptionA.getText().toString());
        mICQQuestion.setOptionB(mTxtOptionB.getText().toString());
        mICQQuestion.setOptionC(mTxtOptionC.getText().toString());
        mICQQuestion.setOptionD(mTxtOptionD.getText().toString());
        mICQQuestion.setRightAnswer((String)mSpRightAnswer.getSelectedItem());
        mICQQuestion.normailze();

        if (mICQQuestion.getId() == null || mICQQuestion.getId().equals("")) {

            ICQQuestionSummary icqQuestionSummary = new ICQQuestionSummary(mICQQuestion);

            String newIcqQuestionID = mDatabaseReference.push().getKey();
            mDatabaseReference.child(newIcqQuestionID).setValue(mICQQuestion);
            mDatabaseReference.child("nb_questions").setValue(mIcqNb_questions + 1);

            icqQuestionSummary.setIcqquestion_id(newIcqQuestionID);

            DatabaseReference tmpDBref = mDbRoot.child("icqquestions").child(newIcqQuestionID);
            tmpDBref.setValue(icqQuestionSummary);
        } else {
            mDatabaseReference.child(mICQQuestion.getId()).setValue(mICQQuestion);
        }
        return true;
    }

    private boolean deleteICQQuestion() {

        if (mICQQuestion != null && mICQQuestion.getId() != null) {
            mDatabaseReference.child(mICQQuestion.getId()).removeValue();
            mDatabaseReference.child("nb_questions").setValue(mIcqNb_questions - 1);
            return true;
        } else {
            Toast.makeText(this, "You cannot delete an unsaved Question", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
    private void backToList() {

        Intent intent = new Intent(ICQQuestionEditActivity.this, ICQQuestionListActivity.class);

        intent.putExtra(ICQ.ICQ_ID, mIcqID);
        intent.putExtra(ICQ.ICQ_TITLE, mIcqTitle);
        intent.putExtra(User.TUTOR_ID, super.getTutorId());
        intent.putExtra(ICQ.ICQ_NB_QUESTIONS, mIcqNb_questions);

        startActivity(intent);
    }

    private void clean() {

        mTxtICQQuestion.setText("");
        mTxtOptionA.setText("");
        mTxtOptionB.setText("");
        mTxtOptionC.setText("");
        mTxtOptionD.setText("");

        mTxtICQQuestion.requestFocus();
    }

    private List<String> initializeICQQuestionOptions() {

        List<String> icqQuestionOptions = new ArrayList<>();

        icqQuestionOptions.add("Option A");
        icqQuestionOptions.add("Option B");
        icqQuestionOptions.add("Option C");
        icqQuestionOptions.add("Option D");

        return icqQuestionOptions;
    }

    public String getIcqID() {
        return mIcqID;
    }

    public void setIcqID(String icqID) {
        mIcqID = icqID;
    }

    public String getIcqTitle() {
        return mIcqTitle;
    }

    public void setIcqTitle(String icqTitle) {
        mIcqTitle = icqTitle;
    }

    public String getUserProfile() {
        return mUserProfile;
    }

    public void setUserProfile(String userProfile) {
        mUserProfile = userProfile;
    }
}