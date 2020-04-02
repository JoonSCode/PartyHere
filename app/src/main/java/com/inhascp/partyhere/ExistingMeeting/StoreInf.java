package com.inhascp.partyhere.ExistingMeeting;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class StoreInf  implements Serializable {

    private GeoPoint geo;

    private String placeId;
    private String name;
    private ArrayList<String> type;
    private String vicinity;

    public StoreInf(){
        geo = new GeoPoint(0,0);
        placeId = new String("");
        name = new String("");
        type = new ArrayList<>();
        vicinity = new String("");



    }

    public StoreInf(GeoPoint geo_, String placeId_, String name_, ArrayList<String> type_, String vicinity_){
        geo = geo_;
        placeId = placeId_;
        name = name_;
        type = new ArrayList<String>(type_);
        vicinity = vicinity_;
    }

    public GeoPoint getGeo(){ return geo;}

    public String getPlaceId(){return placeId;}
    public String getName(){return name;}
    public ArrayList<String> getType(){return type;}
    public String getVicinity(){return vicinity; }


}
