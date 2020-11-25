package com.example.video_chat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    MyList myList=new MyList();
    MedicineReminder medicineReminder =new MedicineReminder();
    private static final String CHANNAL_ID="package com.example.notificationcannal";
    private static final String CHANNAL_NAME="Channel";
    private NotificationManager manager;
    public NotificationHelper(Context base) {
        super(base);
        createChannel();
    }

    private void createChannel() {
        //IMPORTANCE_DEFAULT=show everywhere
       NotificationChannel rehabchannal=new NotificationChannel( CHANNAL_ID,CHANNAL_NAME,NotificationManager.IMPORTANCE_HIGH);
       rehabchannal.enableLights(true);
       rehabchannal.enableVibration(true);
       rehabchannal.setLightColor(Color.GREEN);
       rehabchannal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
       rehabchannal.enableVibration(true);
       getManager().createNotificationChannel(rehabchannal);

    }

    public NotificationManager getManager() {
        if (manager== null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }
    public NotificationCompat.Builder getChannnelNotification(String title, String body){
       // Bitmap picture= BitmapFactory.decodeResource(getResources(),R.drawable.sleep);

        Bitmap picture ;
             //   = BitmapFactory.decodeResource(getResources(),
               // R.drawable.sleep);
       String DOSE=MedicineReminder.getDOSE();
       String dose_text=title+body;
       String MedicineType =myList.MED();
        {
            if (MedicineType=="Topamax") {
                picture= BitmapFactory.decodeResource(getResources(),R.drawable.topamax);
            }
            else if (MedicineType=="Depakene"){
                picture= BitmapFactory.decodeResource(getResources(),R.drawable.depakene);
            }
            else picture = BitmapFactory.decodeResource(getResources(), R.drawable.lamictal);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext(),CHANNAL_ID)
                .setAutoCancel(true)
                .setContentTitle(MedicineType)
                .setSmallIcon(R.drawable.epidoum)
                .setContentText(dose_text)
                .setLargeIcon(picture);

        NotificationCompat.BigPictureStyle bigPictureStyle=new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(picture);
        mBuilder.setStyle(bigPictureStyle);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getApplicationContext(),
                MainActivity.class);

        // The stack builder object will contain an artificial back
        // stack for
        // the
        // started Activity.
        // getApplicationContext() ensures that navigating backward from
        // the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder
                .create(getApplicationContext());

        // Adds the back stack for the Intent (but not the Intent
        // itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the
        // stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        return mBuilder;
    }
}
