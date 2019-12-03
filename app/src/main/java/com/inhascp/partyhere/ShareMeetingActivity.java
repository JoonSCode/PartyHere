package com.inhascp.partyhere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class ShareMeetingActivity extends AppCompatActivity {

    private String MEETING_KEY;
    private String USER_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_meeting);

        init();

        generateContentLink();
        Intent intent = new Intent(getApplicationContext(), ExistMeetingActivity.class);
        intent.putExtra("MEETING_KEY", MEETING_KEY);
        intent.putExtra("USER_KEY", USER_KEY);
        startActivity(intent);
        finish();
    }

    protected void init(){
        MEETING_KEY = getIntent().getStringExtra("MEETING_KEY");
        USER_KEY = getIntent().getStringExtra("USER_KEY");
    }

    public void generateContentLink() {
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://partyhere.com?MEETING_KEY="+MEETING_KEY))
                .setDomainUriPrefix("https://partyhere.page.link")
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            System.out.println("링크: " +shortLink);
                            Uri flowchartLink = task.getResult().getPreviewLink();
                        } else {
                            System.out.println("링크에러");
                        }
                    }
                });

    }

}