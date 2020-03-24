package com.inhascp.partyhere;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private ArrayList<String> bookmarkPosition;
    private ArrayList<String> meetingKeys;
    private HashMap<String,String> meetingTitle;
    private String nickName;

    public User() {
        bookmarkPosition = new ArrayList<>();
        meetingKeys = new ArrayList<>();
        meetingTitle = new HashMap<>();
        nickName = "";
    }

    public User(ArrayList<String> bookmarkPosition, ArrayList<String> meetingKeys, HashMap<String, String> meetingTitle, String nickName) {
        this.bookmarkPosition = bookmarkPosition;
        this.meetingKeys = meetingKeys;
        this.meetingTitle = meetingTitle;
        this.nickName = nickName;
    }

    public ArrayList<String> getBookmarkPosition() {
        return bookmarkPosition;
    }

    public ArrayList<String> getMeetingKeys() {
        return meetingKeys;
    }

    public HashMap<String, String> getMeetingTitle() {
        return meetingTitle;
    }

    public String getNickName() {
        return nickName;
    }
}
