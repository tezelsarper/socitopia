package com.sarpertezel.socitopia.model;

public class UserRegister {

    public String email;
    public String password;
    public String fullName;
    public String userName;



    public UserRegister(String email, String password, String fullName, String userName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }
}
