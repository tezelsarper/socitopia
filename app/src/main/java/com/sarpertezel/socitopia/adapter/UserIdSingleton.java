package com.sarpertezel.socitopia.adapter;

public class UserIdSingleton {
    private static UserIdSingleton instance;
    private String userId;

    private UserIdSingleton() {

    }

    public static UserIdSingleton getInstance() {
        if (instance == null) {
            instance = new UserIdSingleton();
        }
        return instance;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
