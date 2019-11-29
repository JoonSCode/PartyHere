package com.inhascp.partyhere;

import java.util.HashMap;
import java.util.List;

public class Meeting {
    private List<String> meetingType;
    private List<String> memberKeys;
    private HashMap<String,String> members;
    private HashMap<String,String> recommendPlace;

    public Meeting() {
    }

    public Meeting(List<String> meetingType, List<String> memberKeys, HashMap<String, String> members, HashMap<String, String> recommendPlace) {
        this.meetingType = meetingType;
        this.memberKeys = memberKeys;
        this.members = members;
        this.recommendPlace = recommendPlace;
    }

    public List<String> getMeetingType() {
        return meetingType;
    }

    public List<String> getMemberKeys() {
        return memberKeys;
    }

    public HashMap<String, String> getMembers() {
        return members;
    }

    public HashMap<String, String> getRecommendPlace() {
        return recommendPlace;
    }
}
