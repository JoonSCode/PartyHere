package com.inhascp.partyhere.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inhascp.partyhere.ui.main.MainActivity;
import com.inhascp.partyhere.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class InformAccActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private Button mCompleteButton;
    private TextView mInformNick;
    private Intent mIntent;
    private String mNick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform_acc);

        mCompleteButton = findViewById(R.id.activity_inform_account_btn_complete);
        mInformNick = findViewById(R.id.activity_inform_account_nickname);


        db = FirebaseFirestore.getInstance();

        mCompleteButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mNick = mInformNick.getText().toString();


                mIntent = new Intent(getApplicationContext(), MainActivity.class);
                if(getIntent() != null){
                    System.out.println("링크 재연결");
                    mIntent.putExtra("MEETING_KEY",getIntent().getStringExtra("MEETING_KEY"));

                }

                DocumentReference userRef = db.collection("User").document(LoginActivity.USER_KEY);


                userRef
                        .update("nickName", mNick)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                Log.d("성공", "DB 업데이트 성공");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("에러", "DB 업데이트 에러", e);
                            }
                        });
                startActivity(mIntent);
            }
        }));
    }
}
