package com.example.video_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ContactActivity extends AppCompatActivity {

    BottomNavigationView navView;
    RecyclerView myContactList;
    ImageView findPeopleBtn ,back_home;

    private DatabaseReference  contactsRef,userRef ;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private String userName="",profileImage="";
    private String calledBy="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();


       findPeopleBtn=findViewById(R.id.find_people_btn);
       myContactList=findViewById(R.id.contact_list);
       myContactList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
       findPeopleBtn.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent find_people=new Intent(ContactActivity.this,FindPeopleActivity.class);
               startActivity(find_people);
               finish();
           }
       });
       back_home=(ImageView)findViewById(R.id.back_home1);
       back_home.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent home =new Intent(ContactActivity.this,MainActivity.class);
               startActivity(home);
           }
       });

    }

    @Override
    protected void onStart() {
        super.onStart();
        validataUser();

        checkForReceivingCall();

        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef.child(currentUserId),Contacts.class)
                .build();

        FirebaseRecyclerAdapter <Contacts,ContactsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int i, @NonNull Contacts contacts) {

                final String listUserId = getRef(i).getKey();
                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            userName=dataSnapshot.child("name").getValue().toString();
                            profileImage=dataSnapshot.child("Image").getValue().toString();
                            holder.userNameText.setText(userName);
                            Picasso.get().load(profileImage).into(holder.profileImageView);
                        }
                        holder.callBtn.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent calling_intent=new Intent(ContactActivity.this,callingActivity.class);
                                calling_intent.putExtra("visit_use_id",listUserId);
                                startActivity(calling_intent);
                            }
                        });
                        holder.chatBtn.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               Intent chating_intent=new Intent(ContactActivity.this,ChatMainActivity.class);
                               startActivity(chating_intent);

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design, parent, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };
        myContactList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void checkForReceivingCall() {
        userRef.child(currentUserId)
                .child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("ringing")){
                            calledBy=dataSnapshot.child("ringing").getValue().toString();

                            Intent calling_intent=new Intent(ContactActivity.this,callingActivity.class);
                            calling_intent.putExtra("visit_use_id",calledBy);
                            startActivity(calling_intent);
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView userNameText;
        Button callBtn,chatBtn;
        ImageView profileImageView;


        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText= itemView.findViewById(R.id.name_contact);
            callBtn= itemView.findViewById(R.id.call_btn);
            profileImageView= itemView.findViewById(R.id.image_contact);
            chatBtn=itemView.findViewById(R.id.chat_btn);

        }
    }

    private void validataUser(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Intent settings_intent=new Intent(ContactActivity.this,SettingsActivity.class);
                    startActivity(settings_intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
