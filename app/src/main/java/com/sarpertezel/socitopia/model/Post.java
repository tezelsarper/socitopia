package com.sarpertezel.socitopia.model;

public class Post {
    public String userId;

    public String fullName;
    public String email;
    public String comment;
    public String downloadUrl;

    public String profilePicUrl;

    public Post(String userId,String fullName,String email, String comment, String downloadUrl,String profilePicUrl) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.comment = comment;
        this.downloadUrl = downloadUrl;
        this.profilePicUrl = profilePicUrl;

    }
}
