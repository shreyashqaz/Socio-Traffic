package com.example.myapplication;

class UserMap {
    String username;
    String fullname;
    String mobile;
    String status;
    String gender;
    String profile_pic;
    String uid;
    long total_post;
    long like_post;
    long dislike_post;
    long reported;
    String email;
    public UserMap()
    {}

    public UserMap(String username, String fullname, String mobile, String status, String gender, String profile_pic, String uid, long total_post, long like_post, long dislike_post, long reported, String email) {
        this.username = username;
        this.fullname = fullname;
        this.mobile = mobile;
        this.status = status;
        this.gender = gender;
        this.profile_pic = profile_pic;
        this.uid = uid;
        this.total_post = total_post;
        this.like_post = like_post;
        this.dislike_post = dislike_post;
        this.reported = reported;
        this.email = email;
    }

    public UserMap(String username, String fullname, String mobile, String status, String gender, String profile_pic, String uid, long total_post, long like_post, long dislike_post, long reported) {
        this.username = username;
        this.fullname = fullname;
        this.mobile = mobile;
        this.status = status;
        this.gender = gender;
        this.profile_pic = profile_pic;
        this.uid = uid;
        this.total_post = total_post;
        this.like_post = like_post;
        this.dislike_post = dislike_post;
        this.reported = reported;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getReported() {
        return reported;
    }

    public void setReported(long reported) {
        this.reported = reported;
    }

    public long getLike_post() {
        return like_post;
    }

    public void setLike_post(long like_post) {
        this.like_post = like_post;
    }

    public long getDislike_post() {
        return dislike_post;
    }

    public void setDislike_post(long dislike_post) {
        this.dislike_post = dislike_post;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTotal_post() {
        return total_post;
    }

    public void setTotal_post(long total_post) {
        this.total_post = total_post;
    }



    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }




    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }





}
