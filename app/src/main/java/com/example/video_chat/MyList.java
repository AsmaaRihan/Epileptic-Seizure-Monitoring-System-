package com.example.video_chat;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MyList extends AppCompatActivity {

    public static Bitmap picture;
    public static String MedicienType;
    int[] images = {R.drawable.topamax, R.drawable.lamictal, R.drawable.depakene};

    String[] version = {"Topamax", "Lamictal", "Depakene"};

    String[] versionNumber = {"100mg", "1.1", "1.5", "1.6", "2.0"};

    ListView lView;
    ListAdapter Adapter;

    //ListAdapter lAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        lView = (ListView) findViewById(R.id.androidList);

        Adapter = new ListAdapter(getApplicationContext(), version, versionNumber, images);
        lView.setAdapter(Adapter);
        Button save =(Button)findViewById(R.id.button3);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MedicineReminder.class);
                startActivity(intent);
                System.out.println(MedicienType);
            }
        });

        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MedicienType= version[i];
                Toast.makeText(getApplicationContext(),   MedicienType+" "+versionNumber[i], Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),prescription.class);
                startActivity(intent);


            }

        });
    }
    public Bitmap getPicture(){
        if (MedicienType=="Topamax") {
            picture= BitmapFactory.decodeResource(getResources(),R.drawable.topamax);
        }
        else if (MedicienType=="Depakene"){
            picture= BitmapFactory.decodeResource(getResources(),R.drawable.depakene);
        }
        else picture = BitmapFactory.decodeResource(getResources(), R.drawable.lamictal);
        return picture;
    }

    public static String MED()
    {

        return MedicienType;
    }
}
