package com.inhascp.partyhere;

import java.util.HashMap;
import java.util.List;

public class Meeting {
    private List<String> meetingType;
    private List<String> memberKeys;
    private HashMap<String,String> memberKeyPlace;
    private HashMap<String,String> recommendPlace;
    private HashMap<String,String> memberKeyName;

    public Meeting() {
    }

    public Meeting(List<String> meetingType, List<String> memberKeys, HashMap<String, String> memberKeyPlace, HashMap<String, String> recommendPlace, HashMap<String, String> memberKeyName) {
        this.meetingType = meetingType;
        this.memberKeys = memberKeys;
        this.memberKeyPlace = memberKeyPlace;
        this.recommendPlace = recommendPlace;
        this.memberKeyName = memberKeyName;
    }

    public List<String> getMeetingType() {
        return meetingType;
    }

    public List<String> getMemberKeys() {
        return memberKeys;
    }

    public HashMap<String, String> getMemberKeyPlace() {
        return memberKeyPlace;
    }

    public HashMap<String, String> getRecommendPlace() {
        return recommendPlace;
    }

    public HashMap<String, String> getMemberKeyName() {
        return memberKeyName;
    }
}
