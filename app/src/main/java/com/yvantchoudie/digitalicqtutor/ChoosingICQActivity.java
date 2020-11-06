package com.yvantchoudie.digitalicqtutor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ChoosingICQActivity extends AbstractActivity {

    private DatabaseReference mDbRoot;

    private EditText mTxtStudentICQCode;
    private Button mbtStudentConnect;


    private String mUserId;
    private String mUserProfile;
    private String mICQCode;
    private boolean mICQFound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.choosing_icq_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logout_choosing_icq_menu:
                FirebaseUtil.getInstance().logout(ChoosingICQActivity.this);
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
        setContentView(R.layout.activity_choosing_icq);
        mTxtStudentICQCode = (EditText) findViewById(R.id.txtStudentICQCode);
        mbtStudentConnect = (Button) findViewById(R.id.btStudentConnect);

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
                synchronize();
            }
        }

        if (this.getUserId() == null || this.getUserId().equals("")) {

            Log.d("Student details status", "**********Student details missed**********");

        } else {

            mbtStudentConnect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    String icqCode = mTxtStudentICQCode.getText().toString();

                    if (icqCode.equals("")) {

                        mTxtStudentICQCode.requestFocus();
                        Toast.makeText(view.getContext(), "Please provide the ICQ code", Toast.LENGTH_SHORT).show();
                        return;

                    } else {

                        mICQCode = icqCode;
                        displayICQIfFound();
                    }

                }
            });

        }

    }


    private boolean displayICQIfFound() {

        /*/ // HERE HERE template

        Log.d("Student ID status", "**********Student ID successfully initialized**********");
        FirebaseUtil.getInstance().openChild("icq", this).openChild(super.getTutorId(), this);
        RecyclerView rvICQ = (RecyclerView) findViewById(R.id.rvICQs);
        final ICQAdapter icqAdapter = new ICQAdapter(super.getTutorId());
        rvICQ.setAdapter(icqAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvICQ.setLayoutManager(linearLayoutManager);
        registerForContextMenu(rvICQ);

        //*/ // HERE HERE template


        //FirebaseUtil.getInstance().openChild("icq", this).openChild(super.getTutorId(), this);
        //FirebaseUtil.getInstance().getDatabaseReference().orderByChild("icq").equalTo(icqCode);
        //FirebaseUtil.getInstance().openFirebaseReference("digitalicqtutor", this).openChild("icq_codes", this).getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
        FirebaseUtil.getInstance().openFirebaseReference("digitalicqtutor", ChoosingICQActivity.this).openChild("icq_codes", ChoosingICQActivity.this).getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot searchSnapshot) {

                if (searchSnapshot.hasChild(mICQCode)) {

                    //FirebaseUtil.getInstance().getDatabaseReference().orderByChild("icq").equalTo(icqCode);
                    //mDbRoot.orderByChild("icq_codes").equalTo(mICQCode).getRef().addChildEventListener(new ChildEventListener() {
                    mDbRoot.child("icq_codes").orderByChild("icq_id").equalTo(mICQCode).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot foundSnapshot, @Nullable String previousChildName) {

                            Log.d("searchSnapshot ", "key: " + foundSnapshot.getKey() + " val: " + foundSnapshot.getValue());

                            IcqCode icqCode = foundSnapshot.getValue(IcqCode.class);

                            ChoosingICQActivity.super.setTutorId(icqCode.getTutor_id());

                            ChoosingICQActivity.this.mICQFound = true;
                            Log.d("MCQ search ", "Provided MCQ id " + icqCode.getIcq_id() + " found with tutor id " + ChoosingICQActivity.super.getTutorId());

                            //Toast.makeText(ChoosingICQActivity.this, "MCQ found", Toast.LENGTH_SHORT).show();

                            mDbRoot.child("icq_codes").orderByChild("icq_id").equalTo(mICQCode).removeEventListener(this);

                            mDbRoot.child("icq").child(ChoosingICQActivity.super.getTutorId()).orderByKey().equalTo(icqCode.getIcq_id()).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot icqSnapshot, @Nullable String previousChildName) {

                                    Log.d("icqSnapshot ", "key: " + icqSnapshot.getKey() + " val: " + icqSnapshot.getValue());

                                    try {

                                        ICQ icq = icqSnapshot.getValue(ICQ.class);

                                        if (icq.getTitle() != null && !icq.getTitle().equals("")) {

                                            icq.setId(icqSnapshot.getKey());
                                            icq.normalize();

                                            Intent intent = new Intent(ChoosingICQActivity.this, ICQEvaluationActivity.class);

                                            intent.putExtra(User.USER_PROFILE, mUserProfile);
                                            intent.putExtra(ICQ.ICQ_ID, icq.getId()); //intent.putExtra(ICQ.ICQ_ID, ChoosingICQActivity.this.mICQCode)
                                            intent.putExtra(ICQ.ICQ_TITLE, icq.getTitle());
                                            intent.putExtra(User.TUTOR_ID, icq.getAuthor());
                                            intent.putExtra(ICQ.ICQ_NB_QUESTIONS, icq.getNb_questions());

                                            startActivity(intent);

                                        }

                                    } catch (DatabaseException dbE) {

                                        dbE.printStackTrace();

                                    } catch (NullPointerException npE) {

                                        npE.printStackTrace();

                                    } finally {

                                    }

                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {

                    Log.d("MCQ search ", "MCQ not found");
                    Toast.makeText(ChoosingICQActivity.this, "The ICQ code is not correct", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return true;
    }


    private void synchronize() {

        //super.setTutorId(FirebaseUtil.getInstance().synchronize(this).getUserId());

        this.setUserId(FirebaseUtil.getInstance().synchronize(this).getUserId());

        if (mDbRoot == null) {
            mDbRoot = FirebaseUtil.getInstance().openFirebaseReference("digitalicqtutor", this).getDatabaseReference();
        }

        mDbRoot.child("users").child("students").child(this.getUserId()).child("email").setValue(FirebaseUtil.getInstance().synchronize(this).getUserEmail());

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

                /*
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.getEmail();
                //*/
                synchronize();
                continueOnResume();

            } else {
                // Sign in failed. If response is null the user canceled the
                Toast.makeText(this, "Login failed. You must login to continue!", Toast.LENGTH_LONG).show();
                Intent backIntent = new Intent(ChoosingICQActivity.this, DigitalICQTutorActivity.class);
                startActivity(backIntent);
            }
        }
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }
}