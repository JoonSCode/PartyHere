package com.inhascp.partyhere.ExistingMeeting;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.inhascp.partyhere.Meeting;
import com.inhascp.partyhere.R;

import java.util.ArrayList;

import static java.lang.Boolean.TRUE;

public class ExistMeetingActivity extends AppCompatActivity {


    private String MEETING_KEY;
    private String meetingName;
    // FrameLayout에 각 메뉴의 Fragment를 바꿔 줌
    private FragmentManager fragmentManager = getSupportFragmentManager();
    // 4개의 메뉴에 들어갈 Fragment들
    private ExistMeetingFragmentMain menu1Fragment;//= new ExistMeetingFragmentMain();
    private ExistMeetingFragmentInfo menu2Fragment ;//= new ExistMeetingFragmentInfo();
    private ExistMeetingFragmentSetting menu3Fragment ;//= new ExistMeetingFragmentSetting();
    ///////////////////////
    private FirebaseFirestore db;
    private ArrayList <ArrayList<StoreInf>> storeList;
    private BackgroundTask task;
    private Boolean jobFinish;
    private ArrayList<String> places;
    private ArrayList<Integer> typeNum;
    private String[] types;
    private Meeting meeting;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_meeting);


        /////////////               가게 리스트 받아오기
        meeting = new Meeting();
        db = FirebaseFirestore.getInstance();
        jobFinish = false;
        typeNum = new ArrayList<Integer>();
        types = new String[]{"술", "액티비티" , "식사", "대화","공부" }; //모임 타입

        places = new ArrayList<String>();
        typeNum.add(1);
        typeNum.add(0);//타입 임의로 정함
        places.add("가락시장역");//
        places.add("강남구청역");// 아직 장소 알고리즘 적용 안되서 임시로 넣음.
        storeList = new ArrayList < ArrayList<StoreInf>>();
        MEETING_KEY = getIntent().getStringExtra("MEETING_KEY");//
        meetingName = getIntent().getStringExtra("meetingName");




        DocumentReference docRef = db.collection("Meeting").document(MEETING_KEY);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                meeting = documentSnapshot.toObject(Meeting.class);
            }
        }); // 미팅 정보 가져오기


        task = new BackgroundTask(ExistMeetingActivity.this);
        task.execute(); //이후에 onCeate에서 할 것들 다 task의 post로 옮김.


    }
/////선택된 상권의 가게 리스트 카테고리 별로 받아오기
    public  void getStoreList(){

        for(int i=0;i<6;i++){

            ArrayList<StoreInf> newList = new ArrayList<StoreInf>();
            storeList.add(newList);
        }

        //순서 : bar, activity, restaurant, conversation, study, special

        for(int k = 0;k<places.size();k++){

            final int idxk = k;
            for(int j = 0;j<5;j++) {
                final int jj = j;

                db.collection("Place").document(places.get(k)).collection("Category").document(types[j]).collection("Spot")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList <StoreInf> newone = new ArrayList<StoreInf>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        StoreInf sf = document.toObject(StoreInf.class);

                                        newone.add(sf);
                                    }

                                    ArrayList<StoreInf> newtwo = new ArrayList<>(storeList.get(jj));
                                    newtwo.addAll(newone);
                                    storeList.set(jj,newtwo);
                                    if(idxk == places.size()-1 && jj == 4){
                                        jobFinish = TRUE;
                                    }
                                    System.out.println( "Amazing storeList.get("+jj+").size() = " + storeList.get(jj).size());


                                } else {
                                    Log.d("storeLIst", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        }
    }

    /////가게 리스트 db에서 받아오는 백그라운드 태스크
    class BackgroundTask extends AsyncTask<Integer , Integer,Integer> {

        ProgressDialog dialog;

        Context context;

        public BackgroundTask(Context context){

            this.context = context;


        }



        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            dialog = new ProgressDialog(context);

            dialog.show();

        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            getStoreList();
            while(!jobFinish){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for(int k = 0; k <5; k++) {
                System.out.println(k + " Amazing 00: " + storeList.size());
                System.out.println(k + " Amazing 0: " + storeList.get(0).size());
                System.out.println(k + " Amazing 1: " + storeList.get(1).size());
                System.out.println(k + " Amazing 2: " + storeList.get(2).size());
                System.out.println(k + " Amazing 3: " + storeList.get(3).size());
                System.out.println(k + " Amazing 4: " + storeList.get(4).size());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            dialog.dismiss();

            ArrayList<StoreInf> specialList = new ArrayList<StoreInf>();

            for (int i = 0; i < typeNum.size(); i++) {

                specialList.addAll(storeList.get(i));
            }


            System.out.println(" Amazing 스페셜리스트 사이즈 : " + specialList.size());
            storeList.set(5, specialList);


            System.out.println(" Amazing ME 이제끝 " + storeList.get(0).size());
            System.out.println("Amazing " + storeList.get(5).get(0).getName());

            //DB에서 정보를 가져온 이후에 Fragment생성--------------------------------------------------------------------------------------
            menu1Fragment= new ExistMeetingFragmentMain();
            menu2Fragment = new ExistMeetingFragmentInfo();
            menu3Fragment = new ExistMeetingFragmentSetting();

            // fragment에 미팅 키, 가게 리스트 등 전달
            Bundle bundle = new Bundle();
            bundle.putString("MEETING_KEY",MEETING_KEY);
            bundle.putSerializable("storeList" , storeList);
            bundle.putSerializable("meeting", meeting);
            bundle.putString("meetingName", meetingName);

            menu1Fragment.setArguments(bundle);
            menu2Fragment.setArguments(bundle);
            menu3Fragment.setArguments(bundle);

            /////////////////////////////////////

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
            // 첫 화면 지정
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_layout, menu1Fragment).commitAllowingStateLoss();

            // bottomNavigationView의 아이템이 선택될 때 호출될 리스너 등록
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    switch (item.getItemId()) {
                        case R.id.navigation_menu1: {
                            transaction.replace(R.id.frame_layout, menu1Fragment).commitAllowingStateLoss();
                            break;
                        }
                        case R.id.navigation_menu2: {
                            transaction.replace(R.id.frame_layout, menu2Fragment).commitAllowingStateLoss();
                            break;
                        }
                        case R.id.navigation_menu3: {
                            transaction.replace(R.id.frame_layout, menu3Fragment).commitAllowingStateLoss();
                            break;
                        }
                    }

                    return true;
                }
            });



        }
    }

}
