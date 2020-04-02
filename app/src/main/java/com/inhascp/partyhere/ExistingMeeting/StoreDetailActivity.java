package com.inhascp.partyhere.ExistingMeeting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.inhascp.partyhere.R;

import java.util.ArrayList;

public class StoreDetailActivity extends AppCompatActivity {

    private Intent mIntent;
    private TextView titleView;
    private StoreInf mStoreInf;
    private String placeId;
    private String name;
    private ArrayList<String> type;
    private String vicinity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        //titleView = findViewById(R.id.activity_store_detail_title);
        mIntent = getIntent();
        //mStoreInf = mIntent.getParcelableExtra("storeInf");
        name = mIntent.getStringExtra("storeName");
        placeId = mIntent.getStringExtra("storePlaceId");
        type = mIntent.getStringArrayListExtra("storeType");
        vicinity = mIntent.getStringExtra("storeVicinity");
        //titleView.setText(name);

    }
}
