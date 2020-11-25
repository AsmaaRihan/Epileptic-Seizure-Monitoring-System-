package com.example.video_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ChatMainActivity extends AppCompatActivity {


    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private Toolbar toolbar ;  // al 7eta ak 5adra akt fo2
    String CurrentUser;
    private sectionsPagerAdapter sectionsPagerAdapter;
    ImageView find_people;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

        auth = FirebaseAuth.getInstance();
        CurrentUser= auth.getCurrentUser().getUid();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser);

        toolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ask your Doctors");

        //Tabs
        ViewPager viewPager = findViewById(R.id.view_pager);
        sectionsPagerAdapter = new sectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        find_people=(ImageView)findViewById(R.id.find_people_chat_btn);
        find_people.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent find_people=new Intent(ChatMainActivity.this,FindPeopleActivity.class);
                startActivity(find_people);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_settings_btn )
        {

            startActivity(new Intent(ChatMainActivity.this, SettingsActivity.class ));
        }
        if(item.getItemId() == R.id.home_btn )
        {

            startActivity(new Intent(ChatMainActivity.this, MainActivity.class ));
        }
        return true;
    }

   /* @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser == null ) {
            SendToStart();
        }
        else{
            UpdateUserStatus("Online");
//            UserRef.child("online").setValue("true");

        }
    }
    private void SendToStart() {
        startActivity(new Intent(MainActivity.this, StartActivity.class ));

    }*/
    private void UpdateUserStatus(String State){
        String saveCurrentDate, saveCurrentTime;
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate= currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime= new SimpleDateFormat("hh:mm a ");
        saveCurrentTime= currentTime.format(calendar.getTime());
        HashMap<String,Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time",saveCurrentTime);
        onlineStateMap.put("date",saveCurrentDate);
        onlineStateMap.put("state",State);

        UserRef.child("UserState").setValue(onlineStateMap);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (CurrentUser != null ) {
            UpdateUserStatus("Offline");

//            UserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CurrentUser != null ) {
            UpdateUserStatus("Offline");

//            UserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
