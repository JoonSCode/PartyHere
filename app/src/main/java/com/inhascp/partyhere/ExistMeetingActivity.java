package com.inhascp.partyhere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;


public class ExistMeetingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView mTvParticipantName;
    private TextView mTvParticipantPlace;
    private TextView mTvRecommend;
    private TextView mTvType;

    private Meeting meeting;
    private User user;


    private Button mBtnLeave;
    private String mMeetingKey;
    private String mUserKey;
    private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_meeting);

        if(InputPlaceActivity.activity != null){
            InputPlaceActivity activity = InputPlaceActivity.activity;
            activity.finish();
        }

        init();
        mIntent = getIntent();
        if(mIntent.getData() != null){//링크를 눌러서 접속한것   여기에 로그인 여부 확인후 돌아오기 처리해야함 intent에서 setresult인가 이거 쓰면 될듯
            Uri uri = mIntent.getData();
            mMeetingKey = uri.getQueryParameter("MEETING_KEY");
            mUserKey = "NueasP51ZCXmqkhcY60E";
            System.out.println("링크를 통합 접속");
        }
        else {//그냥 내 모임 목록에서 선택한것
            System.out.println("링크 눌러서 접속");
            mMeetingKey = mIntent.getStringExtra("MEETING_KEY");
            mUserKey = mIntent.getStringExtra("USER_KEY");
        }

        getMeeting(mMeetingKey);
        getUser(mUserKey);
//모임 삭제 기능
        mBtnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference docRef = db.collection("Meeting").document(mMeetingKey);//모임 목록에서 삭제
                HashMap<String,Object> updates = new HashMap<>();

                HashMap<String,String> memberKeyName = meeting.getMemberKeyName();
                HashMap<String,String> memberKeyPlace = meeting.getMemberKeyPlace();
                //모임의 keyname값을 hashmap으로 받아오고 내 정보를 지운 후 size가 0 이면 db에서 모임을 제거함
                memberKeyName.remove(mUserKey);
                memberKeyPlace.remove(mUserKey);
                if(memberKeyName.size() == 0)
                    docRef.delete();
                else {//size 0아닐 경우 갱신함
                    updates.put("memberKeys", FieldValue.arrayRemove(mUserKey));
                    updates.put("memberKeyName", memberKeyName);
                    updates.put("memberKeyPlace", memberKeyPlace);
                    docRef.update(updates);
                    updates.clear();
                }
                //user 업데이트
                docRef = db.collection("User").document(mUserKey);

                HashMap<String,String> meetingTitle = user.getMeetingTitle();
                meetingTitle.remove(mMeetingKey);

                updates.put("meetingKeys", FieldValue.arrayRemove(mMeetingKey));
                updates.put("meetingTitle", meetingTitle);

                docRef.update(updates);

                finish();
            }
        });

    }
    public void changePosition(View v) {
        Intent intent = new Intent(getApplicationContext(), ChangePosition.class);

        startActivity(intent);
    }

    protected void init(){
        mTvParticipantName = findViewById(R.id.activity_exist_meeting_textview_participant_name);
        mTvParticipantPlace = findViewById(R.id.activity_exist_meeting_textview_participant_place);
        mTvRecommend = findViewById(R.id.activity_exist_meeting_textview_recommend);
        mTvType = findViewById(R.id.activity_exist_meeting_textview_type);
        mBtnLeave = findViewById(R.id.activity_exist_meeting_btn_leave);
        db = FirebaseFirestore.getInstance();
    }

    protected void getMeeting(String mMeetingKey){
        db.collection("Meeting").document(mMeetingKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    meeting = snapshot.toObject(Meeting.class);

                    String participant_name = "";
                    String participant_place ="";
                    String types = "";

                    for(int i = 0;i < meeting.getMemberKeys().size(); i++) {
                        String memberKey = meeting.getMemberKeys().get(i);
                        participant_name +=  meeting.getMemberKeyName().get(memberKey);
                        participant_place += meeting.getMemberKeyPlace().get(memberKey);
                    }
                    mTvParticipantName.setText(participant_name);
                    mTvParticipantPlace.setText(participant_place);
                    mTvRecommend.setText(meeting.getRecommendPlace().get("인하대"));

                    for(String type:meeting.getMeetingType()){
                        types += type;
                    }

                    mTvType.setText(types);
                }
                else {
                }
            }
        });
    }

    protected  void getUser(String mUserKey){
        db.collection("User").document(mUserKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = document.toObject(User.class);
                    } else {
                    }
                } else {
                }
            }
        });
    }

}
