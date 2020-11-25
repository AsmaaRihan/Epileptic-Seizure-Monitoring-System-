package com.example.video_chat;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class prescription extends AppCompatActivity {
     TextView prescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescription);
        prescription=(TextView)findViewById(R.id.prescription);
    prescription.setMovementMethod(new ScrollingMovementMethod());

    }
}
