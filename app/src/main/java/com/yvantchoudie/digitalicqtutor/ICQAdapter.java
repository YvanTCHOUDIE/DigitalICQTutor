package com.yvantchoudie.digitalicqtutor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ICQAdapter extends RecyclerView.Adapter<ICQAdapter.ICQViewHolder>{

    private ArrayList<ICQ> mICQs;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private String mTutorID;

    private int mLongClickPosition;

    public ICQAdapter(String tutorID) {

        mFirebaseDatabase = FirebaseUtil.getInstance().getFirebaseDatabase();
        mDatabaseReference = FirebaseUtil.getInstance().getDatabaseReference();
        mICQs = FirebaseUtil.getInstance().getICQs();
        mTutorID = tutorID;

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                try {

                    ICQ icq = snapshot.getValue(ICQ.class);
                    Log.d("ICQ Loading:  ", icq.getTitle());
                    Log.d("ICQ Loading nb_q1:  ", String.valueOf(icq.getNb_questions()));

                    if (icq.getTitle() != null && !icq.getTitle().equals("")) {

                        icq.setId(snapshot.getKey());
                        icq.normalize();

                        Log.d("ICQ Loading nb_q2:  ", String.valueOf(icq.getNb_questions()));

                        mICQs.add(icq);
                        notifyItemInserted(mICQs.size() - 1);
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
    public ICQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.rv_row_icq, parent, false);
        return new ICQViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ICQViewHolder holder, int position) {

        ICQ icq = mICQs.get(position);
        holder.bindICQ(icq);

        //02nd way???
        /*
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setLongClickPosition(holder.getPosition());
                return false;
            }
        });
        //*/
    }


    public int getLongClickPosition() {
        return mLongClickPosition;
    }

    public void setLongClickPosition(int position) {
        this.mLongClickPosition = position;
    }

    @Override
    public int getItemCount() {
        return mICQs.size();
    }

    public String getTutorID() {
        return mTutorID;
    }

    public void setTutorID(String tutorID) {
        mTutorID = tutorID;
    }

    public class ICQViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        private TextView mTvICQTitle;
        private TextView mTvICQNbQuestion;
        private TextView mTvICQAuthor;

        private PopupWindow mPopUp;
        private TextView mIcqCodeTitlePopUpTextView;
        private TextView mIcqCodePopUpTextView;
        private ImageView mIcqCodeCopyPopupImageView;
        private ImageView mIcqCodeClosePopupImageView;

        @SuppressLint("ClickableViewAccessibility")
        public ICQViewHolder(@NonNull View itemView) {

            super(itemView);
            mTvICQTitle = (TextView) itemView.findViewById(R.id.tvICQTitle);
            mTvICQNbQuestion = (TextView) itemView.findViewById(R.id.tvICQNbQuestions);
            mTvICQAuthor = (TextView) itemView.findViewById(R.id.tvICQAuthor);

            LayoutInflater layoutInflater = (LayoutInflater) itemView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.popup_icq_code, null);

            //mPopUp = new PopupWindow(itemView.getContext());
            mPopUp = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            mIcqCodePopUpTextView = (TextView) popupView.findViewById(R.id.tvICQCodePopupTextView);
            mIcqCodeTitlePopUpTextView = (TextView) popupView.findViewById(R.id.tvICQCodeTitlePopupTextView);
            mIcqCodeCopyPopupImageView = (ImageView) popupView.findViewById(R.id.ivICQCodeCopyPopupImageView);
            mIcqCodeClosePopupImageView = (ImageView) popupView.findViewById(R.id.ivICQCodeClosePopupImageView);

            popupView.setClickable(true);
            popupView.setFocusableInTouchMode(true);
            mPopUp.setOutsideTouchable(true);

            popupView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
                @Override
                public boolean onGenericMotion(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        //
                    }

                    return false;
                }
            });

            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                        //
                    }

                    return false;
                }

            });

            popupView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {

                    if (!hasFocus) {
                        //
                    }
                }
            });

            mIcqCodeCopyPopupImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {

                        android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboardManager.setText(mIcqCodePopUpTextView.getText().toString());

                    } else {

                        android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        android.content.ClipData clipboardData = android.content.ClipData.newPlainText(ICQ.COPIED_ICQ_CODE_ON_CLIPBOARD, mIcqCodePopUpTextView.getText().toString());
                        clipboardManager.setPrimaryClip(clipboardData);

                    }

                }
            });

            mIcqCodeCopyPopupImageView.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            if (view.getBackground() != null) {
                                view.getBackground().setColorFilter(0xe033FF88, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            } else {

                                mIcqCodeCopyPopupImageView.getBackground().setColorFilter(0xe033FF88, PorterDuff.Mode.SRC_ATOP);
                                mIcqCodeCopyPopupImageView.invalidate();
                            }

                            break;
                        }
                        case MotionEvent.ACTION_UP: {

                            if (view.getBackground() != null) {
                                view.getBackground().clearColorFilter();
                                view.invalidate();
                            } else {

                                mIcqCodeCopyPopupImageView.getBackground().clearColorFilter();
                                mIcqCodeCopyPopupImageView.invalidate();
                            }

                            break;
                        }
                    }
                    return false;

                }
            });

            mIcqCodeClosePopupImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPopUp.dismiss();
                }
            });

            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);

        }

        public void bindICQ(ICQ icq) {

            mTvICQTitle.setText(icq.getTitle());
            mTvICQNbQuestion.setText(String.valueOf(icq.getNb_questions()) + " questions");
            //mTvICQAuthor.setText(icq.getAuthor());
        }

        @Override
        public void onClick(View view) {

            itemView.showContextMenu();
            /*
            int position = getAdapterPosition();
            Log.d("ICQ click: ", String.valueOf(position));
            ICQ selectedICQ = mICQs.get(position);
            Intent intent = new Intent(view.getContext(), ICQQuestionListActivity.class);
            intent.putExtra(ICQ.ICQ_ID, selectedICQ.getId());
            intent.putExtra(ICQ.ICQ_TITLE, selectedICQ.getTitle());
            intent.putExtra(User.TUTOR_ID, selectedICQ.getAuthor());
            view.getContext().startActivity(intent);
            //*/
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {


            Log.d("context menu ", "captured");

            contextMenu.setHeaderTitle("Select The Action");
            /*
            contextMenu.add(Menu.NONE, view.getId(), 0, "View code");//groupId, itemId, order, title
            contextMenu.add(Menu.NONE, view.getId(), 0, "Edit Question");
            //*/

            MenuItem viewCodeMenuItem = contextMenu.add(Menu.NONE, 1, 1, "View MCQ code");//groupId, itemId, order, title
            MenuItem editICQMenuItem = contextMenu.add(Menu.NONE, 2, 2, "Edit MCQ");

            viewCodeMenuItem.setOnMenuItemClickListener(onEditMenu);
            editICQMenuItem.setOnMenuItemClickListener(onEditMenu);
            int position;

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
            if (info != null) {
                position = info.position;
            } else {
                position = getAdapterPosition();
            }

            setLongClickPosition(position);
            contextMenu.setHeaderTitle("Select The Action for " + position);

        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1:
                        mPopUp.dismiss();
                        mPopUp.showAsDropDown(itemView, 0, 0);
                        Log.d("Context menu item: ", "View MCQ code menu item of the RV item num " + String.valueOf(getLongClickPosition()));
                        String icqId = mICQs.get(getLongClickPosition()).getId();
                        mIcqCodePopUpTextView.setText(icqId);
                        mIcqCodeTitlePopUpTextView.setText(mICQs.get(getLongClickPosition()).getTitle());

                        //mPopUp.showAsDropDown(itemView, 0, 0);

                        break;

                    case 2:

                        //Toast.makeText(itemView.getContext(), "Edit ICQ selected " + getLongClickPosition(),Toast.LENGTH_LONG).show();
                        Log.d("Context menu item: ", "Edit ICQ menu item of the RV item num " + String.valueOf(getLongClickPosition()));
                        ICQ selectedICQ = mICQs.get(getLongClickPosition());
                        Intent intent = new Intent(itemView.getContext(), ICQQuestionListActivity.class);

                        intent.putExtra(ICQ.ICQ_ID, selectedICQ.getId());
                        intent.putExtra(ICQ.ICQ_TITLE, selectedICQ.getTitle());
                        intent.putExtra(User.TUTOR_ID, selectedICQ.getAuthor());
                        intent.putExtra(ICQ.ICQ_NB_QUESTIONS, selectedICQ.getNb_questions());

                        itemView.getContext().startActivity(intent);

                        break;
                }
                return true;
            }
        };

        /*

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            if (item.getTitle() == "View ICQ code") {
                // do your coding
                Toast.makeText(item.getActionView().getContext(), "View ICQ code selected",Toast.LENGTH_LONG).show();
            }
            else {
                return  false;
            }
            return true;
        }

        //*/

    }

}
