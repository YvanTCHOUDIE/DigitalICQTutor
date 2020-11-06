package com.yvantchoudie.digitalicqtutor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.InputQueue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ICQEvaluationActivity extends AbstractActivity {


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDbRoot;

    private TextView mTvICQTitle;
    private Spinner mSpRightAnswer;

    private String mIcqID;
    private String mIcqTitle;
    private ICQ mICQ;
    private int mIcqNb_questions;
    private String mUserProfile;
    private TextView mTvBackToChoosingICQLabel;
    private ImageView mIvBackToChoosingICQLabel;
    private TextView mTvSubmitLabel;
    private ImageView mIvSubmitLabel;
    private String mUserId;
    private ICQEvaluationAdapter mIcqEvaluationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.icq_question_evaluation_activity_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.icq_submit_menu_item:
                if (submitICQ()) {
                    //Toast.makeText(this, "MCQ submitted", Toast.LENGTH_LONG).show();
                    clean();
                    backToICQChoosing();
                }
                return true;

            case R.id.logout_icq_question_menu:
                FirebaseUtil.getInstance().logout(ICQEvaluationActivity.this);
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
        setContentView(R.layout.activity_icq_evaluation);
        showMenu();

        mTvICQTitle = (TextView) findViewById(R.id.tvICQTitle);
        mTvBackToChoosingICQLabel = (TextView) findViewById(R.id.tvBackToChoosingICQLabel);
        mIvBackToChoosingICQLabel = (ImageView) findViewById(R.id.ivBackToChoosingICQLabel);
        mTvSubmitLabel = (TextView) findViewById(R.id.tvSubmitLabel);
        mIvSubmitLabel = (ImageView) findViewById(R.id.ivSubmitLabel);

        mTvBackToChoosingICQLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ICQEvaluationActivity.this, ChoosingICQActivity.class);

                intent.putExtra(User.USER_PROFILE, mUserProfile);

                startActivity(intent);

            }
        });

        mIvBackToChoosingICQLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ICQEvaluationActivity.this, ChoosingICQActivity.class);

                intent.putExtra(User.USER_PROFILE, mUserProfile);

                startActivity(intent);

            }
        });

        mTvSubmitLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (submitICQ()) {
                    //Toast.makeText(view.getContext(), "MCQ Submitted", Toast.LENGTH_LONG).show();
                    clean();
                    backToICQChoosing();
                }
                return;

            }
        });

        mIvSubmitLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (submitICQ()) {
                    //Toast.makeText(view.getContext(), "MCQ submitted", Toast.LENGTH_LONG).show();
                    clean();
                    backToICQChoosing();
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


        if (this.getUserId() == null || this.getUserId().equals("")) {

            if (FirebaseUtil.getInstance().checkIfSignedIn(this)) {
                this.setUserId(FirebaseUtil.getInstance().synchronize(this).getUserId());
            }
        }
            Intent intent = getIntent();
            String icqID = intent.getStringExtra(ICQ.ICQ_ID);
            String icqTitle = intent.getStringExtra(ICQ.ICQ_TITLE);
            String tutorId = intent.getStringExtra(User.TUTOR_ID);

            int icqNb_questions = intent.getIntExtra(ICQ.ICQ_NB_QUESTIONS, ICQ.ICQ_NB_QUESTIONS_DEFAULT);

            if (icqID != null && icqTitle != null && icqNb_questions != ICQ.ICQ_NB_QUESTIONS_DEFAULT && !(tutorId == null)) {

                mIcqID = icqID;
                mIcqTitle = icqTitle;
                mIcqNb_questions = icqNb_questions;
                super.setTutorId(tutorId);

                FirebaseUtil.getInstance().openChild("icq", this).openChild(super.getTutorId(), this);
                mFirebaseDatabase = FirebaseUtil.getInstance().getFirebaseDatabase();
                mDatabaseReference = FirebaseUtil.getInstance().getDatabaseReference();

                mICQ = new ICQ(mIcqID, mIcqTitle, super.getTutorId(), mIcqNb_questions);
                mTvICQTitle.setText(mIcqTitle);

                showMenu();

                FirebaseUtil.getInstance().openChild(getIcqID(), this);

                RecyclerView rvICQEvaluation = (RecyclerView) findViewById(R.id.rvICQEvaluation);
                mIcqEvaluationAdapter = new ICQEvaluationAdapter(mIcqID, mIcqTitle, super.getTutorId(), mIcqNb_questions);
                rvICQEvaluation.setAdapter(mIcqEvaluationAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                rvICQEvaluation.setLayoutManager(linearLayoutManager);




            } else {

                if (FirebaseUtil.getInstance().checkIfSignedIn(this)) {
                    Intent backIntent = new Intent(ICQEvaluationActivity.this, ChoosingICQActivity.class);

                    if (mUserProfile == null) { mUserProfile = User.USER_PROFILE_STUDENT; }

                    backIntent.putExtra(User.USER_PROFILE, mUserProfile);

                    startActivity(intent);
                }
            }

    }

    private void synchronize() {


        this.setUserId(FirebaseUtil.getInstance().synchronize(this).getUserId());

        if (mDbRoot == null) {
            mDbRoot = FirebaseUtil.getInstance().openFirebaseReference("digitalicqtutor", this).getDatabaseReference();
        }

        mDbRoot.child("users").child("students").child(this.getUserId()).child("email").setValue(FirebaseUtil.getInstance().synchronize(this).getUserEmail());

        Toast.makeText(this, "Welcome back!", Toast.LENGTH_LONG).show();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        super.setTutorId(FirebaseUtil.getInstance().synchronize(this).getUserId());
        Toast.makeText(this, "Welcome back!", Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FirebaseUtil.getInstance().RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Log.d("Auth : ", "User logged in");
                synchronize();
                continueOnResume();

            } else {
                // Sign in failed. If response is null the user canceled the
                Toast.makeText(this, "Login failed. You must login to continue!", Toast.LENGTH_LONG).show();
                Intent backIntent = new Intent(ICQEvaluationActivity.this, DigitalICQTutorActivity.class);
                startActivity(backIntent);
            }
        }
    }

    public void showMenu() {

        invalidateOptionsMenu();
    }

    private boolean submitICQ() {

        if (mICQ == null) {
            mICQ = new ICQ();
            mICQ.setAuthor(mTutorId); //HERE HERE
        }

        mICQ.setTitle(mTvICQTitle.getText().toString());
        mICQ.normalize();

        if (mICQ.getId() != null && !(mICQ.getId().toString().equals(""))){

            /*Avoid the below, because the below, when over-write the object,
            will not re-write the subject (various icqquestions), because the java object does not
            necessarily contains the various sub-objects
             */
            // mDatabaseReference.child(mICQ.getId()).setValue(mICQ);
            //*/

            //Rather consider the below
            //mDatabaseReference.child(mICQ.getId()).child("title").setValue(mICQ.getTitle());
            mIcqID = mICQ.getId();

            int nbQuestions = 0;
            int nbPoints = 0;
            if (mIcqEvaluationAdapter != null) {

                if (mIcqEvaluationAdapter.getICQResponsesPoints() != null) {

                    if (mIcqEvaluationAdapter.getICQResponsesPoints().size() > 0) {

                        nbQuestions = mIcqEvaluationAdapter.getICQResponsesPoints().size();
                        for (int counter = 0; counter < nbQuestions; counter++) {
                            nbPoints += mIcqEvaluationAdapter.getICQResponsesPoints().get(counter);
                        }


                        double scoreOver20 = ((double)Math.round((((double)nbPoints)*20) / ((double)nbQuestions) * 10000)) / 10000;
                        Toast.makeText(ICQEvaluationActivity.this, "Your score: " + String.valueOf(nbPoints) + "/" + String.valueOf(nbQuestions), Toast.LENGTH_LONG).show();

                        LayoutInflater layoutInflater = (LayoutInflater) ICQEvaluationActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = layoutInflater.inflate(R.layout.popup_icq_score, null);

                        PopupWindow popUp = new PopupWindow(
                                popupView,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );

                        popupView.setClickable(true);
                        popupView.setFocusableInTouchMode(true);
                        popUp.setOutsideTouchable(true);

                        TextView tvICQScoreTitlePopupTextView = (TextView) popupView.findViewById(R.id.tvICQScoreTitlePopupTextView);
                        TextView tvICQScoreLabelPopupTextView = (TextView) popupView.findViewById(R.id.tvICQScoreLabelPopupTextView);
                        TextView tvICQScoreValuePopupTextView = (TextView) popupView.findViewById(R.id.tvICQScoreValuePopupTextView);
                        TextView tvICQScoreInfoPopupTextView = (TextView) popupView.findViewById(R.id.tvICQScoreInfoPopupTextView);
                        ImageView ivICQScoreClosePopupImageView = (ImageView) popupView.findViewById(R.id.ivICQScoreClosePopupImageView);

                        ivICQScoreClosePopupImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popUp.dismiss();
                            }
                        });

                        tvICQScoreTitlePopupTextView.setText(mTvICQTitle.getText());
                        tvICQScoreValuePopupTextView.setText(String.valueOf(scoreOver20) + "/20");
                        tvICQScoreInfoPopupTextView.setText("You found " + nbPoints + " correct answers, over a total of " + nbQuestions);

                        popUp.showAsDropDown(ICQEvaluationActivity.this.mTvICQTitle, 0, 0);

                    }
                }

            }
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
    private void backToICQChoosing() {
        //Intent intent = new Intent(ICQEvaluationActivity.this, ICQEvaluationActivity.class);
        /*
        Intent intent = new Intent(ICQEvaluationActivity.this, ChoosingICQActivity.class);

        intent.putExtra(User.USER_PROFILE, mUserProfile);

        intent.putExtra(ICQ.ICQ_ID, mIcqID);
        intent.putExtra(ICQ.ICQ_TITLE, mIcqTitle);
        intent.putExtra(User.TUTOR_ID, super.getTutorId());
        intent.putExtra(ICQ.ICQ_NB_QUESTIONS, mIcqNb_questions);

        startActivity(intent);
        */
    }

    private void clean() {

        //mTvICQTitle.setText("");
        //mTvICQTitle.requestFocus();
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

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }
}