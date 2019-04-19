package com.example.myapplication;

public class Post {
     public String uid,post_date,post_time,post_title,user_profile_image,user_full_name,post_description,post_image;
     public Double post_lat,post_long;
     public int likes,dislikes,reports;
     public String postid;
     public String rid1,rid2,rid3,rid4,rid5;
     public String postloc;

     public Post()
     {

     }

    public String getPostloc() {
        return postloc;
    }

    public void setPostloc(String postloc) {
        this.postloc = postloc;
    }

    public Post(String uid, String post_date, String post_time, String post_title, String user_profile_image, String user_full_name, String post_description, String post_image, Double post_lat, Double post_long, int likes, int dislikes, int reports, String postid, String rid1, String rid2, String rid3, String rid4, String rid5, String postloc) {
        this.uid = uid;
        this.post_date = post_date;
        this.post_time = post_time;
        this.post_title = post_title;
        this.user_profile_image = user_profile_image;
        this.user_full_name = user_full_name;
        this.post_description = post_description;
        this.post_image = post_image;
        this.post_lat = post_lat;
        this.post_long = post_long;
        this.likes = likes;
        this.dislikes = dislikes;
        this.reports = reports;
        this.postid = postid;
        this.rid1 = rid1;
        this.rid2 = rid2;
        this.rid3 = rid3;
        this.rid4 = rid4;
        this.rid5 = rid5;
        this.postloc = postloc;
    }

    public String getRid1() {
        return rid1;
    }

    public void setRid1(String rid1) {
        this.rid1 = rid1;
    }

    public String getRid2() {
        return rid2;
    }

    public void setRid2(String rid2) {
        this.rid2 = rid2;
    }

    public String getRid3() {
        return rid3;
    }

    public void setRid3(String rid3) {
        this.rid3 = rid3;
    }

    public String getRid4() {
        return rid4;
    }

    public void setRid4(String rid4) {
        this.rid4 = rid4;
    }

    public String getRid5() {
        return rid5;
    }

    public void setRid5(String rid5) {
        this.rid5 = rid5;
    }



    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getReports() {
        return reports;
    }

    public void setReports(int reports) {
        this.reports = reports;
    }

    public Double getPost_lat() {
        return post_lat;
    }

    public void setPost_lat(Double post_lat) {
        this.post_lat = post_lat;
    }

    public Double getPost_long() {
        return post_long;
    }

    public void setPost_long(Double post_long) {
        this.post_long = post_long;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getUser_profile_image() {
        return user_profile_image;
    }

    public void setUser_profile_image(String user_profile_image) {
        this.user_profile_image = user_profile_image;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public String getPost_description() {
        return post_description;
    }

    public void setPost_description(String post_description) {
        this.post_description = post_description;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }
}
