package com.example.video_chat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivityChat extends AppCompatActivity {

    private TextView profileName,profileStatus, profileFriendsCount;
    private Button profileSendReqbtn;

    private DatabaseReference Usersdatabase,UserRef;
    private DatabaseReference FriendReqDatabase;
    private DatabaseReference FriendDatabase;

    private FirebaseUser current_user;
    private String current_state;
    private CircleImageView ProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_chat);

        // al profile aly bdos 4leeh mn al list
        final String user_id = getIntent().getStringExtra("user_id");
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FriendReqDatabase= FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        FriendDatabase= FirebaseDatabase.getInstance().getReference().child("Contacts");

        //account Holder
        current_user= FirebaseAuth.getInstance().getCurrentUser();
        Usersdatabase= UserRef.child(user_id);

        profileName = findViewById(R.id.profileName);
        profileStatus = findViewById(R.id.profileStatus);
        ProfileImage = findViewById(R.id.ProfileImage);

        profileSendReqbtn = findViewById(R.id.profileSendReqbtn);


        current_state = "not_friends";


        Usersdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();
                String image = dataSnapshot.child("Image").getValue().toString();

                if(!image.equals("default")){
                    Picasso.get().load(image).into(ProfileImage);
                }
                profileName.setText(display_name);
                profileStatus.setText(status);

                // how my profile will appear when make a request
                FriendReqDatabase.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //checking if there's a user name (aly b3telo req)
                        if (dataSnapshot.hasChild(user_id)){
                            String req_type= dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")){
                                current_state = "req_received";
                                profileSendReqbtn.setText("Accept Friend Request");

                            } else if(req_type.equals("sent"))
                            {
                                current_state = "not_friends";
                                profileSendReqbtn.setText("Cancel Friend Request");

                            } else{
                                FriendDatabase.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        current_state = "friends";
                                        profileSendReqbtn.setText("UnFriend this person");


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /// send friend request
        profileSendReqbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // SEND FRIEND Req
                if (current_state.equals("not_friends"))
                {
                    profileSendReqbtn.setEnabled(false);

                    FriendReqDatabase.child(current_user.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                FriendReqDatabase.child(user_id).child(current_user.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        current_state = "req_sent";
                                        profileSendReqbtn.setText("Cancel Friend Request");

                                    }
                                });

                            }else {
                                Toast.makeText(ProfileActivityChat.this,"Faild Sending Request",Toast.LENGTH_LONG).show();
                            }
                            profileSendReqbtn.setEnabled(true);

                        }
                    });


                }
                // CANCEL FRIEND Req
                if (current_state.equals("req_sent"))
                {
                    FriendReqDatabase.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FriendReqDatabase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    profileSendReqbtn.setEnabled(true);
                                    current_state = "not_friends";
                                    profileSendReqbtn.setText("send Friend Request");

                                }
                            });
                        }
                    });
                }

                // Friend Received
                if(current_state.equals("req_received")){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    FriendDatabase.child(current_user.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FriendDatabase.child(user_id).child(current_user.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    FriendReqDatabase.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FriendReqDatabase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    profileSendReqbtn.setEnabled(true);
                                                    current_state = "friends";
                                                    profileSendReqbtn.setText("UnFriend this person");

                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

            }


        });

    }
}
