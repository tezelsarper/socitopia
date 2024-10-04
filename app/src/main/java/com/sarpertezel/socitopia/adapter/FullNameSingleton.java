package com.sarpertezel.socitopia.adapter;

public class FullNameSingleton {
    private static FullNameSingleton instance;
    private String fullName;

    private FullNameSingleton() {

    }

    public static FullNameSingleton getInstance() {
        if (instance == null) {
            instance = new FullNameSingleton();
        }
        return instance;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
