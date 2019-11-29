package com.inhascp.partyhere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ExistMeetingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView mParticipant;
    private TextView mRecommend;
    private TextView mType;
    private Meeting meeting;
    private String mMeetingKey;
    private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_meeting);

        init();
        mMeetingKey = "u0LiRsDwiU0UsEIsr5hV";
        mIntent = getIntent();
        mMeetingKey = mIntent.getStringExtra("MEETING_KEY");
        db.collection("Meeting").document(mMeetingKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    meeting = snapshot.toObject(Meeting.class);
                    String memberKey = meeting.getMemberKeys().get(0);
                    mParticipant.setText(meeting.getMembers().get(memberKey));
                    mRecommend.setText(meeting.getRecommendPlace().get("인하대"));
                    mType.setText(meeting.getMeetingType().get(0));
                }
                else {
                }
            }
        });
    }

    protected void init(){
        mParticipant = findViewById(R.id.activity_exist_meeting_textview_participant);
        mRecommend = findViewById(R.id.activity_exist_meeting_textview_recommend);
        mType = findViewById(R.id.activity_exist_meeting_textview_type);
        db = FirebaseFirestore.getInstance();
    }
}
