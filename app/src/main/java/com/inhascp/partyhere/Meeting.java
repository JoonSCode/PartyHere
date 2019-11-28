package com.inhascp.partyhere;

import java.util.HashMap;
import java.util.List;

public class Meeting {
    private List<String> MeetingType;
    private List<String> MemberKeys;
    private HashMap<String,String> Members;
    private HashMap<String,String> RecommendPlace;

    public Meeting() {
    }

    public Meeting(List<String> meetingType, List<String> memberKeys, HashMap<String, String> members, HashMap<String, String> recommendPlace) {
        MeetingType = meetingType;
        MemberKeys = memberKeys;
        Members = members;
        RecommendPlace = recommendPlace;
    }

    public List<String> getMeetingType() {
        return MeetingType;
    }

    public List<String> getMemberKeys() {
        return MemberKeys;
    }

    public HashMap<String, String> getMembers() {
        return Members;
    }

    public HashMap<String, String> getRecommendPlace() {
        return RecommendPlace;
    }

}
