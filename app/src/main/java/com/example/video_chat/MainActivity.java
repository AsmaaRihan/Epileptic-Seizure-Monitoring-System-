package com.example.video_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navView;
    ImageView medicine;
    ImageView AskyourDoctor,contact_us,FAQ,calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = (BottomNavigationView)findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        medicine=(ImageView)findViewById(R.id.bus1);
        medicine.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent=new Intent(getApplicationContext(),MedicineReminder.class);
                startActivity(intent);
            }
        });
        AskyourDoctor=(ImageView)findViewById(R.id.bus);
        AskyourDoctor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ask_doctor=new Intent(getApplicationContext(),ChatMainActivity.class);
                startActivity(ask_doctor);

            }
        });
        contact_us=(ImageView)findViewById(R.id.follow_us);
        contact_us.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent follow_us =new Intent(MainActivity.this,Follow_us.class);
                startActivity(follow_us);
            }
        });
        FAQ=(ImageView)findViewById(R.id.FAQ_View);
        FAQ.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent FAQ =new Intent(MainActivity.this,FAQ.class);
                startActivity(FAQ);
            }
        });
        calendar=(ImageView)findViewById(R.id.calendar);
        calendar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent FAQ =new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(FAQ);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {

                        case R.id.navigation_home:
                            Intent mainintent=new Intent(MainActivity.this, MainActivity.class);
                            startActivity(mainintent);
                            break;
                        case R.id.navigation_contact:
                            Intent contanctIntent=new Intent(MainActivity.this, ContactActivity.class);
                            startActivity(contanctIntent);
                            break;
                        case R.id.navigation_settings:
                            Intent stteingsintent=new Intent(MainActivity.this,SettingsActivity.class);
                            startActivity(stteingsintent);
                            break;
                        case R.id.navigation_notifications:
                            Intent notificationsintent=new Intent(MainActivity.this, NotificationsActivity.class);
                            startActivity(notificationsintent);
                            break;
                        case R.id.navigation_logout:
                            FirebaseAuth.getInstance().signOut();
                            Intent logoutintent=new Intent(MainActivity.this, RegistrationActivity.class);
                            startActivity(logoutintent);
                            finish();
                            break;
                    }
                    return true;
                }
            };
}
