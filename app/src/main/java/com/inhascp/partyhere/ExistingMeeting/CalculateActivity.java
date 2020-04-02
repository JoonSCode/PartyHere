package com.inhascp.partyhere.ExistingMeeting;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inhascp.partyhere.R;
import com.inhascp.partyhere.login.LoginActivity;
import com.inhascp.partyhere.ui.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CalculateActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();;
    private final int PLACE_SIZE = 130;

    private String MEETING_KEY;
    private String USER_KEY = LoginActivity.USER_KEY;
    private String startPoint;

    private List<Pair<String, LatLng>> PLACE;
    private BackgroundCalculate task;
    private List<String> apiResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);


        MEETING_KEY = getIntent().getStringExtra("MEETING_KEY");
        startPoint = getIntent().getStringExtra("START_POINT");
        task = new BackgroundCalculate();
        PLACE = MainActivity.PLACE;
        apiResult = new ArrayList<>();

        task.execute();
    }
//background 에서 수행 함수---------------------------------------------------------------------------------------
//( USER_KEY에 해당하는 유저의 Start_Point에 대해서 모든 상권으로의 이동시간을 api를 통해 가져옴)
    public class BackgroundCalculate extends AsyncTask<URL, Integer, StringBuilder> {

        private static final String DIRECTIONS_API_BASE = "https://maps.googleapis.com/maps/api/distancematrix";
        private static final String OUT_JSON = "/json";

        // API KEY of the project Google Map Api For work
        private final String API_KEY = getString(R.string.google_api_key);

        @Override
        protected StringBuilder doInBackground(URL... params) {

            HttpURLConnection mUrlConnection = null;

            try {
                for (int n = 0; n < PLACE_SIZE; n++) {//모든 상권에 대해 소요시간 검색(api url 만들기)
                    StringBuilder mJsonResults = new StringBuilder();
                    StringBuilder sb = new StringBuilder(DIRECTIONS_API_BASE + OUT_JSON);
                    sb.append("?origins=");
                    sb.append(URLEncoder.encode(startPoint, "utf8"));
                    sb.append("&destinations=");
                   // Log.v("범위", n+1+"부터" );
                    for (int k = n; k < n + 23; k++) {//행렬이 최대 25개까지 된다는거 같은데 일단 출발점 1개 도착점 23개 총24개로 테스트  url을 만드는 중
                        LatLng latLng = PLACE.get(k).second;
                        String lat = Double.toString(latLng.latitude);
                        String lng = Double.toString(latLng.longitude);
                        sb.append(URLEncoder.encode(lat + "," + lng, "utf8"));
                        if (k == PLACE_SIZE - 1 || k == n + 22) {
                            System.out.println(lat +", " + lng);
                            n = k;
                            break;
                        } else
                            sb.append(URLEncoder.encode("|", "utf8"));
                    }
                    //Log.v("범위", n+1+"까지" );
                    sb.append("&departure_time=now&mode=transit&key=" + API_KEY);
                    URL url = new URL(sb.toString());
                    System.out.println("api : " + url);


                    mUrlConnection = (HttpURLConnection) url.openConnection();//url을 실행하여 결과값을 받아 string으로 변환함
                    InputStreamReader in = new InputStreamReader(mUrlConnection.getInputStream());
                    int read;
                    char[] buff = new char[1024];
                    while ((read = in.read(buff)) != -1) {
                        mJsonResults.append(buff, 0, read);
                    }

                    String result = mJsonResults.toString();
                    //Log.v("결과", "JSON결과"+mJsonResults);
                    apiResult.add(result);
                }
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (mUrlConnection != null) {
                    mUrlConnection.disconnect();
                }
            }
            return new StringBuilder();
        }
        @Override
        protected void onPostExecute(StringBuilder stringBuilder) {
            calculateTime();
        }
    }
//api에서 가져온 결과 string을 json형식으로 파싱하고 시간 정보만 추려서 db에 업로드------------------------------

    protected void calculateTime() {
        HashMap<String, List<Integer>> memberTransferTime = new HashMap<>();
        List<Integer> timeList = new ArrayList<>();

        for (int n = 0; n < apiResult.size(); n++) {
            int size = 23;
            if (n == apiResult.size() - 1)
                size = 15;
            try {
                JSONArray jsonTime = new JSONObject(apiResult.get(n)).getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements");
               // Log.v("결과", "idx: "+n + "값: "+jsonTime );

                for (int p = 0; p < size; p++) {
                    int time = jsonTime.getJSONObject(p)//p번째의 정보
                            .getJSONObject("duration").getInt("value");
                    time /= 60;//분단위로 변경
                    timeList.add(time);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        memberTransferTime.put(USER_KEY, timeList);

        db.collection("Meeting").document(MEETING_KEY).update(
                "memberTransferTime."+USER_KEY ,timeList
        );
        System.out.println("장소: "+ memberTransferTime);
        System.out.println("장소 추리기 끝");

        finish();
    }

}
