package com.example.emre.bloodbankcyprus;


import java.util.Date;

public class SeekPost extends SeekPostId
{
    public String user_id;
    public String image_url;
    public String desc;
    public Date timestamp;
    public String blood_type;
    public String location;


    public String image_thumb;


    public SeekPost() {}

    public SeekPost(String user_id, String image_url, String desc, String blood_type, String location, String image_thumb, Date timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.blood_type = blood_type;
        this.location = location;
        this.timestamp = timestamp;
        this.image_thumb=image_thumb;

    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public String getImage_thumb() { return image_thumb; }

    public void setImage_thumb(String image_thumb) {this.image_thumb = image_thumb; }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setLocation(String location) {this.location = location; }

    public String getBlood_type() {
        return blood_type;
    }

    public void setBlood_type(String blood_type) {
        this.blood_type = blood_type;
    }

    public String getLocation() {
        return location;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
