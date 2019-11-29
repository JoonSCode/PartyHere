package com.inhascp.partyhere;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewMeetingActivity extends AppCompatActivity {


    static final String KEY = "NueasP51ZCXmqkhcY60E";

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CheckBox mTypeStudy;//값 1
    private CheckBox mTypeTalk;//값 2
    private CheckBox mTypeEat;//값 3
    private CheckBox mTypeDrink;//값 4
    private CheckBox mTypeActivity;//값 5
    private Button mMake;
    private Context mContext;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meeting);

        init();

        mMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<String> mType = new ArrayList<>();
                List<String> mMemberKeys = new ArrayList<>();
                HashMap<String,String> mMembers = new HashMap<>();

                if(mTypeStudy.isChecked())
                    mType.add(mTypeStudy.getText().toString());
                if(mTypeTalk.isChecked())
                    mType.add(mTypeTalk.getText().toString());
                if(mTypeEat.isChecked())
                    mType.add(mTypeEat.getText().toString());
                if(mTypeDrink.isChecked())
                    mType.add(mTypeDrink.getText().toString());
                if(mTypeActivity.isChecked())
                    mType.add(mTypeActivity.getText().toString());

                mMemberKeys.add(KEY);
                mMembers.put(KEY,"");
                Meeting meeting = new Meeting(mType, mMemberKeys, mMembers, new HashMap<String, String>());
                String MeetingKey = db.collection("Meeting").document().getId();
                db.collection("Meeting").document(MeetingKey).set(meeting);
                DocumentReference User = db.collection("User").document(KEY);
                User.update("MeetingKeys", FieldValue.arrayUnion(MeetingKey));

                intent = new Intent(mContext, ExistMeetingActivity.class);
                intent.putExtra("MEETING_KEY", MeetingKey);
                startActivity(intent);
                finish();
            }
        });



    }
    protected void init(){
        mTypeStudy = findViewById(R.id.activity_new_meeting_cb_study);
        mTypeTalk = findViewById(R.id.activity_new_meeting_cb_talk);
        mTypeEat = findViewById(R.id.activity_new_meeting_cb_eat);
        mTypeDrink = findViewById(R.id.activity_new_meeting_cb_drink);
        mTypeActivity = findViewById(R.id.activity_new_meeting_cb_activity);
        mMake = findViewById(R.id.activity_new_meeting_btn_make);
        mContext = this;
    }
}
