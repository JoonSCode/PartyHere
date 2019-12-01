package com.inhascp.partyhere;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    private Button mSignInButton;
    private Button mSignUpButton;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSignInButton = findViewById(R.id.activity_login_btn_sign_in);
        mSignUpButton= findViewById(R.id.activity_login_btn_sign_up);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 mIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(mIntent);
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mIntent);

                //login 완료 시
                finish();
            }
        });

    }
}
