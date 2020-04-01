package com.inhascp.partyhere.ExistingMeeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inhascp.partyhere.Meeting;
import com.inhascp.partyhere.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExistMeetingFragmentMain.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExistMeetingFragmentMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExistMeetingFragmentMain extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Spinner mSpinnerType;
    private ArrayList <ArrayList<StoreInf>> storeList;
    private ArrayList<StoreInf> mStores;
    private String MEETING_KEY;
    private Meeting meeting;
    private String meetingName;

    private TextView meetingTitle;


    public ExistMeetingFragmentMain() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExistMeetingFragmentMain.
     */
    // TODO: Rename and change types and number of parameters
    public static ExistMeetingFragmentMain newInstance(String param1, String param2) {
        ExistMeetingFragmentMain fragment = new ExistMeetingFragmentMain();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_exist_meeting_main, container, false);

        storeList = (ArrayList<ArrayList<StoreInf>>) getArguments().getSerializable("storeList");
        MEETING_KEY = getArguments().getString("MEETING_KEY");
        meeting = (Meeting) getArguments().getSerializable("meeting");
        meetingName = getArguments().getString("meetingName");

        meetingTitle = view.findViewById(R.id.meeting_title);
        meetingTitle.setText(meetingName);

        mSpinnerType = view.findViewById(R.id.activity_exist_meeting_spinner_type);
        String list[] = (getResources().getStringArray(R.array.spinner_type));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, list);
        mSpinnerType.setAdapter(arrayAdapter);


        mStores = storeList.get(5);//5번 - 디폴트인 스페셜리스트

        ///RecyclerView에 가게 리스트를 띄운다
        final RecyclerView recyclerView =  view.findViewById(R.id.activity_exist_meeting_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())) ;

        //mStores = 리스트에 보여질 가게 목록
        final StoreListAdaptor adapter = new StoreListAdaptor(getActivity(), mStores) ;
        recyclerView.setAdapter(adapter) ;

        //스피너 드롭다운에서 해당 항목 클릭시
        mSpinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) mStores = storeList.get(5);
                else  mStores = storeList.get(position-1);

                //리스트 바꾸고 RecyclerView 갱신
                adapter.setNewList(mStores);
                adapter.notifyDataSetChanged();

            }

            @Override//기본
            public void onNothingSelected(AdapterView<?> parent) {

                mStores = storeList.get(5);
                adapter.setNewList(mStores);

                adapter.notifyDataSetChanged();

            }



        });




        return view;
    }






}
