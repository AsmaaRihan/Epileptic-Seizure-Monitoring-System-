package com.example.video_chat;

//  get and set data of List viewer
public class get_setDataUser {
    String name;
    String status;

    public get_setDataUser() {
    }

    public get_setDataUser(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
