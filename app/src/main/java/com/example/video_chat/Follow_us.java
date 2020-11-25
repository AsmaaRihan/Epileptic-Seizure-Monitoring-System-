package com.example.video_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Follow_us extends AppCompatActivity {
    ImageView home , whatsapp,website,call_us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_us);
        home=(ImageView)findViewById(R.id.back_home);
        whatsapp=(ImageView)findViewById(R.id.whatsApp);
        website=(ImageView)findViewById(R.id.webSite);
        call_us=(ImageView)findViewById(R.id.phone);
        home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home=new Intent(Follow_us.this,MainActivity.class);
                startActivity(home);
            }
        });
        whatsapp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent whatsapp=new Intent(Follow_us.this,MainActivity.class);
                startActivity(whatsapp);
            }
        });

    }
}
