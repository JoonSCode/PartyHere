package com.inhascp.partyhere.ExistingMeeting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class StoreInf implements Parcelable {

    private GeoPoint geo;

    private String placeId;
    private String name;
    private ArrayList<String> type;
    private String vicinity;

    public static final Creator<StoreInf> CREATOR = new Creator<StoreInf>() {
        @Override
        public StoreInf createFromParcel(Parcel in) {
            return new StoreInf(in);
        }

        @Override
        public StoreInf[] newArray(int size) {
            return new StoreInf[size];
        }
    };

    public StoreInf(GeoPoint geo_, String placeId_, String name_, ArrayList<String> type_, String vicinity_){
        geo = geo_;
        placeId = placeId_;
        name = name_;
        type = new ArrayList<String>(type_);
        vicinity = vicinity_;
    }

    public StoreInf() {
        geo = new GeoPoint(0, 0);
        placeId = "";
        name = "";
        type = new ArrayList<>();
        vicinity = "";


    }

    protected StoreInf(Parcel in) {
        placeId = in.readString();
        name = in.readString();
        type = in.createStringArrayList();
        vicinity = in.readString();
    }

    public GeoPoint getGeo(){ return geo;}

    public String getPlaceId(){return placeId;}
    public String getName(){return name;}
    public ArrayList<String> getType(){return type;}
    public String getVicinity(){return vicinity; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeDouble(geo.getLatitude());
        parcel.writeDouble(geo.getLongitude());

    }
}
