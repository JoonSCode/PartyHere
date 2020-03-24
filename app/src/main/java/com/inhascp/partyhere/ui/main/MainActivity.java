package com.inhascp.partyhere.ui.main;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.inhascp.partyhere.ExistMeetingActivityBackup;
import com.inhascp.partyhere.ExistingMeeting.ExistMeetingActivity;
import com.inhascp.partyhere.Meeting;
import com.inhascp.partyhere.NewMeeting.InputPlaceActivity;
import com.inhascp.partyhere.R;
import com.inhascp.partyhere.User;
import com.inhascp.partyhere.login.LoginActivity;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    ImageButton logout_but;
    private String USER_KEY;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseFirestore db2 = FirebaseFirestore.getInstance();

    private ImageButton mNewMeetingButton;
    private ListView mListView;
    private Intent mIntent;
    private ListViewAdapter mListViewAdapter;
    private BackgroundTask task;
    private BackgroundMeetingTask meetingTask;
    private User user;
    private Meeting meeting;
    public static List<Pair<String, LatLng>> PLACE;
    final List<Integer> meetingPersonCnt = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tb = findViewById(R.id.toolbar) ;
        setSupportActionBar(tb) ;

        USER_KEY = LoginActivity.USER_KEY;
        task = new BackgroundTask();
        meetingTask = new BackgroundMeetingTask();
        PLACE = new ArrayList<>();
        mNewMeetingButton = findViewById(R.id.activity_main_btn_new_meeting);
        mListView = findViewById(R.id.activity_main_lv_meeting);
        mListViewAdapter = new ListViewAdapter();


        getSupportActionBar().setTitle("");


        task.execute();


        mNewMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getApplicationContext(), InputPlaceActivity.class);
                startActivity(mIntent);
            }
        });

        logout_but = findViewById(R.id.logout_btn);
        logout_but.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                onClickLogout();

            }
        });

        if(InputPlaceActivity.activity != null){// input activity를 stack에서 제거한다,
            InputPlaceActivity activity = InputPlaceActivity.activity;
            activity.finish();
        }

        checkPermissions();

    }



    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            if(getIntent().getStringExtra("MEETING_KEY") != null){
                System.out.println("방으로 자동 접속");
                Intent mIntent = new Intent(getApplicationContext(), ExistMeetingActivityBackup.class);
                mIntent.putExtra("MEETING_KEY", getIntent().getStringExtra("MEETING_KEY"));
                startActivity(mIntent);
            }// 권한 승인이 필요없을 때 실행할 함수
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23){ // 마시멜로(안드로이드 6.0) 이상 권한 체크
            TedPermission.with(getApplicationContext())
                    .setPermissionListener(permissionlistener)
                    .setRationaleMessage("위치 설정을 위해서 접근 권한이 필요합니다")
                    .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                    .setPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION})
                    .check();

        } else {
            if(getIntent().getStringExtra("MEETING_KEY") != null){
                System.out.println("방으로 자동 접속");
                Intent mIntent = new Intent(getApplicationContext(), ExistMeetingActivityBackup.class);
                mIntent.putExtra("MEETING_KEY", getIntent().getStringExtra("MEETING_KEY"));
                startActivity(mIntent);
            }
        }
    }

    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                LoginActivity.USER_KEY = null;
                redirectLoginActivity();
            }
        });
    }

    private void redirectLoginActivity() {

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    protected class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... integers) {
            getPlace();
            getMeetingList();
            while(user==null || PLACE == null) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Integer integer) {
            meetingTask.execute();
        }
    }
    protected class BackgroundMeetingTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... integers) {
            ArrayList<String> mMeetingKeys = user.getMeetingKeys();
            mListViewAdapter.clear();
            meetingPersonCnt.clear();
            for(int i = 0; i < mMeetingKeys.size(); i++){
                String mMeetingKey = mMeetingKeys.get(i);
                db2.collection("Meeting").document(mMeetingKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task != null){
                            meeting = task.getResult().toObject(Meeting.class);
                            meetingPersonCnt.add(meeting.getMemberKeys().size());
                        }
                    }
                });
                while(meetingPersonCnt.size() != i+1){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Integer integer) {
            ArrayList<String> mMeetingKeys = user.getMeetingKeys();


            for(int i = 0; i < mMeetingKeys.size(); i++) {
                String mMeetingKey = mMeetingKeys.get(i);
                String mMeetingTitle = user.getMeetingTitle().get(mMeetingKey);
                mListViewAdapter.addItem(mMeetingTitle,mMeetingKey, meetingPersonCnt.get(i));
            }

            mListView.setAdapter(mListViewAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    // get item
                    MeetingListItem item =  (MeetingListItem) parent.getItemAtPosition(position) ;

                    mIntent = new Intent(getApplicationContext(), ExistMeetingActivity.class);
                    mIntent.putExtra("MEETING_KEY", item.getmMeetingKey());
                    startActivity(mIntent);

                }
            }) ;
        }
    }
    //필요한 정보 받아오기---------------------------------------------------------------------------
    protected void getMeetingList(){
        db.collection("User").document(USER_KEY).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    user = snapshot.toObject(User.class);
                }
                else {
                }
            }
        });

    }

    protected void getPlace(){
        db.collection("Place").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // System.out.println("상권: " + document.getId() + " 위치: " + document.getGeoPoint("location"));
                        LatLng latlng = new LatLng(document.getGeoPoint("location").getLatitude(), document.getGeoPoint("location").getLongitude());
                        PLACE.add(new Pair<>(document.getId(), latlng));
                        //Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                    List<String> tmp = new ArrayList<>();
                    for(int i = 0; i < 130; i++){
                        tmp.add(PLACE.get(i).first);
                    }
                    System.out.println("장소리스트:" + tmp);
                } else {
                    // Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}