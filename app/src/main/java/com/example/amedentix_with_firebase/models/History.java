package com.example.amedentix_with_firebase.models;

import com.example.amedentix_with_firebase.activities.HistoryId;

import java.util.ArrayList;
import java.util.Date;

public class History extends com.example.amedentix_with_firebase.activities.HistoryId {

    public String user_id, image_url, description, image_thumb, title;
    public Date timestamp;

    public History() {
    }

    public History(String user_id, String image_url, String description, String image_thumb, Date timestamp, String title) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.description = description;
        this.title = title;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
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

    public String getDesc() {
        return description;
    }

    public void setDesc(String description) {
        this.description = description;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
