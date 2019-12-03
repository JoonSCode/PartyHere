package com.inhascp.partyhere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.inhascp.partyhere.ui.main.SectionsPagerAdapter;

import java.util.logging.Logger;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction()) && intent.getData() != null) {
            Uri uri = intent.getData();
            String KEY = uri.getQueryParameter("MEETING_KEY");
        } else {
            System.out.println("Not DeepLink!");
        }

        if(InputPlaceActivity.activity != null){
            InputPlaceActivity activity = InputPlaceActivity.activity;
            activity.finish();
        }
    }
}