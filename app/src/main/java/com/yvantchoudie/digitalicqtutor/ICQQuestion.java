package com.yvantchoudie.digitalicqtutor;

import android.util.Log;

import java.io.Serializable;

public class ICQQuestion implements Serializable {

    private String mId;
    private String mQuestion;
    private String mOptionA;
    private String mOptionB;
    private String mOptionC;
    private String mOptionD;
    private String mRightAnswer;
    private String mIcqID;
    private String mIcqTitle;
    private String mTutorID;
    private int mRightAnswerPosition;

    public static final String ICQ_QUESTION_ID = "com.yvantchoudie.digitalicqtutor.ICQQuestion.ICQ_QUESTION_ID";
    public static final String ICQ_QUESTION_TITLE = "com.yvantchoudie.digitalicqtutor.ICQQuestion.ICQ_QUESTION_TITLE";
    public static final String ICQ_QUESTION_OBJECT = "com.yvantchoudie.digitalicqtutor.ICQQuestion.ICQ_QUESTION_QUESTION";

    public ICQQuestion() {

        super();

    }

    public ICQQuestion(String question, String optionA, String optionB, String optionC, String optionD, String rightAnswer) {

        //setId(id);
        setQuestion(question);
        setOptionA(optionA);
        setOptionB(optionB);
        setOptionC(optionC);
        setOptionD(optionD);
        setRightAnswer(rightAnswer);
    }


    public ICQQuestion(String id, String question, String optionA, String optionB, String optionC, String optionD, String rightAnswer) {

        setId(id);
        setQuestion(question);
        setOptionA(optionA);
        setOptionB(optionB);
        setOptionC(optionC);
        setOptionD(optionD);
        setRightAnswer(rightAnswer);
    }
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String question) {
        mQuestion = question;
    }

    public String getOptionA() {
        return mOptionA;
    }

    public void setOptionA(String optionA) {
        mOptionA = optionA;
    }

    public String getOptionB() {
        return mOptionB;
    }

    public void setOptionB(String optionB) {
        mOptionB = optionB;
    }

    public String getOptionC() {
        return mOptionC;
    }

    public void setOptionC(String optionC) {
        mOptionC = optionC;
    }

    public String getOptionD() { return mOptionD; }

    public void setOptionD(String optionD) { mOptionD = optionD; }

    public String getRightAnswer() { return mRightAnswer; }

    public void setRightAnswer(String rightAnswer) { mRightAnswer = rightAnswer; }

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

    public String getTutorID() {
        return mTutorID;
    }

    public void setTutorID(String tutorID) {
        mTutorID = tutorID;
    }

    public int getRightAnswerPosition() {
        return mRightAnswerPosition;
    }

    public void setRightAnswerPosition(int rightAnswerPosition) {
        mRightAnswerPosition = rightAnswerPosition;
    }

    public ICQQuestion normailze() {

        try {

            switch (mRightAnswer) {

                case "Option A":
                    mRightAnswerPosition = 0;
                    Log.d("debug**position ", "AAAAAA");
                    break;

                case "Option B":
                    mRightAnswerPosition = 1;
                    Log.d("debug**position ", "BBBBBB");
                    break;

                case "Option C":
                    mRightAnswerPosition = 2;
                    Log.d("debug**position ", "CCCCC");
                    break;

                case "Option D":
                    mRightAnswerPosition = 3;
                    Log.d("debug**position ", "DDDDDD");
                    break;
                default:
                    Log.d("debug**position", "NOTHING");
                    break;
            }

        }  catch(NullPointerException nPE) {

            nPE.printStackTrace();

        }

        return this;
    }

}