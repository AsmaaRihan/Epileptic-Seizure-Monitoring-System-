package com.example.video_chat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class callingActivity extends AppCompatActivity {
    private TextView nameContact;
    private ImageView profileImage;
    private ImageView acceptCallBtn,cancelCallBtn;
    private String receiverUserId="",receiverUserImage="",receiverUserName="";
    private String senderUserId="",senderUserImage="",senderUserName="",checker="";
    private String callingId="",ringingId="";
    private DatabaseReference userRef;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        senderUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_use_id").toString();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mediaPlayer=MediaPlayer.create(this,R.raw.ringing);

        setContentView(R.layout.activity_calling);
        nameContact = findViewById(R.id.name_calling);
        profileImage = findViewById(R.id.profile_image_calling);
        cancelCallBtn = findViewById(R.id.cancel_call);
        acceptCallBtn = findViewById(R.id.make_call);

        cancelCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                checker="clicked";
                cancelCallingUser();
            }
        });

        acceptCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                final HashMap<String ,Object> callingPickUpMap= new HashMap<>();
                callingPickUpMap.put("picked","picked");
                userRef.child(senderUserId).child("Ringing")
                        .updateChildren(callingPickUpMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    mediaPlayer.stop();
                                    Intent intent=new Intent(callingActivity.this,videoChatActivity.class);
                                    startActivity(intent);
                                }

                            }
                        });

            }
        });

        getAndSetUserProfileInf();
    }

    private void getAndSetUserProfileInf() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(receiverUserId).exists())
                {
                    receiverUserImage=dataSnapshot.child(receiverUserId).child("Image").getValue().toString();
                    receiverUserName=dataSnapshot.child(receiverUserId).child("name").getValue().toString();
                }
                nameContact.setText(receiverUserName);
                Picasso.get().load(receiverUserImage).placeholder(R.drawable.profile_image).into(profileImage);
                if(dataSnapshot.child(senderUserId).exists()){
                   senderUserName=dataSnapshot.child(senderUserId).child("Image").getValue().toString();
                   senderUserImage=dataSnapshot.child(senderUserId).child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mediaPlayer.start();

        userRef.child(receiverUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!checker.equals("clicked")&&!dataSnapshot.hasChild("Calling")&&!dataSnapshot.hasChild("Ringing") ){

                    final HashMap<String,Object> callingInfo=new HashMap<>();
                    callingInfo.put("calling",receiverUserId);

                    userRef.child(senderUserId)
                            .child("Calling")
                            .updateChildren(callingInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        final HashMap<String,Object> ringingInfo=new HashMap<>();
                                        ringingInfo.put("ringing",senderUserId);

                                        userRef.child(receiverUserId)
                                                .child("Ringing")
                                                .updateChildren(ringingInfo);
                                    }

                                }
                            });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(senderUserId).hasChild("Ringing")&& !dataSnapshot.child(senderUserId).hasChild("Calling")){
                    acceptCallBtn.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.child(receiverUserId).child("Ringing").hasChild("picked")){
                    mediaPlayer.stop();
                    Intent intent=new Intent(callingActivity.this,videoChatActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void cancelCallingUser() {
        //from sender side
        userRef.child(senderUserId)
                .child("Calling")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()&&dataSnapshot.hasChild("calling")){
                            callingId=dataSnapshot.child("calling").getValue().toString();

                            userRef.child(callingId)
                                    .child("Ringing")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful()){
                                                userRef.child(senderUserId)
                                                        .child("Calling")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                startActivity(new Intent(callingActivity.this,RegistrationActivity.class));
                                                                finish();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else {
                            startActivity(new Intent(callingActivity.this,RegistrationActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        //from receiver side
        userRef.child(senderUserId)
                .child("Ringing")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()&&dataSnapshot.hasChild("ringing")){
                            ringingId=dataSnapshot.child("ringing").getValue().toString();

                            userRef.child(ringingId)
                                    .child("Calling")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful()){
                                                userRef.child(senderUserId)
                                                        .child("Ringing")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                startActivity(new Intent(callingActivity.this,RegistrationActivity.class));
                                                                finish();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else {
                            startActivity(new Intent(callingActivity.this,RegistrationActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
