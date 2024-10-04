package com.sarpertezel.socitopia.adapter;

public class GroupIdSingleton {
    private static GroupIdSingleton instance;
    private String groupId;

    private GroupIdSingleton() {

    }

    public static GroupIdSingleton getInstance() {
        if (instance == null) {
            instance = new GroupIdSingleton();
        }
        return instance;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }
}

