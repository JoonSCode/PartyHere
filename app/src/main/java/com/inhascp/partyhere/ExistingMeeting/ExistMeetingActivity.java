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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
    List<String> PlaceList = Arrays.asList("가락시장역", "가산디지털단지역", "강남구청역", "강남역12번출구", "강남역9번출구", "강변역", "강서구청", "건대입구역", "경동시장", "경복궁역", "경희대학교", "고덕역", "고속터미널역", "공덕역", "광화문", "교대역", "구로역", "구의역", "국회의사당역", "군자역", "길동역", "남대문시장", "남부터미널역", "노량진역", "노원역", "논현역", "답십리역", "당산역", "대림역", "대치역", "대학동", "도산공원사거리", "독산역", "동대문시장", "동대문역", "동대문역사문화공원역", "둔촌동역", "등촌역", "디지털미디어시티", "롯데백화점본점", "마포역", "매봉역", "명동역", "명일역", "목동사거리", "목동현대백화점앞", "문래동주민센터", "문정역", "미아사거리역", "발산역", "방배동카페골목", "방배역", "방이동먹자골목", "뱅뱅사거리", "보라매역", "불광역", "사가정역", "사당역", "삼성역", "서대문역", "서울대입구역", "서초역", "석촌역", "선릉역", "선정릉역", "성신여대입구역", "송정역", "수서역", "수유역", "숙대입구역", "시청역", "시흥1동", "신당역", "신대방삼거리역", "신도림역", "신림역", "신사동가로수길", "신사역", "신설동역", "신용산역", "신정네사거리역", "신천역", "신촌역", "안국역", "안암역", "압구정로데오거리", "압구정역", "약수역", "양재역", "어린이대공원역", "언주역", "여의도역", "역삼역", "연신내역", "영등포구청역", "영등포시장역", "영등포역", "오류동역앞", "오목교역", "왕십리역", "용산전자상가", "을지로3가역", "을지로4가역", "이대역", "이수역", "이태원역", "인사동", "잠실역", "장안동사거리", "장지역", "장충동족발거리", "장한평역", "종각역", "종로3가역", "종로5가역", "종로구청", "창동역", "천호역", "청담사거리", "청량리역", "충무로역", "코엑스", "포스코사거리", "포이사거리", "학동사거리", "학동역", "한남오거리", "한티역", "혜화역", "홍대입구");
    private ArrayList<String> places;
    private Boolean jobFinish0;
    private List<Boolean> typeNum;
    private Meeting meeting;
    private String[] types = new String[]{"공부", "대화", "식사", "술", "액티비티"}; //모임 타입;
    private getMeeting getMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_meeting);

        Log.v("test", "existing들어옴");
        /////////////               가게 리스트 받아오기
        meeting = new Meeting();
        db = FirebaseFirestore.getInstance();
        jobFinish = false;
        jobFinish0 = false;
        typeNum = Arrays.asList(false, false, false, false, false);


        places = new ArrayList<String>();

        //  places.add("가락시장역");//
        // places.add("강남구청역");// 아직 장소 알고리즘 적용 안되서 임시로 넣음.
        storeList = new ArrayList < ArrayList<StoreInf>>();
        MEETING_KEY = getIntent().getStringExtra("MEETING_KEY");//
        meetingName = getIntent().getStringExtra("meetingName");


        getMeeting = new getMeeting();
        getMeeting.execute();
        task = new BackgroundTask(ExistMeetingActivity.this);


    }

    /////선택된 상권의 가게 리스트 카테고리 별로 받아오기
    public void getStoreList() {

        for (int i = 0; i < 6; i++) {

            ArrayList<StoreInf> newList = new ArrayList<StoreInf>();
            storeList.add(newList);
        }

        //순서 : bar, activity, restaurant, conversation, study, special


        for (int k = 0; k < places.size(); k++) {

            final int idxk = k;
            for (int j = 0; j < 5; j++) {
                final int jj = j;

                db.collection("Place").document(places.get(k)).collection("Category").document(types[j]).collection("Spot")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<StoreInf> newone = new ArrayList<StoreInf>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        StoreInf sf = document.toObject(StoreInf.class);

                                        newone.add(sf);
                                    }

                                    ArrayList<StoreInf> newtwo = new ArrayList<>(storeList.get(jj));
                                    newtwo.addAll(newone);
                                    storeList.set(jj, newtwo);
                                    if (idxk == places.size() - 1 && jj == 4) {
                                        jobFinish = TRUE;
                                        System.out.println("Amazing 가져오기 완료");
                                    }
                                    System.out.println("Amazing storeList.get(" + jj + ").size() = " + storeList.get(jj).size());


                                } else {
                                    Log.d("storeLIst", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        }
    }

    // class to define user defined conparator
    static class Compare {

        static void compare(Pair[] arr, int n) {
            // Comparator to sort the pair according to second element
            Arrays.sort(arr, new Comparator<Pair>() {
                @Override
                public int compare(Pair p1, Pair p2) {
                    return (int) (p2.x - p1.x);
                }
            });

        }
    }

    private class getMeeting extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {
            DocumentReference docRef = db.collection("Meeting").document(MEETING_KEY);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    meeting = documentSnapshot.toObject(Meeting.class);

                    typeNum = meeting.getMeetingType();

                    jobFinish0 = true;
                    System.out.println("Amazing 타입 설정 완료");

                    HashMap<String, List<Integer>> memberTransferTime = meeting.getMemberTransferTime();
                    List<List<Integer>> lists = new ArrayList<>();
                    Pair[] arr = new Pair[130];

                    for (List<Integer> li : memberTransferTime.values()) {
                        lists.add(li);
                    }
                    for (int i = 0; i < 130; i++) {
                        double total = 0;
                        double average = 0;
                        for (int n = 0; n < lists.size(); n++) {
                            total = lists.get(n).get(i);
                        }
                        average = total / lists.size();//평균 이동시간
                        double cal = 0;
                        for (int a = 0; a < lists.size(); a++) {
                            cal += Math.abs(average - lists.get(a).get(i));//평균과의 편차 총합
                        }
                        cal /= lists.size();
                        arr[i] = new Pair(4 * average + 7 * cal, i);
                    }

                    Compare obj = new Compare();

                    Compare.compare(arr, 130);

                    for (int i = 0; i < 3; i++) {
                        places.add(PlaceList.get(arr[i].y));
                    }
                    task.execute(); //이후에 onCeate에서 할 것들 다 task의 post로 옮김.

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {

        }
    }

    class Pair {
        double x;
        int y;

        // Constructor
        public Pair(double x, int y) {
            this.x = x;
            this.y = y;
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
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (jobFinish && jobFinish0) break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);


            ArrayList<StoreInf> specialList = new ArrayList<StoreInf>();

            for (int i = 0; i < 5; i++) {

                System.out.println(" Amazing typeNum" + i + ":  " + typeNum.get(i));
                if (typeNum.get(i)) {
                    specialList.addAll(storeList.get(i));
                }
            }


            storeList.set(5, specialList);
            System.out.println(" Amazing 끝 스페셜리스트 사이즈 : " + specialList.size());

            dialog.dismiss();


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