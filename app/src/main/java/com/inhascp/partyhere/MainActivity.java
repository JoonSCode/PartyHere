package com.inhascp.partyhere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.inhascp.partyhere.ui.main.SectionsPagerAdapter;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class MainActivity extends AppCompatActivity {

    private Button logout_but;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        logout_but = findViewById(R.id.logout_but);


        logout_but.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                onClickLogout();

            }




        });

        if(InputPlaceActivity.activity != null){
            InputPlaceActivity activity = InputPlaceActivity.activity;
            activity.finish();
        }
    }


    private void onClickLogout() {



        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
    }

    private void redirectLoginActivity() {

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

    }
}