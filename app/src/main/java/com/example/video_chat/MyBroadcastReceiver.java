package com.example.video_chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public MyBroadcastReceiver(){
        super();
    }
    public static final String ALARM_ALERT_ACTION ="com.android.alarmclock.ALARM_ALERT";
    public static final String ALARM_INTENT_EXTRA = "intent.extra.alarm";

    @Override
    public void onReceive(Context context, Intent intent) {

         String dose = intent.getStringExtra("title_doze");
         String title="your Dose:";
         NotificationHelper notificationHelper=new NotificationHelper(context);
         NotificationCompat.Builder nb=notificationHelper.getChannnelNotification(title,dose);
         notificationHelper.getManager().notify(1,nb.build());
    }
}
