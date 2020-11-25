package com.example.video_chat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MedicineReminder extends AppCompatActivity {
    Button Alarm,medicine;
    EditText no_days,Dose;
    public static String  DOSE_TEXT ;
    int mHour;
    int mMinute;
    int number_of_days;
    MyList list=new MyList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_reminder);
        no_days= (EditText)findViewById(R.id.no_days);
        Dose= (EditText)findViewById(R.id.dose);
        DOSE_TEXT= Dose.getText().toString();
        try {
            number_of_days = Integer.parseInt(no_days.getText().toString());
        }catch (NumberFormatException ex) {
            // Output expected NumberFormatException.
            //Toast.makeText(getApplicationContext(),"ENTER NUMBER PLE",Toast.LENGTH_SHORT).show();
        }
        TextView textView=(TextView)findViewById(R.id.textView3) ;
        String MED= list.MED();
        textView.setText(MED);
        // LISTVIEW INTENT
        medicine=(Button)findViewById(R.id.button2);
        medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent medicine = new Intent(MedicineReminder.this,
                        MyList.class);
                startActivity(medicine);
            }
        });


        Alarm=(Button)findViewById(R.id.button);
        Alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(no_days.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Fields are empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    Time_Calendar();
                }
            }
        });
    }

    public void Time_Calendar()
    {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        //if(settxttime==1) {timebutton.setText(hourOfDay + ":" + minute);}
                        //if(settxttime==2) {timebutton2.setText(hourOfDay + ":" + minute);}
                        //if(settxttime==3) {timebutton3.setText(hourOfDay + ":" + minute);}
                        Calendar calNow = Calendar.getInstance();
                        Calendar  calSet ;

                        calSet = (Calendar) calNow.clone();
                        calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calSet.set(Calendar.MINUTE, minute);
                        calSet.set(Calendar.SECOND, 0);
                        calSet.set(Calendar.MILLISECOND, 0);
                        if(calSet.compareTo(calNow) <= 0){
                            calSet.add(Calendar.DATE, 1);
                        }
                        setAlarm(calSet);
                        Toast.makeText(getApplicationContext(), "Alarm Set...", Toast.LENGTH_LONG).show();

                    }
                }, mHour, mMinute, false);

        timePickerDialog.show();
    }


    private void setAlarm(Calendar targetCal){
        int i = Integer.parseInt(no_days.getText().toString());
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,MyBroadcastReceiver.class);
        System.out.println("title_dose  "+DOSE_TEXT);
        intent.putExtra("title_doze", DOSE_TEXT);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        // cal.add(Calendar.SECOND, 5);
        alarmMgr.set(AlarmManager.RTC, targetCal.getTimeInMillis(), pendingIntent);

         for(int con=1;i<=number_of_days;con++) {
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), number_of_days* 24 * 60 * 60 * 1000, pendingIntent);
    }
    }
    // get dose
    public  static String getDOSE(){

        return DOSE_TEXT;
    }



}

