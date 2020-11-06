package com.yvantchoudie.digitalicqtutor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    private static FirebaseDatabase sFirebaseDatabase;
    private static DatabaseReference sDatabaseReference;
    private static FirebaseUtil sOurInstance;
    private static FirebaseAuth sFirebaseAuth;
    private static FirebaseAuth.AuthStateListener sAuthStateListener;
    public static final int RC_SIGN_IN = 1;
    private static AbstractActivity sCallerActivity;
    private static ArrayList<ICQ> sICQs;
    private static ArrayList<ICQQuestion> sICQQuestions;
    private static String sUserId;
    private static String sUserEmail;
    private static boolean sIsTutor;


    private FirebaseUtil () {};


    public static boolean checkIfSignedIn(final AbstractActivity callerActivity) {

        if (sOurInstance == null) {
            sOurInstance = getInstance();
        }

        boolean isSignedIn = false;
        setCallerActivity(callerActivity);
        sFirebaseAuth = FirebaseAuth.getInstance();

        if (sFirebaseAuth.getCurrentUser() != null) {
            isSignedIn = true;
        }

        return isSignedIn;
    }

    public static FirebaseUtil synchronize(final AbstractActivity callerActivity) {

        if (sOurInstance == null) {
            sOurInstance = getInstance();
        }

        setCallerActivity(callerActivity);
        sFirebaseAuth = FirebaseAuth.getInstance();

        if (sFirebaseAuth.getCurrentUser() != null) {

            FirebaseUtil.setUserId(sFirebaseAuth.getUid());
            FirebaseUtil.setUserEmail(sFirebaseAuth.getCurrentUser().getEmail());
            checkTutor(sUserId);
            sICQs = new ArrayList<ICQ>();
            sICQQuestions = new ArrayList<ICQQuestion>();

        } else {

            FirebaseUtil.setUserId("");
        }

        return getInstance();
    }

    @SuppressLint("RestrictedApi")
    public static FirebaseUtil openFirebaseReference(String ref, final AbstractActivity callerActivity) {

        if (sOurInstance == null) {
            sOurInstance = getInstance();
        }

        setCallerActivity(callerActivity);
        sFirebaseAuth = FirebaseAuth.getInstance();
        sAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (sFirebaseAuth.getCurrentUser() == null) {
                    signin();
                } else {

                    FirebaseUtil.setUserId(sFirebaseAuth.getUid());
                    checkTutor(sUserId);

                }

            }
        };

        sICQs = new ArrayList<ICQ>();
        sICQQuestions = new ArrayList<ICQQuestion>();
        setDatabaseReference(getFirebaseDatabase().getReference().child(ref));
        return getInstance();
    }

    public static FirebaseUtil openChild(String childRef, final AbstractActivity callerActivity) {

        if (sOurInstance == null) {
            sOurInstance = getInstance();
        }

        setCallerActivity(callerActivity);
        sFirebaseAuth = FirebaseAuth.getInstance();

        sICQs = new ArrayList<ICQ>();
        sICQQuestions = new ArrayList<ICQQuestion>();

        if (sDatabaseReference == null) {
            FirebaseUtil.openFirebaseReference(childRef, callerActivity);
        } else {
            setDatabaseReference(sDatabaseReference.child(childRef));
        }

        return getInstance();

    }


    private static void checkTutor(String userId) {

        DatabaseReference tmpDBReference = sFirebaseDatabase.getReference().child("digitalicqtutor").child("users").child("tutors").child(userId);
        ChildEventListener tmpChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                FirebaseUtil.setIsTutor(true);
                Log.d("User profile ", "User is a tutor");
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
        };
        /*
        DatabaseReference tmpDBReference = sFirebaseDatabase.getReference().child("digitalicqtutor").child("users").child("tutors");
        tmpDBReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            if (snapshot.hasChild(sUserId)) {

                    FirebaseUtil.setIsTutor(true);
                    Log.d("User profile ", "User is a tutor");


                } else {

                    Log.d("User profile ", "User is not a tutor");
                    FirebaseUtil.setIsTutor(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //*/
    }

    private static void signin() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        String tmpId = sFirebaseAuth.getUid();
        String tmp2 = tmpId;

        // Create and launch sign-in intent
        getCallerActivity().startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN
        );
    }

    public static String getUserId() {
        return sUserId;
    }

    private static void setUserId(String userId) {
        sUserId = userId;
    }

    private static boolean isIsTutor() {
        return sIsTutor;
    }

    private static void setIsTutor(boolean isTutor) {
        sIsTutor = isTutor;
    }

    public static AbstractActivity getCallerActivity() {
        return sCallerActivity;
    }

    public static void setCallerActivity(AbstractActivity callerActivity) {
        sCallerActivity = callerActivity;
    }

    public static String getUserEmail() {
        return sUserEmail;
    }

    private static void setUserEmail(String userEmail) {
        sUserEmail = userEmail;
    }

    public void logout(Context context) {

        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {

                        Log.d("Auth : ", "User logged out");
                        //FirebaseUtil.getInstance().attachListener();
                        Intent intent = new Intent(context, DigitalICQTutorActivity.class);
                        context.startActivity(intent);
                    }
                });
        FirebaseUtil.getInstance().detachListener();

    }

    public static FirebaseDatabase getFirebaseDatabase() {

        if (sOurInstance == null) {
            sOurInstance = getInstance();
        }
        return sFirebaseDatabase;
    }

    private static void setFirebaseDatabase(FirebaseDatabase firebaseDatabase) {
        sFirebaseDatabase = firebaseDatabase;
    }

    public static DatabaseReference getDatabaseReference() {

        if (sOurInstance == null) {
            sOurInstance = getInstance();
        }

        return sDatabaseReference;
    }

    private static void setDatabaseReference(DatabaseReference databaseReference) {
        sDatabaseReference = databaseReference;
    }

    public static void attachListener() {
        sFirebaseAuth.addAuthStateListener(sAuthStateListener);
    }
    public static void detachListener() {
        sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
    }


    public static FirebaseUtil getInstance() {

        if (sOurInstance == null) {

            setInstance(new FirebaseUtil());
            setFirebaseDatabase(FirebaseDatabase.getInstance());
            setICQs(new ArrayList<ICQ>());
            setICQQuestions(new ArrayList<ICQQuestion>());
        }

        return sOurInstance;
    }

    public static FirebaseUtil resetInstance() {

        setInstance(new FirebaseUtil());
        setFirebaseDatabase(FirebaseDatabase.getInstance());
        setICQs(new ArrayList<ICQ>());
        setICQQuestions(new ArrayList<ICQQuestion>());

        return sOurInstance;
    }

    private static void setInstance(FirebaseUtil firebaseUtil) {
        sOurInstance = firebaseUtil;
    }

    public static ArrayList<ICQ> getICQs() {
        return sICQs;
    }

    private static void setICQs(ArrayList<ICQ> ICQs) {
        sICQs = ICQs;
    }

    public static ArrayList<ICQQuestion> getICQQuestions() {
        return sICQQuestions;
    }

    public static void setICQQuestions(ArrayList<ICQQuestion> ICQQuestions) {
        sICQQuestions = ICQQuestions;
    }


    public static FirebaseAuth getFirebaseAuth() {
        return sFirebaseAuth;
    }

    private static void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        sFirebaseAuth = firebaseAuth;
    }

    public static FirebaseAuth.AuthStateListener getAuthStateListener() {
        return sAuthStateListener;
    }

    private static void setAuthStateListener(FirebaseAuth.AuthStateListener authStateListener) {
        sAuthStateListener = authStateListener;
    }

}
