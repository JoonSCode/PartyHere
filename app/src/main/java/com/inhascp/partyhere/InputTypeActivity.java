package com.inhascp.partyhere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputTypeActivity extends AppCompatActivity {



    private String USER_KEY;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CheckBox mTypeStudyCheckBox;//값 1
    private CheckBox mTypeTalkCheckBox;//값 2
    private CheckBox mTypeEat;//값 3
    private CheckBox mTypeDrink;//값 4
    private CheckBox mTypeActivity;//값 5
    private Button mMake;
    private Intent intent;
    private String mUserName;
    private String mUserPlace;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_type);

        init();

        db.collection("User").document(USER_KEY).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = document.toObject(User.class);
                        mUserName = user.getNickName();
                    } else {
                    }
                } else {
                }
            }
        });

        mMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<String> mType = new ArrayList<>();
                List<String> mMemberKeys = new ArrayList<>();
                HashMap<String,String> mMemberKeyName = new HashMap<>();
                HashMap<String,String> mMemberKeyPlace = new HashMap<>();

                //체크 박스를 통해 모임 성격 값을 준다.
                if(mTypeStudyCheckBox.isChecked())
                    mType.add(mTypeStudyCheckBox.getText().toString());
                if(mTypeTalkCheckBox.isChecked())
                    mType.add(mTypeTalkCheckBox.getText().toString());
                if(mTypeEat.isChecked())
                    mType.add(mTypeEat.getText().toString());
                if(mTypeDrink.isChecked())
                    mType.add(mTypeDrink.getText().toString());
                if(mTypeActivity.isChecked())
                    mType.add(mTypeActivity.getText().toString());

                //meeting 객체의 요소들을 만듬
                mMemberKeys.add(USER_KEY);
                mMemberKeyPlace.put(USER_KEY,mUserPlace);
                mMemberKeyName.put(USER_KEY, mUserName);

                //meeting 객체를 만들어 db에 추가
                Meeting meeting = new Meeting(mType, mMemberKeys, mMemberKeyPlace, new HashMap<String, String>(), mMemberKeyName);
                String MeetingKey = db.collection("Meeting").document().getId();
                db.collection("Meeting").document(MeetingKey).set(meeting);

                //user 객체의 요소들을 받아 업데이트 한 후 재 업로드
                DocumentReference docRefUser = db.collection("User").document(USER_KEY);
                HashMap<String, Object> updates = new HashMap<>();

                HashMap<String,String> meetingTitle = user.getMeetingTitle();
                meetingTitle.put(MeetingKey,mUserName);

                updates.put("meetingTitle", meetingTitle);
                updates.put("meetingKeys", FieldValue.arrayUnion(MeetingKey));

                docRefUser.update(updates);

                //만든 방 공유 화면으로 넘어간다.
                intent = new Intent(getApplicationContext(), ShareMeetingActivity.class);
                intent.putExtra("MEETING_KEY", MeetingKey);
                intent.putExtra("USER_KEY", USER_KEY);
                startActivity(intent);
                finish();
            }
        });



    }
    protected void init(){
        mUserPlace = getIntent().getStringExtra("Place");
        USER_KEY = getIntent().getStringExtra("USER_KEY");
        mTypeStudyCheckBox = findViewById(R.id.activity_new_meeting_cb_study);
        mTypeTalkCheckBox = findViewById(R.id.activity_new_meeting_cb_talk);
        mTypeEat = findViewById(R.id.activity_new_meeting_cb_eat);
        mTypeDrink = findViewById(R.id.activity_new_meeting_cb_drink);
        mTypeActivity = findViewById(R.id.activity_new_meeting_cb_activity);
        mMake = findViewById(R.id.activity_new_meeting_btn_make);
    }
}
