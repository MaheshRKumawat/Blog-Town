package com.example.blogtown;

import java.sql.Timestamp;

public class BlogPost {
    public String user_id;
    public String image_uri;
    public String desc;
    public Timestamp timestamp;

    public BlogPost(){

    }

    public BlogPost(String user_id, String image_url, String desc, Timestamp timestamp) {
        this.user_id = user_id;
        this.image_uri = image_url;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_uri;
    }

    public void setImage_url(String image_url) {
        this.image_uri = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
