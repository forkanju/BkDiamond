package com.bechakeena.bkdiamond.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Notification extends RealmObject {

    @PrimaryKey
    public String id = UUID.randomUUID().toString();
    @SerializedName("timestamp")
    @Expose
    private long timestamp;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("message")
    @Expose
    private String message;

    public Notification() {
    }

    public Notification(long timestamp, String title, String message) {
        this.timestamp = timestamp;
        this.title = title;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
