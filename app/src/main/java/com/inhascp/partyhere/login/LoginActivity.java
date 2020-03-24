package com.inhascp.partyhere.login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inhascp.partyhere.R;
import com.inhascp.partyhere.User;
import com.inhascp.partyhere.ui.main.MainActivity;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {


    private Button mSignInButton;
    private Button mSignUpButton;
    private Button mCustomKakaoButton;
    private Intent mIntent;

    private FirebaseFirestore db;

    private LoginButton mKakaoLoginButton;

    public static String USER_KEY;


    private EditText IDText;
    private EditText passwordText;

    private FirebaseAuth mAuth;
    private SessionCallback callback;
    private Boolean isLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db  = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        isLink = false;
        mSignInButton = findViewById(R.id.activity_login_btn_sign_in);
        mSignUpButton= findViewById(R.id.activity_login_btn_sign_up);
        IDText = findViewById(R.id.activity_login_et_id);
        passwordText = findViewById(R.id.activity_login_et_pw);
        mCustomKakaoButton = findViewById(R.id.activity_login_btn_kakao);
        mKakaoLoginButton = findViewById(R.id.btn_kakao_login);




        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();


        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(view);
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp(view);
            }
        });

        getHashKey(getApplicationContext());

        mCustomKakaoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mKakaoLoginButton.performClick();
            }
        });

    }



    protected void redirectMainActivity(boolean in) {
        mIntent = new Intent(this, MainActivity.class);
        Intent intent2 = new Intent(this, InformAccActivity.class);
        if(getIntent() != null){
            System.out.println("링크!!");
            mIntent.putExtra("MEETING_KEY",getIntent().getStringExtra("MEETING_KEY"));
        }


        if(in){
            System.out.println("기존 유저는 메인으로 라이츄");
            startActivity(mIntent);

        }
        else{
            System.out.println("신규 유저는 닉넴입력으로 라이츄");
            mIntent = new Intent(this, InformAccActivity.class);
            startActivity(mIntent);

        }

        finish();
    }


    public class SessionCallback implements ISessionCallback {

        // 로그인에 성공한 상태
        @Override
        public void onSessionOpened() {
            System.out.println("opened");

            requestMe();
        }
        // 로그인에 실패한 상태
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            System.out.println("failed");

            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }

        // 사용자 정보 요청
        public void requestMe() {

            List<String> keys = new ArrayList<>();
            keys.add("properties.nickname");
            keys.add("properties.profile_image");
            keys.add("kakao_account.email");

            // 사용자정보 요청 결과에 대한 Callback
            UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
                // 세션 오픈 실패. 세션이 삭제된 경우,
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("SessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());
                }

                // 회원이 아닌 경우,
            /*@Override
            public void onNotSignedUp() {
                Log.e("SessionCallback :: ", "onNotSignedUp");
            }*/

                // 사용자정보 요청에 성공한 경우,
                @Override
                public void onSuccess(final MeV2Response response) {

                    USER_KEY = String.valueOf(response.getId()); //어플에 할당되는 카톡계정당 유저 아이디

                    System.out.println("user id : " + USER_KEY);

                    System.out.println("email: " + response.getKakaoAccount().getEmail());




                    /*public void requestProfile() {

                        KakaoTalkService.getInstance().requestProfile(new TalkResponseCallback<KakaoTalkProfile>() {
                            @Override
                            public void onSessionClosed(ErrorResult errorResult) {

                                ;
                            }

                            @Override
                            public void onNotKakaoTalkUser() {
                                Logger.d("There is no such user");
                            }

                            @Override
                            public void onSuccess(KakaoTalkProfile talkProfile) {
                                final String nickName = talkProfile.getNickName();
                                final String profileImageURL = talkProfile.getProfileImageUrl();
                                final String thumbnailURL = talkProfile.getThumbnailUrl();
                                final String countryISO = talkProfile.getCountryISO();
                            }
                        });
                    }*/


                    final boolean[] alreadyIn = {false};

                    DocumentReference userRef = db.collection("User").document(USER_KEY); //유저 확인
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) { //가입 이미 되어있음

                                    alreadyIn[0] = true;

                                    System.out.println("기존 유저 라이츄");
                                    redirectMainActivity(alreadyIn[0]);
                                } else {//안되어있음
                                    alreadyIn[0] = false;
                                    User user =new User();

                                    db.collection("User").document(USER_KEY).set(user);

                                    System.out.println("신규 유저 라이츄");
                                    redirectMainActivity(alreadyIn[0]);
                                }




                            } else {
                                Log.d("에러", "get failed with ", task.getException());
                            }
                        }
                    });






                }

                // 사용자 정보 요청 실패
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e("SessionCallback :: ", "onFailure : " + errorResult.getErrorMessage());
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // 이메일 회원가입 - signup액티비에서 돌아온 것 처리.
            if (resultCode == RESULT_OK) {

                loginUser(data.getStringExtra("email"),data.getStringExtra("password"),false);

            } else {   // RESULT_CANCEL
                Toast.makeText(LoginActivity.this, "이메일 회원가입 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }





    public void signIn(View v){

        String id = IDText.getText().toString();
        String ps = passwordText.getText().toString();


        loginUser(id, ps, true);

    }

    public void signUp(View v){

        mIntent = new Intent(getApplicationContext(),SignUpActivity.class);
        startActivityForResult(mIntent,1);

    }

    //이메일로 로그인
    public void loginUser(final String email, String password, final boolean alreadyIn) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            USER_KEY =  currentUser.getUid();





                            DocumentReference userRef = db.collection("User").document(USER_KEY); //유저 확인
                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) { //가입처리 이미 되어있음



                                            System.out.println("기존 유저 라이츄 꼬북이");
                                            redirectMainActivity(alreadyIn);
                                        } else {//안되어있음
                                            User user = new User();

                                            db.collection("User").document(USER_KEY).set(user);


                                            System.out.println("신규 유저 라이츄 꼬북이");
                                            redirectMainActivity(alreadyIn);
                                        }




                                    } else {
                                        Log.d("에러", "get failed with ", task.getException());
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                    }
                });
    }







    @Nullable //해쉬키 생성
    public static String getHashKey(Context context) {
        final String TAG = "KeyHash";
        String keyHash = null;
        try {
            PackageInfo info =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = new String(Base64.encode(md.digest(), 0));
                //System.out.println("키는:" +keyHash);
                Log.d(TAG, keyHash);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }

        return keyHash;
    }


}
