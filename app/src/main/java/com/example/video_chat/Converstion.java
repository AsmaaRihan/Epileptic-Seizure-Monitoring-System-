package com.example.video_chat;

public class Converstion {
    public boolean seen;
    public String timeStame;
    public Converstion(){

    }
    public Converstion(boolean seen, String timeStame) {
        this.seen = seen;
        this.timeStame = timeStame;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getTimeStame() {
        return timeStame;
    }

    public void setTimeStame(String timeStame) {
        this.timeStame = timeStame;
    }
}
