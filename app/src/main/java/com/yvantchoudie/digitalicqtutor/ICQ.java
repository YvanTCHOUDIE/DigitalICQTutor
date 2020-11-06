package com.yvantchoudie.digitalicqtutor;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class ICQ implements Serializable {

    private String mId;
    private String mTitle;
    private String mAuthor;
    private String mDescription;
    private int mNb_questions;
    private ArrayList<ICQQuestion>  mICQQuestions;

    public static final String ICQ_ID = "com.yvantchoudie.digitalicqtutor.ICQ.ICQ_ID";
    public static final String ICQ_TITLE = "com.yvantchoudie.digitalicqtutor.ICQ.ICQ_TITLE";
    public static final String COPIED_ICQ_CODE_ON_CLIPBOARD = "com.yvantchoudie.digitalicqtutor.ICQ.COPIED_ICQ_CODE_ON_CLIPBOARD";
    public static final String ICQ_NB_QUESTIONS = "com.yvantchoudie.digitalicqtutor.ICQ.NB_QUESTIONS_CODE_ON_CLIPBOARD";
    public static final int ICQ_NB_QUESTIONS_DEFAULT = -1;

    public ICQ() {
        super();
        mNb_questions = ICQ.ICQ_NB_QUESTIONS_DEFAULT;
    }

    public ICQ(String title, String author, int nb_questions) {

        setTitle(title);
        setAuthor(author);
        setDescription();
        setNb_questions(nb_questions);
    }
    public ICQ(String id, String title, String author, int nb_questions) {

        setId(id);
        setTitle(title);
        setAuthor(author);
        setDescription();
        setNb_questions(nb_questions);
    }
    public ICQ(String title, String author, int nb_questions, ArrayList<ICQQuestion>  icqQuestions) {

        setTitle(title);
        setAuthor(author);
        setICQQuestions(icqQuestions);
        setDescription();
        setNb_questions(nb_questions);
    }

    public ICQ(String id, String title, String author, int nb_questions, ArrayList<ICQQuestion>  icqQuestions) {

        setId(id);
        setTitle(title);
        setAuthor(author);
        setICQQuestions(icqQuestions);
        setDescription();
        setNb_questions(nb_questions);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {

        mAuthor = author;
    }

    public int getNb_questions() {

        if (mNb_questions != ICQ.ICQ_NB_QUESTIONS_DEFAULT) {
            return mNb_questions;
        } else {
            return 0;
        }

    }

    public void setNb_questions(int nb_questions) {
        this.mNb_questions = nb_questions;
    }

    public ArrayList<ICQQuestion> getICQQuestions() {
        return mICQQuestions;
    }

    public void setICQQuestions(ArrayList<ICQQuestion> ICQQuestions) {
        mICQQuestions = ICQQuestions;
    }

    public String getDescription() {
        return mDescription;
    }

    private void setDescription() {

        mDescription = mTitle + ", ICQ by the author with id : " + mAuthor;

    }

    public void normalize() {

        setDescription();

        if (getICQQuestions() != null) {
            setNb_questions(getICQQuestions().size());
        }

    }

}