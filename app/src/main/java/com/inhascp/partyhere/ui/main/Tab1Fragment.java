package com.inhascp.partyhere.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.inhascp.partyhere.ExistMeetingActivity;
import com.inhascp.partyhere.InputPlaceActivity;
import com.inhascp.partyhere.ListViewAdapter;
import com.inhascp.partyhere.MeetingListItem;
import com.inhascp.partyhere.R;
import com.inhascp.partyhere.User;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class Tab1Fragment extends Fragment {

    static final String USER_KEY = "NueasP51ZCXmqkhcY60E";//userkey

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button mNewMeetingButton;
    private ListView mListView;
    private Intent mIntent;
    private ListViewAdapter mListViewAdapter;
    private PageViewModel pageViewModel;

    public Tab1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        pageViewModel.setIndex(0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mNewMeetingButton = root.findViewById(R.id.fragment_main_btn_new_meeting);

        mListView = root.findViewById(R.id.fragment_tab1_lv_meeting);
        mListViewAdapter = new ListViewAdapter();

        db.collection("User").document(USER_KEY).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    //System.out.println("print: "+ snapshot.toString());
                    User user = snapshot.toObject(User.class);
                    ArrayList<String> mMeetingKeys = user.getMeetingKeys();
                    mListViewAdapter.clear();
                    for(int i = 0; i < mMeetingKeys.size(); i++) {
                        String mMeetingKey = mMeetingKeys.get(i);
                        String mMeetingTitle = user.getMeetingTitle().get(mMeetingKey);
                        mListViewAdapter.addItem(mMeetingTitle,mMeetingKey);
                    }

                    mListView.setAdapter(mListViewAdapter);
                }
                else {
                }
            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                MeetingListItem item =  (MeetingListItem) parent.getItemAtPosition(position) ;

                mIntent = new Intent(getContext(), ExistMeetingActivity.class);
                mIntent.putExtra("MEETING_KEY", item.getmMeetingKey());
                mIntent.putExtra("USER_KEY", USER_KEY);
                startActivity(mIntent);
            }
        }) ;
        mNewMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getContext(), InputPlaceActivity.class);
                startActivity(mIntent);
            }
        });

        return root;
    }
}