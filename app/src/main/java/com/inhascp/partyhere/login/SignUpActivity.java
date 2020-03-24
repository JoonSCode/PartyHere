package com.inhascp.partyhere.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.inhascp.partyhere.R;

public class SignUpActivity extends AppCompatActivity {

    private TextView mId;
    private TextView mPassword;
    private TextView mName;
    private Button mCompleteButton;
    private Intent mIntent;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mId = findViewById(R.id.activity_sign_up_et_id);
        mPassword = findViewById((R.id.activity_sign_up_et_pw));
        mName = findViewById(R.id.activity_sign_up_et_name);
        mCompleteButton = findViewById(R.id.activity_sign_up_btn_complete);

        mAuth = FirebaseAuth.getInstance();


        mCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String id = mId.getText().toString();
                String ps = mPassword.getText().toString();


                createUser(id, ps);

                //mIntent = new Intent(getApplicationContext(), LoginActivity.class);
                //startActivity(mIntent);
            }
        });
    }

    public void createUser(final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUpActivity.this, R.string.success_signup, Toast.LENGTH_SHORT).show();

                            comeback(email, password);


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }



    public void comeback(String email, String password){



        mIntent = new Intent();
        mIntent.putExtra("email",email);
        mIntent.putExtra("password",password);
        setResult(RESULT_OK, mIntent);
        finish();






    }




}
