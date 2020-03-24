package com.inhascp.partyhere.NewMeeting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inhascp.partyhere.Meeting;
import com.inhascp.partyhere.R;
import com.inhascp.partyhere.User;
import com.inhascp.partyhere.login.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputTypeActivity extends AppCompatActivity {



    private String USER_KEY;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CheckBox mTypeStudyCheckBox;//값 1
    private ImageButton mTypeStudyImgBtn;
    private LinearLayout mCheckboxStudy;

    private CheckBox mTypeTalkCheckBox;//값 2
    private ImageButton mTypeTalkImgBtn;
    private LinearLayout mCheckboxTalk;

    private CheckBox mTypeEat;//값 3
    private ImageButton mTypeEatImgBtn;
    private LinearLayout mCheckboxEat;

    private CheckBox mTypeDrink;//값 4
    private ImageButton mTypeDrinkImgBtn;
    private LinearLayout mCheckboxDrink;

    private CheckBox mTypeActivity;//값 5
    private ImageButton mTypeActivityImgBtn;
    private LinearLayout mCheckboxActivity;

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



        mTypeStudyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mTypeStudyCheckBox.setTextColor(Color.parseColor("#FFFFFF"));
                    mTypeStudyCheckBox.setBackgroundColor(Color.parseColor("#0741AD"));
                    mTypeStudyImgBtn.setImageResource(R.drawable.study_icon_changed);
                    mCheckboxStudy.setBackgroundResource(R.drawable.round_checkbox_checked);

                }
                if(!isChecked){
                    mTypeStudyCheckBox.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTypeStudyCheckBox.setTextColor(Color.parseColor("#000000"));
                    mTypeStudyImgBtn.setImageResource(R.drawable.study_icon);
                    mCheckboxStudy.setBackgroundResource(R.drawable.round_checkbox);
                }
            }
        });
        mTypeTalkCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mTypeTalkCheckBox.setTextColor(Color.parseColor("#FFFFFF"));
                    mTypeTalkCheckBox.setBackgroundColor(Color.parseColor("#0741AD"));
                    mTypeTalkImgBtn.setImageResource(R.drawable.talk_icon_changed);
                    mCheckboxTalk.setBackgroundResource(R.drawable.round_checkbox_checked);
                }
                if(!isChecked){
                    mTypeTalkCheckBox.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTypeTalkCheckBox.setTextColor(Color.parseColor("#000000"));
                    mTypeTalkImgBtn.setImageResource(R.drawable.talk_icon);
                    mCheckboxTalk.setBackgroundResource(R.drawable.round_checkbox);
                }
            }
        });
        mTypeEat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mTypeEat.setTextColor(Color.parseColor("#FFFFFF"));
                    mTypeEat.setBackgroundColor(Color.parseColor("#0741AD"));
                    mTypeEatImgBtn.setImageResource(R.drawable.eat_icon_changed);
                    mCheckboxEat.setBackgroundResource(R.drawable.round_checkbox_checked);
                }
                if(!isChecked){
                    mTypeEat.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTypeEat.setTextColor(Color.parseColor("#000000"));
                    mTypeEatImgBtn.setImageResource(R.drawable.eat_icon);
                    mCheckboxEat.setBackgroundResource(R.drawable.round_checkbox);
                }
            }
        });
        mTypeDrink.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mTypeDrink.setTextColor(Color.parseColor("#FFFFFF"));
                    mTypeDrink.setBackgroundColor(Color.parseColor("#0741AD"));
                    mTypeDrinkImgBtn.setImageResource(R.drawable.drink_icon_changed);
                    mCheckboxDrink.setBackgroundResource(R.drawable.round_checkbox_checked);
                }
                if(!isChecked){
                    mTypeDrink.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTypeDrink.setTextColor(Color.parseColor("#000000"));
                    mTypeDrinkImgBtn.setImageResource(R.drawable.drink_icon);
                    mCheckboxDrink.setBackgroundResource(R.drawable.round_checkbox);
                }
            }
        });
        mTypeActivity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mTypeActivity.setTextColor(Color.parseColor("#FFFFFF"));
                    mTypeActivity.setBackgroundColor(Color.parseColor("#0741AD"));
                    mTypeActivityImgBtn.setImageResource(R.drawable.activity_icon_changed);
                    mCheckboxActivity.setBackgroundResource(R.drawable.round_checkbox_checked);
                }
                if(!isChecked){
                    mTypeActivity.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTypeActivity.setTextColor(Color.parseColor("#000000"));
                    mTypeActivityImgBtn.setImageResource(R.drawable.activity_icon);
                    mCheckboxActivity.setBackgroundResource(R.drawable.round_checkbox);

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
                Meeting meeting = new Meeting(mType, mMemberKeys, mMemberKeyPlace, new HashMap<String, String>(), mMemberKeyName, new HashMap<String, List<Integer>>());
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
        mUserPlace = getIntent().getStringExtra("PLACE");
        USER_KEY = LoginActivity.USER_KEY;
        mTypeStudyCheckBox = findViewById(R.id.activity_new_meeting_cb_study);
        mTypeStudyImgBtn = findViewById(R.id.activity_new_meeting_imgbtn_study);
        mCheckboxStudy = findViewById(R.id.checkbox_study);

        mTypeTalkCheckBox = findViewById(R.id.activity_new_meeting_cb_talk);
        mTypeTalkImgBtn = findViewById(R.id.activity_new_meeting_imgbtn_talk);
        mCheckboxTalk = findViewById(R.id.checkbox_talk);

        mTypeEat = findViewById(R.id.activity_new_meeting_cb_eat);
        mTypeEatImgBtn = findViewById(R.id.activity_new_meeting_imgbtn_eat);
        mCheckboxEat= findViewById(R.id.checkbox_eat);

        mTypeDrink = findViewById(R.id.activity_new_meeting_cb_drink);
        mTypeDrinkImgBtn = findViewById(R.id.activity_new_meeting_imgbtn_drink);
        mCheckboxDrink = findViewById(R.id.checkbox_drink);

        mTypeActivity = findViewById(R.id.activity_new_meeting_cb_activity);
        mTypeActivityImgBtn = findViewById(R.id.activity_new_meeting_imgbtn_activity);
        mCheckboxActivity = findViewById(R.id.checkbox_activity);

        mMake = findViewById(R.id.activity_new_meeting_btn_make);
    }
}
