package com.inhascp.partyhere.ExistingMeeting;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inhascp.partyhere.Meeting;
import com.inhascp.partyhere.R;
import com.inhascp.partyhere.User;
import com.inhascp.partyhere.login.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.

 */
public class ExistMeetingFragmentSetting extends Fragment {

    private Button mBtnLeave;
    private Button mBtnRenew;
    private EditText mEditTitle;
    private FirebaseFirestore db;
    private Meeting meeting;
    private String MEETING_KEY;
    private Boolean jobFinish;
    private ArrayList<HashMap <String, String> > nameChanges;
    private String newTitle;


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





    public ExistMeetingFragmentSetting() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ExistMeetingFragmentSetting newInstance(String param1, String param2) {
        ExistMeetingFragmentSetting fragment = new ExistMeetingFragmentSetting();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exist_meeting_setting, container, false);

        //init
        init(rootView);


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

////////////////////모임 갱신 버튼- 일단 이름만 갱신됨. 그 타입 버튼 누르면 튕기더라.
        mBtnRenew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                newTitle =  mEditTitle.getText().toString();
                System.out.println("제발 1 ㅋㅋㅋ");
                //백그라운드에서 또 태스크 돌리자
                changeTask ct = new changeTask(getContext());
                ct.execute();



            }
        });


        return rootView;
    }

    public void changeMeetingName(){

        if(mEditTitle.getText().length() != 0 ) {


            DocumentReference userRef = db.collection("User").document(LoginActivity.USER_KEY);
            DocumentReference meetingRef = db.collection("Meeting").document(MEETING_KEY);

            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = new User();
                    user = documentSnapshot.toObject(User.class);

                    System.out.println("제발 3ㅋㅋㅋ" +  user.getNickName());

                    nameChanges.add(0, user.getMeetingTitle());

                }
            });

            meetingRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {


                    nameChanges.add(1, documentSnapshot.toObject(Meeting.class).getMemberKeyName());
                    jobFinish = true;
                }
            });





        }
    }

    private void init(View rootView){
        mBtnLeave = rootView.findViewById(R.id.fragment_exist_meeting_setting_btn_leave); //모임 떠나기 버튼
        mBtnRenew = rootView.findViewById(R.id.fragment_exist_meeting_setting_btn_renewal); // 모임 정보 갱신 버튼
        db =FirebaseFirestore.getInstance();
        meeting = (Meeting) getArguments().getSerializable("meeting");
        MEETING_KEY = getArguments().getString("MEETING_KEY");
        mEditTitle = rootView.findViewById(R.id.fragment_exist_meeting_setting_et_meeting_title);
        jobFinish = false;
        nameChanges = new ArrayList<HashMap<String,String>>(2);


        mTypeStudyCheckBox = rootView.findViewById(R.id.fragment_exist_meeting_setting_cb_study);
        mCheckboxStudy = rootView.findViewById(R.id.fragment_exist_meeting_setting_checkbox_study);
        mTypeTalkCheckBox = rootView.findViewById(R.id.fragment_exist_meeting_setting_cb_talk);
        mCheckboxTalk = rootView.findViewById(R.id.fragment_exist_meeting_setting_checkbox_talk);
        mTypeEat = rootView.findViewById(R.id.fragment_exist_meeting_setting_cb_eat);
        mCheckboxEat= rootView.findViewById(R.id.fragment_exist_meeting_setting_checkbox_eat);
        mTypeDrink = rootView.findViewById(R.id.fragment_exist_meeting_setting_cb_drink);
        mCheckboxDrink = rootView.findViewById(R.id.fragment_exist_meeting_setting_checkbox_drink);
        mTypeActivity = rootView.findViewById(R.id.fragment_exist_meeting_setting_cb_activity);
        mCheckboxActivity = rootView.findViewById(R.id.fragment_exist_meeting_setting_checkbox_activity);



    }

    class changeTask extends AsyncTask<Integer , Integer,Integer> {

        ProgressDialog dialog;

        Context context;
        Map<String, String>[] meetingChanges;

        public changeTask(Context context) {

            this.context = context;


        }



        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = new ProgressDialog(context);

            dialog.show();

        }

        @Override
        protected Integer doInBackground(Integer... integers) {


            changeMeetingName();

            while(!jobFinish){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            nameChanges.get(0).put(MEETING_KEY,newTitle);
            nameChanges.get(1).put(LoginActivity.USER_KEY,newTitle);/////////

            DocumentReference userRef = db.collection("User").document(LoginActivity.USER_KEY);
            DocumentReference meetingRef = db.collection("Meeting").document(MEETING_KEY);


            userRef
                    .update("meetingTitle", nameChanges.get(0))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
            meetingRef
                    .update("memberKeyName", nameChanges.get(1))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });




            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            dialog.dismiss();




            System.out.println("제발 ㅋㅋㅋ" +LoginActivity.USER_KEY+  nameChanges.get(0).get("QbadY7ns4tlcMdLuPwZD"));

        }
    }

}
