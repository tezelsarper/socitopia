package com.sarpertezel.socitopia.adapter;


public class profilePicUrlSingleton {
    private static profilePicUrlSingleton instance;
    private String profilePhotoUrl;

    private profilePicUrlSingleton() {
    }

    public static profilePicUrlSingleton getInstance() {
        if (instance == null) {
            synchronized (profilePicUrlSingleton.class) {
                if (instance == null) {
                    instance = new profilePicUrlSingleton();
                }
            }
        }
        return instance;
    }

    public void setProfilePhotoUrl(String url) {
        this.profilePhotoUrl = url;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }
}

