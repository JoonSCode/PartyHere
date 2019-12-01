package com.inhascp.partyhere;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InputPlaceActivity extends AppCompatActivity {

    public static InputPlaceActivity activity= null;

    private Button mBtnNext;
    private TextView mTvPlace;
    private Intent mIntent;
    private String USER_KEY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_place);

        activity = this;

        USER_KEY = "NueasP51ZCXmqkhcY60E";
        mBtnNext = findViewById(R.id.activity_input_place_btn_next);
        mTvPlace = findViewById(R.id.activity_input_place_et_place);

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent = new Intent(getApplicationContext(), InputTypeActivity.class);
                mIntent.putExtra("Place", mTvPlace.getText().toString());
                mIntent.putExtra("USER_KEY",USER_KEY);
                startActivity(mIntent);
            }
        });
    }
}
