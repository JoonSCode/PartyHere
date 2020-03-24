package com.inhascp.partyhere;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.inhascp.partyhere.ExistingMeeting.CalculateActivity;
import com.inhascp.partyhere.ExistingMeeting.ChangePosition;
import com.inhascp.partyhere.NewMeeting.InputPlaceActivity;
import com.inhascp.partyhere.login.LoginActivity;

import java.util.HashMap;

public class ExistMeetingActivityBackup extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView mTvParticipantName;
    private TextView mTvParticipantPlace;
    private TextView mTvRecommend;
    private TextView mTvType;
    private Button mBtnCalculateTime;
    private Meeting meeting;
    private User user;

    private Button mBtnGetLink;
    private Button mBtnLeave;
    private String mMeetingKey;
    private String mUserKey;
    private Intent mIntent;

    private BackgroundTask task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_meeting_backup);

        if (InputPlaceActivity.activity != null) {
            InputPlaceActivity activity = InputPlaceActivity.activity;
            activity.finish();
        }


        init();

        linkCheck();

    }

    //---------------------------------------------------------------------------------------------------------
    ///초기화
    protected void init() {
        mTvParticipantName = findViewById(R.id.activity_exist_meeting_textview_participant_name);
        mTvParticipantPlace = findViewById(R.id.activity_exist_meeting_textview_participant_place);
        mTvRecommend = findViewById(R.id.activity_exist_meeting_textview_recommend);
        mTvType = findViewById(R.id.activity_exist_meeting_textview_type);
        mBtnLeave = findViewById(R.id.activity_exist_meeting_btn_leave);
        db = FirebaseFirestore.getInstance();
        mBtnGetLink = findViewById(R.id.activity_exist_meeting_get_link);
        task = new BackgroundTask();
        //내 출발장소 변경 기능
        mTvParticipantPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePosition();
            }
        });

//모임 삭제 기능
        mBtnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference docRef = db.collection("Meeting").document(mMeetingKey);//모임 목록에서 삭제
                HashMap<String, Object> updates = new HashMap<>();

                HashMap<String, String> memberKeyName = meeting.getMemberKeyName();
                HashMap<String, String> memberKeyPlace = meeting.getMemberKeyPlace();
                //모임의 keyname값을 hashmap으로 받아오고 내 정보를 지운 후 size가 0 이면 db에서 모임을 제거함
                memberKeyName.remove(mUserKey);
                memberKeyPlace.remove(mUserKey);
                if (memberKeyName.size() == 0)
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
                HashMap<String, String> meetingTitle = user.getMeetingTitle();
                meetingTitle.remove(mMeetingKey);

                updates.put("meetingKeys", FieldValue.arrayRemove(mMeetingKey));
                updates.put("meetingTitle", meetingTitle);

                docRef.update(updates);

                finish();
            }
        });


        mBtnGetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateContentLink();
            }
        });


        mBtnCalculateTime = findViewById(R.id.activity_exist_meeting_btn_calculate_travel_time);
        mBtnCalculateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent = new Intent(getApplicationContext(), CalculateActivity.class);
                mIntent.putExtra("MEETING_KEY", mMeetingKey);
                mIntent.putExtra("START_POINT", meeting.getMemberKeyPlace().get(mUserKey));
                startActivity(mIntent);
            }
        });


    }

    //------------------------------------------------------------------------------------------------------
    ///장소 변경 기능
    public void changePosition() {
        Intent intent = new Intent(getApplicationContext(), ChangePosition.class);
        intent.putExtra("PLACE", meeting.getMemberKeyPlace().get(mUserKey));
        intent.putExtra("MEETING_KEY", mMeetingKey);
        startActivity(intent);

    }

    //-----------------------------------------------------------------------------------------------------------------
    //링크 확인 기능
    protected void linkCheck() {
        mIntent = getIntent();
        if (mIntent.getData() != null) {//링크를 눌러서 접속한것   여기에 로그인 여부 확인후 돌아오기 처리해야함 intent에서 setresult인가 이거 쓰면 될듯
            Uri uri = mIntent.getData();
            mMeetingKey = uri.getQueryParameter("MEETING_KEY");
            System.out.println("미팅키: " + mMeetingKey);
            System.out.println("링크를 통한 접속");
            if (LoginActivity.USER_KEY == null) {//로그인 안된경우
                System.out.println("링크를 통한 접속 + 로그인 안됨");
                mIntent = new Intent(getApplicationContext(), LoginActivity.class);
                mIntent.putExtra("MEETING_KEY", mMeetingKey);
                startActivity(mIntent);
                finish();
            } else {
                System.out.println("링크를 통한 접속 + 로그인 됨");
                System.out.println("스태틱 유저키: " + LoginActivity.USER_KEY);
                mUserKey = LoginActivity.USER_KEY;

                //background작업을 통해서 meeting과 user 정보를 기져오고 다 가져온 후 이 모임방에 있는 유저인지 검사하고 추가한다.
                task.execute(100);
            }
        } else {//그냥 내 모임 목록에서 선택한것
            System.out.println("링크 눌러서 접속");
            System.out.println("스태틱 유저키: " + LoginActivity.USER_KEY);
            mMeetingKey = mIntent.getStringExtra("MEETING_KEY");
            mUserKey = LoginActivity.USER_KEY;

            //background작업을 통해서 meeting과 user 정보를 기져오고 다 가져온 후 이 모임방에 있는 유저인지 검사하고 추가한다.
            task.execute(100);
        }
    }

    //-----------------------------------------------------------------------------------------------------
//async thread---------------------------------------------------------------------------------------------
    protected class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... integers) {
            getMeeting(mMeetingKey);
            getUser(mUserKey);

            while (meeting == null || user == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (!isParticipant()) {
                addUser();
                changePosition();
            }
        }
    }

    //---------------------------------------------------------------------------------------------------------
//이 모임에 있는 유저인지 확인
    protected boolean isParticipant() {
        if (meeting.getMemberKeyName().containsKey(mUserKey))
            return true;
        return false;
    }

    //------------------------------------------------------------------------------------------------------------
    //유저를 모임에 추가   TODO: 요때 뭔가 모임에 추가합니다 띄워 주는게 나을듯 아니면 changeposition 전에서던지
    protected void addUser() {
        DocumentReference docRef = db.collection("Meeting").document(mMeetingKey);
        HashMap<String, Object> updates = new HashMap<>();

        HashMap<String, String> memberKeyName = meeting.getMemberKeyName();
        HashMap<String, String> memberKeyPlace = meeting.getMemberKeyPlace();

        memberKeyName.put(mUserKey, user.getNickName());
        memberKeyPlace.put(mUserKey, "");

        updates.put("memberKeys", FieldValue.arrayUnion(mUserKey));
        updates.put("memberKeyName", memberKeyName);
        updates.put("memberKeyPlace", memberKeyPlace);
        docRef.update(updates);
        updates.clear();

        //user 업데이트
        docRef = db.collection("User").document(mUserKey);
        HashMap<String, String> meetingTitle = user.getMeetingTitle();
        meetingTitle.put(mMeetingKey, mTvParticipantName.getText().toString());

        updates.put("meetingKeys", FieldValue.arrayUnion(mMeetingKey));
        updates.put("meetingTitle", meetingTitle);

        docRef.update(updates);
    }

    //----------------------------------------------------------------------------------------------------------------------
/////////////////////필요한 정보를 받아오는 코드
    protected void getMeeting(String mMeetingKey) {
        db.collection("Meeting").document(mMeetingKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
                Log.v("소스", source);
                if (snapshot != null && snapshot.exists()) {
                    meeting = snapshot.toObject(Meeting.class);

                    String participant_name = "";
                    String participant_place = "";
                    String types = "";

                    for (int i = 0; i < meeting.getMemberKeys().size(); i++) {
                        String memberKey = meeting.getMemberKeys().get(i);
                        participant_name += meeting.getMemberKeyName().get(memberKey);
                        participant_place += meeting.getMemberKeyPlace().get(memberKey);
                    }
                    mTvParticipantName.setText(participant_name);
                    mTvParticipantPlace.setText(participant_place);
                    mTvRecommend.setText(meeting.getRecommendPlace().get("인하대"));

                    for (String type : meeting.getMeetingType()) {
                        types += type;
                    }

                    mTvType.setText(types);

                } else {
                }
            }
        });
    }

    protected void getUser(String mUserKey) {
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

    //링크 출력------------------------------------------------------------------------------------------------
    public void generateContentLink() {
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://partyhere.com?MEETING_KEY=" + mMeetingKey))
                .setDomainUriPrefix("https://partyhere.page.link")
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            System.out.println("링크: " + shortLink);
                            Uri flowchartLink = task.getResult().getPreviewLink();
                        } else {
                            System.out.println("링크에러");
                        }
                    }
                });

    }
}
