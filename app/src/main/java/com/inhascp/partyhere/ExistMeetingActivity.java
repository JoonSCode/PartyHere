package com.inhascp.partyhere;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ExistMeetingActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView participant;
    private TextView recommend;
    private TextView type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_meeting);

        init();

        db.collection("Meeting").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Meeting meeting = document.toObject(Meeting.class);
                        String memberKey = meeting.getMemberKeys().get(0);
                        participant.setText(meeting.getMembers().get(memberKey));
                        recommend.setText(meeting.getRecommendPlace().get("인하대"));
                        type.setText(meeting.getMeetingType());
                    }
                }
            }
        });

    }

    protected void init(){
        participant = findViewById(R.id.participant);
        recommend = findViewById(R.id.recommend);
        type = findViewById(R.id.type);
    }
}
