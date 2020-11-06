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
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ICQListActivity extends AbstractActivity {


    private String mUserProfile;
    private DatabaseReference mDbRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.icq_list_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.insert_icq_menu:

                Intent intent = new Intent(ICQListActivity.this, ICQQuestionListActivity.class);

                intent.putExtra(User.TUTOR_ID, super.getTutorId());

                startActivity(intent);

                return true;

            case R.id.logout_icq_menu:
                FirebaseUtil.getInstance().logout(ICQListActivity.this);
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
        setContentView(R.layout.activity_icq_list);

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
                synchronize();
            }
        }

        if (super.getTutorId() == null || super.getTutorId().equals("")) {

            Log.d("Tutor details status", "**********Tutor details missed**********");

        } else {

            Log.d("Tutor ID status", "**********Tutor ID successfully initialized**********");
            FirebaseUtil.getInstance().openChild("icq", this).openChild(super.getTutorId(), this);
            RecyclerView rvICQ = (RecyclerView) findViewById(R.id.rvICQs);
            final ICQAdapter icqAdapter = new ICQAdapter(super.getTutorId());
            rvICQ.setAdapter(icqAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rvICQ.setLayoutManager(linearLayoutManager);
            registerForContextMenu(rvICQ);

        }

    }

    private void synchronize() {

        super.setTutorId(FirebaseUtil.getInstance().synchronize(this).getUserId());
        if (mDbRoot == null) {
            mDbRoot = FirebaseUtil.getInstance().openFirebaseReference("digitalicqtutor", this).getDatabaseReference();
        }
        mDbRoot.child("users").child("tutors").child(super.getTutorId()).child("email").setValue(FirebaseUtil.getInstance().synchronize(this).getUserEmail());
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
                Intent backIntent = new Intent(ICQListActivity.this, DigitalICQTutorActivity.class);
                startActivity(backIntent);
            }
        }
    }
}