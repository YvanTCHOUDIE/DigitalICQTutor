package com.yvantchoudie.digitalicqtutor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
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

public class ICQEvaluationAdapter extends RecyclerView.Adapter<ICQEvaluationAdapter.ICQQuestionViewHolder>{

    private ArrayList<ICQQuestion> mICQQuestions;
    private ArrayList<Integer> mICQResponsesPoints;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private String mIcqID;
    private String mIcqTitle;
    private String mTutorID;
    private int mIcqNb_questions;

    public ICQEvaluationAdapter(String icqID, String icqTitle, String tutorID, int icqNb_questions) {

        mFirebaseDatabase = FirebaseUtil.getInstance().getFirebaseDatabase();
        mDatabaseReference = FirebaseUtil.getInstance().getDatabaseReference();
        mICQQuestions = FirebaseUtil.getInstance().getICQQuestions();
        setICQResponsesPoints(new ArrayList<Integer>());
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
                        getICQResponsesPoints().add(0);
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
                inflate(R.layout.rv_row_icq_evaluate, parent, false);
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

    public ArrayList<Integer> getICQResponsesPoints() {
        return mICQResponsesPoints;
    }

    public void setICQResponsesPoints(ArrayList<Integer> ICQResponsesPoints) {
        mICQResponsesPoints = ICQResponsesPoints;
    }

    public class ICQQuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTvICQEvaluateTitle;
        private RadioButton mRb_optionA;
        private RadioButton mRb_optionB;
        private RadioButton mRb_optionC;
        private RadioButton mRb_optionD;

        private String mRightAnswerOption;

        private TextView mTvICQQuestionNbOptions;

        public ICQQuestionViewHolder(@NonNull View itemView) {

            super(itemView);
            mTvICQEvaluateTitle = (TextView) itemView.findViewById(R.id.tvICQEvaluateTitle);
            mRb_optionA = (RadioButton) itemView.findViewById(R.id.rb_optionA);
            mRb_optionB = (RadioButton) itemView.findViewById(R.id.rb_optionB);
            mRb_optionC = (RadioButton) itemView.findViewById(R.id.rb_optionC);
            mRb_optionD = (RadioButton) itemView.findViewById(R.id.rb_optionD);

            //mTvICQQuestionNbOptions = (TextView) itemView.findViewById(R.id.tvICQQuestionNbOptions);
            itemView.setOnClickListener(this);

        }


        public void bindIcqQuestion(ICQQuestion icqQuestion) {

            mTvICQEvaluateTitle.setText(icqQuestion.getQuestion());

            mRb_optionA.setText(icqQuestion.getOptionA());
            mRb_optionB.setText(icqQuestion.getOptionB());
            mRb_optionC.setText(icqQuestion.getOptionC());
            mRb_optionD.setText(icqQuestion.getOptionD());

            mRightAnswerOption = icqQuestion.getRightAnswer();

            mRb_optionA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (mRightAnswerOption.toString().equals("Option A")) {
                        ICQEvaluationAdapter.this.getICQResponsesPoints().set(position, 1);
                    } else {
                        ICQEvaluationAdapter.this.getICQResponsesPoints().set(position, 0);
                    }
                }
            });

            mRb_optionB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (mRightAnswerOption.toString().equals("Option B")) {
                        ICQEvaluationAdapter.this.getICQResponsesPoints().set(position, 1);
                    } else {
                        ICQEvaluationAdapter.this.getICQResponsesPoints().set(position, 0);
                    }
                }
            });

            mRb_optionC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (mRightAnswerOption.toString().equals("Option C")) {
                        ICQEvaluationAdapter.this.getICQResponsesPoints().set(position, 1);
                    } else {
                        ICQEvaluationAdapter.this.getICQResponsesPoints().set(position, 0);
                    }
                }
            });

            mRb_optionD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (mRightAnswerOption.toString().equals("Option D")) {
                        ICQEvaluationAdapter.this.getICQResponsesPoints().set(position, 1);
                    } else {
                        ICQEvaluationAdapter.this.getICQResponsesPoints().set(position, 0);
                    }
                }
            });

        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            //HERE HERE//Log.d("ICQ Question click: ", String.valueOf(position));
            //HERE HERE//ICQQuestion selectedICQQuestion = mICQQuestions.get(position);
            //HERE HERE//Intent intent = new Intent(view.getContext(), ICQQuestionEditActivity.class);

            /*
            intent.putExtra(ICQQuestion.ICQ_QUESTION_ID, selectedICQQuestion.getIcqID());
            intent.putExtra(ICQ.ICQ_ID, selectedICQQuestion.getIcqID());
            intent.putExtra(ICQ.ICQ_TITLE, selectedICQQuestion.getIcqTitle());
            intent.putExtra(User.TUTOR_ID, selectedICQQuestion.getTutorID());
            //*/

            //HERE HERE//intent.putExtra(ICQQuestion.ICQ_QUESTION_OBJECT, selectedICQQuestion);
            //HERE HERE//intent.putExtra(ICQ.ICQ_NB_QUESTIONS, mIcqNb_questions);
            //HERE HERE//view.getContext().startActivity(intent);

        }
    }

}
