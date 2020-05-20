package com.inhascp.partyhere.ui.main;

import java.util.List;

public class MeetingListItem {
    private String mName;
    private String mMeetingKey;
    private Integer mNumOfPerson;
    private Integer mMeetingType;

    public Integer getmMeetingType() {
        return mMeetingType;
    }

    public void setmMeetingType(Integer mMeetingType) {
        this.mMeetingType = mMeetingType;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmMeetingKey() {
        return mMeetingKey;
    }

    public void setmMeetingKey(String mMeetingKey) {
        this.mMeetingKey = mMeetingKey;
    }

    public Integer getmNumOfPerson() {
        return mNumOfPerson;
    }

    public void setmNumOfPerson(Integer mNumOfPerson) {
        this.mNumOfPerson = mNumOfPerson;
    }
}
