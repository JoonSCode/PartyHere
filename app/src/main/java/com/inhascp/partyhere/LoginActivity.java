package com.inhascp.partyhere;

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
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private Button mSignInButton;
    private Button mSignUpButton;
    private Intent mIntent;

    private FirebaseFirestore db;


    private Button btn_custom_login;
    private LoginButton btn_kakao_login;





    EditText IDText;
    EditText passwordText;

    private FirebaseAuth mAuth;
    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db  = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mSignInButton = findViewById(R.id.activity_login_btn_sign_in);
        mSignUpButton= findViewById(R.id.activity_login_btn_sign_up);
        IDText = findViewById(R.id.activity_login_et_id);
        passwordText = findViewById(R.id.activity_login_et_pw);


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


    }

    protected void redirectMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class SessionCallback implements ISessionCallback {

        // 로그인에 성공한 상태
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        // 로그인에 실패한 상태
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
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
                public void onSuccess(MeV2Response response) {

                    long userID = response.getId(); //어플에 할당되는 카톡계정당 유저 아이디

                    System.out.println("user id : " + userID);

                    System.out.println("email: " + response.getKakaoAccount().getEmail());

                    mIntent = new Intent(getApplicationContext(),LoginActivity.class);
                    mIntent.putExtra("userID", userID);

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
                    Map<String, Object> user = new HashMap<>();
                    user.put("nickName", "남현우");

                    user.put("userID", response.getId());

                    db.collection("User").document(String.valueOf(response.getId())).set(user);


                    redirectMainActivity();
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
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }







    public void signIn(View v){

        String id = IDText.getText().toString();
        String ps = passwordText.getText().toString();


        loginUser(id, ps);

    }

    public void signUp(View v){


        String id = IDText.getText().toString();
        String ps = passwordText.getText().toString();


        createUser(id, ps);


    }

    public void loginUser(final String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();

                            String email_ = email;

                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra("email",email_);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                    }
                });
    }

    public void createUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, R.string.success_signup, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
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
                System.out.println("키는:" +keyHash);
                Log.d(TAG, keyHash);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }

        return keyHash;
    }


}
