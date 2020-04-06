package com.inhascp.partyhere.ExistingMeeting;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.inhascp.partyhere.R;
import com.inhascp.partyhere.login.LoginActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;


public class ChangePosition extends AppCompatActivity
        implements OnMapReadyCallback,
        OnRequestPermissionsResultCallback{


    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String USER_KEY;
    private String MEETING_KEY;
    private GoogleMap mMap;
    private Marker currentMarker = null;
    private EditText mEtPlace;
    private String markerTitle;
    private ImageButton searchMyPositionBtn;
    private ImageButton mBtnNext;
    private ImageButton mBtnSearch;
    private Geocoder geocoder;

    private static final String TAG = "Change Position";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    boolean needRequest = false;

    Location mCurrentLocation;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    private String mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_input_place);
        USER_KEY = LoginActivity.USER_KEY;


        mEtPlace = findViewById(R.id.activity_input_place_et_place);
        searchMyPositionBtn = findViewById(R.id.search_my_position);
        mBtnNext = findViewById(R.id.activity_input_place_btn_next);
        mBtnSearch = findViewById(R.id.activity_input_place_btn_search_place);
        MEETING_KEY = getIntent().getStringExtra("MEETING_KEY");
        mPlace = getIntent().getStringExtra("PLACE");

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        showDialogForLocationServiceSetting();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mEtPlace.setText(mPlace);
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("Meeting").document(MEETING_KEY)
                        .update(
                                "memberKeyPlace." + USER_KEY, mEtPlace.getText().toString()
                        );
                Intent intent = new Intent(getApplicationContext(), CalculateActivity.class);
                intent.putExtra("MEETING_KEY", MEETING_KEY);
                intent.putExtra("START_POINT", mEtPlace.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }

//Ted permission 함수----------------------------------------------------------------------------------


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // 권한 승인이 필요없을 때 실행할 함수
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(ChangePosition.this, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23){ // 마시멜로(안드로이드 6.0) 이상 권한 체크
            TedPermission.with(getApplicationContext())
                    .setPermissionListener(permissionlistener)
                    .setRationaleMessage("위치 설정을 위해서 접근 권한이 필요합니다")
                    .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                    .setPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION})
                    .check();

        } else {
            // 권한 승인이 필요없을 때 실행할 함수
        }
    }

 //----------------------------------------------------------------------------------------------------------
    //구글 맵 관련 함수----------------------------------------------------------------------------------
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");
        geocoder = new Geocoder(this);
        mMap = googleMap;
        mBtnSearch.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                String str= mEtPlace.getText().toString();
                List<Address> addressList = null;
                try {
                    // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                    addressList = geocoder.getFromLocationName(
                            str, // 주소
                            10); // 최대 검색 결과 개수
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(addressList.get(0).toString());
                // 콤마를 기준으로 split
                String []splitStr = addressList.get(0).toString().split(",");
                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                mEtPlace.setText(address);
                System.out.println(address);

                String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도

                String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도


                // 좌표(위도, 경도) 생성
                LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                // 마커 생성
                setCurrentLocation(point, "search result", address);
            }
        });
        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();
        System.out.println("onMapready");

        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                markerTitle = getCurrentAddress(latLng);
                String markerSnippet = "위도:" + (location.getLatitude())
                        + " 경도:" + (location.getLongitude());

                setCurrentLocation(latLng, markerTitle, markerSnippet);

                mEtPlace.setText(markerTitle);

                Log.d( TAG, "onMapClick :" + latLng);
            }
        });

        searchMyPositionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("내위치 찾기 시작");
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        });

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + location.getLatitude()
                        + " 경도:" + location.getLongitude();

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                if(!mPlace.equals("") && mEtPlace.getText().toString().equals(mPlace)){
                    System.out.println("et: " +mEtPlace.getText().toString() + "                  mPlace: "+mPlace);
                    mFusedLocationClient.removeLocationUpdates(locationCallback);
                }
                else {
                    //현재 위치에 마커 생성하고 이동
                    setCurrentLocation(currentPosition, markerTitle, markerSnippet);
                    System.out.println("result");
                    mEtPlace.setText(markerTitle);
                    mCurrentLocation = location;
                    mFusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }
        }

    };




    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
        checkPermissions();//tedpermissiong을 이용한 권한 체크
        System.out.println("onStart");


        if (mMap!=null)
            mMap.setMyLocationEnabled(true);
    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

//지오코더 함수--------------------------------------------------------------------------------------------------
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            System.out.println("출력!!!!!!!!!!!!: "+address.getAddressLine(0));
            return address.getAddressLine(0);
        }

    }

//-----------------------------------------------------------------------------------------------------
    //지도 위치 설정 함수------------------------------------------------------------------------------
    public void setCurrentLocation(LatLng currentLatLng, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();



        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = newLatLngZoom(currentLatLng, 15);
        mMap.moveCamera(cameraUpdate);

    }

//초기 지도 위치 설정 함수---------------------------------------------------------------------------
    public void setDefaultLocation() {

        List<Address> addressList = null;
        try {
            if(mPlace.equals("")){
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                return;
            }
            else {
                // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                addressList = geocoder.getFromLocationName(
                        mPlace, // 주소
                        10); // 최대 검색 결과 개수
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(addressList.get(0).toString());
        // 콤마를 기준으로 split
        String []splitStr = addressList.get(0).toString().split(",");
        String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
        System.out.println(address);

        String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도

        String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도


        // 좌표(위도, 경도) 생성
        LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        // 마커 생성
        setCurrentLocation(point, "search result", address);

    }

//-----------------------------------------------------------------------------------------------------------------
    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePosition.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");

                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }



}