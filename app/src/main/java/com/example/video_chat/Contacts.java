package com.example.video_chat;

public class Contacts {
    String name,Image,Status,uid;

    public Contacts() {
    }

    public Contacts(String name, String Image, String Status, String uid) {
        this.name = name;
        this.Image = Image;
        this.Status = Status;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
