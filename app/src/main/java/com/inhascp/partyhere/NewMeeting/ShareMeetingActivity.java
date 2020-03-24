package com.inhascp.partyhere.NewMeeting;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.inhascp.partyhere.ExistingMeeting.ExistMeetingActivity;
import com.inhascp.partyhere.R;

public class ShareMeetingActivity extends AppCompatActivity {

    private String MEETING_KEY;
    private TextView mTvLink;
    private Button mBtnComplete;
    private Button mBtnInvite;
    private MakeLink task;
    private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_meeting);

        init();

        task.execute();
    }

    protected void init(){
        MEETING_KEY = getIntent().getStringExtra("MEETING_KEY");
        mTvLink = findViewById(R.id.activity_share_meeting_tv_link);
        mBtnComplete = findViewById(R.id.activity_share_meeting_btn_complete);
        mBtnInvite = findViewById(R.id.activity_share_meeting_btn_invite);
        task = new MakeLink();
    }
    protected class MakeLink extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... integers) {
            generateContentLink();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mBtnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareMeeting();
                }
            });

            mBtnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIntent = new Intent(getApplicationContext(), ExistMeetingActivity.class);
                    mIntent.putExtra("MEETING_KEY", MEETING_KEY);
                    startActivity(mIntent);
                    finish();
                }
            });
        }
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
                           mTvLink.setText(shortLink.toString());
                            Uri flowchartLink = task.getResult().getPreviewLink();
                        } else {
                            System.out.println("링크에러");
                        }
                    }
                });

    }
    public void shareMeeting(){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);

        intent.setType("text/plain");
// Set default text message
// 카톡, 이메일, MMS 다 이걸로 설정 가능
//String subject = "문자의 제목";
        String text = mTvLink.getText().toString();
//intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);

// Title of intent
        Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
        startActivity(chooser);
    }


}
