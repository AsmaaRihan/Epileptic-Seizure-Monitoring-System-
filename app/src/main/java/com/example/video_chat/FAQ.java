package com.example.video_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class FAQ extends AppCompatActivity {

    String[] Questions = {"What is the epilepsy symptoms? ", "What is the effect of epilepsy medicine ?"};
    ImageView home ;
    ListView QuestionList ;
    QuestionListAdapter Adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        QuestionList=(ListView)findViewById(R.id.QList_List);
        Adapter=new QuestionListAdapter(getApplicationContext(),Questions);
        QuestionList.setAdapter(Adapter);
        home=(ImageView)findViewById(R.id.back_home_btn_faq);
        home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home =new Intent(FAQ.this ,MainActivity.class);
                startActivity(home);
            }
        });
        QuestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){

                }

            }
    });
}
}
