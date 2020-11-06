package com.yvantchoudie.digitalicqtutor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ICQQuestionAdapter extends RecyclerView.Adapter<ICQQuestionAdapter.ICQQuestionViewHolder>{

    private ArrayList<ICQQuestion> mICQQuestions;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private String mIcqID;
    private String mIcqTitle;
    private String mTutorID;
    private int mIcqNb_questions;

    public ICQQuestionAdapter(String icqID, String icqTitle, String tutorID, int icqNb_questions) {

        mFirebaseDatabase = FirebaseUtil.getInstance().getFirebaseDatabase();
        mDatabaseReference = FirebaseUtil.getInstance().getDatabaseReference();
        mICQQuestions = FirebaseUtil.getInstance().getICQQuestions();
        mIcqID = icqID;
        mIcqTitle = icqTitle;
        mTutorID = tutorID;
        mIcqNb_questions = icqNb_questions;

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                try {

                    ICQQuestion icqQuestion = snapshot.getValue(ICQQuestion.class);
                    Log.d("ICQQuestion Loading:  ", icqQuestion.getQuestion());

                    if (icqQuestion.getQuestion() != null && !icqQuestion.getQuestion().equals("")) {

                        icqQuestion.setId(snapshot.getKey());
                        icqQuestion.setIcqID(mIcqID);
                        icqQuestion.setIcqTitle(mIcqTitle);
                        icqQuestion.setTutorID(mTutorID);
                        icqQuestion.normailze();

                        mICQQuestions.add(icqQuestion);
                        notifyItemInserted(mICQQuestions.size() - 1);
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
        };

        mDatabaseReference.addChildEventListener(mChildEventListener);

    }

    @NonNull
    @Override
    public ICQQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.rv_row_icqquestion, parent, false);
        return new ICQQuestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ICQQuestionViewHolder holder, int position) {

        ICQQuestion icqQuestion = mICQQuestions.get(position);
        holder.bindIcqQuestion(icqQuestion);
    }

    @Override
    public int getItemCount() {
        return mICQQuestions.size();
    }

    public String getTutorID() {
        return mTutorID;
    }

    public void setTutorID(String tutorID) {
        mTutorID = tutorID;
    }

    public class ICQQuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTvICQQuestionVerbiage;
        private TextView mTvICQQuestionICQ;
        private TextView mTvICQQuestionNbOptions;

        public ICQQuestionViewHolder(@NonNull View itemView) {

            super(itemView);
            mTvICQQuestionVerbiage = (TextView) itemView.findViewById(R.id.tvICQQuestionVerbiage);
            mTvICQQuestionICQ = (TextView) itemView.findViewById(R.id.tvICQQuestionICQ);
            mTvICQQuestionNbOptions = (TextView) itemView.findViewById(R.id.tvICQQuestionNbOptions);
            itemView.setOnClickListener(this);

        }

        public void bindIcqQuestion(ICQQuestion icqQuestion) {

            mTvICQQuestionVerbiage.setText(icqQuestion.getQuestion());
            mTvICQQuestionICQ.setText(String.valueOf(icqQuestion.getIcqTitle()));
            mTvICQQuestionNbOptions.setText("4 options");
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Log.d("ICQ Question click: ", String.valueOf(position));
            ICQQuestion selectedICQQuestion = mICQQuestions.get(position);
            Intent intent = new Intent(view.getContext(), ICQQuestionEditActivity.class);
            /*
            intent.putExtra(ICQQuestion.ICQ_QUESTION_ID, selectedICQQuestion.getIcqID());
            intent.putExtra(ICQ.ICQ_ID, selectedICQQuestion.getIcqID());
            intent.putExtra(ICQ.ICQ_TITLE, selectedICQQuestion.getIcqTitle());
            intent.putExtra(User.TUTOR_ID, selectedICQQuestion.getTutorID());
            //*/

            intent.putExtra(ICQQuestion.ICQ_QUESTION_OBJECT, selectedICQQuestion);
            intent.putExtra(ICQ.ICQ_NB_QUESTIONS, mIcqNb_questions);
            view.getContext().startActivity(intent);

        }
    }

}
