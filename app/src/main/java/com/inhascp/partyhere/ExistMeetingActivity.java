package com.inhascp.partyhere;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

public class ExistMeetingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView mParticipant;
    private TextView mRecommend;
    private TextView mType;
    private Meeting meeting;
    private String mMeetingKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_meeting);

        init();
        mMeetingKey = "u0LiRsDwiU0UsEIsr5hV";
        db.collection("Meeting").document(mMeetingKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                meeting = documentSnapshot.toObject(Meeting.class);
                String memberKey = meeting.getMemberKeys().get(0);
                mParticipant.setText(meeting.getMembers().get(memberKey));
                mRecommend.setText(meeting.getRecommendPlace().get("인하대"));
                mType.setText(meeting.getMeetingType().get(0));
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
