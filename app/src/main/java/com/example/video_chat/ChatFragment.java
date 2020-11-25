package com.example.video_chat;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private View ChatsFragView;
    private RecyclerView ChatsList;
    private DatabaseReference UserRef,MsgRef,ConvRef;
    private FirebaseAuth auth;
    String Current_User;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ChatsFragView = inflater.inflate(R.layout.fragment_chat, container, false);
        ChatsList = ChatsFragView.findViewById(R.id.list);
        ChatsList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        ChatsList.setHasFixedSize(true);
        ChatsList.setLayoutManager(linearLayoutManager);

        auth = FirebaseAuth.getInstance();
        Current_User= auth.getCurrentUser().getUid();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ConvRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(Current_User);
        MsgRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(Current_User);

        return ChatsFragView;

    }

    @Override
    public void onStart() {
        super.onStart();
        Query conversationQuery= MsgRef.orderByChild("time");
        FirebaseRecyclerOptions<Converstion> options= new FirebaseRecyclerOptions.Builder<Converstion>()
                .setQuery(conversationQuery, Converstion.class)
                .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Converstion, ConvViewHolder>(options) {
            @NonNull
            @Override
            public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout,parent,false);
                ConvViewHolder holder = new ChatFragment.ConvViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final ConvViewHolder convViewHolder, int i, @NonNull final Converstion converstion) {
                final String users = getRef(i).getKey();
                Query lastMessageQuery = MsgRef.child(users).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String data= dataSnapshot.child("message").getValue().toString();
                        convViewHolder.setMessage(data,converstion.isSeen());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                UserRef.child(users).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String name= dataSnapshot.child("name").getValue().toString();
                        final String image= dataSnapshot.child("Image").getValue().toString();
                        convViewHolder.username.setText(name);
                        if(!image.equals("default")){
                            Picasso.get().load(image).into(convViewHolder.UserImage);
                        }
                        convViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent Chat_intent = new Intent(getContext(), ChatActivity.class);
                                Chat_intent.putExtra("user_id",users);
                                Chat_intent.putExtra("username",name);
                                startActivity(Chat_intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
        ChatsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder {
        TextView username,userStatus ;
        CircleImageView UserImage;

        public ConvViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.user_single_name);
            userStatus=itemView.findViewById(R.id.user_single_status);
            UserImage=itemView.findViewById(R.id.user_single_image);
        }

        public void setMessage(String data, boolean seen) {
            userStatus.setText(data);
            if(seen){
                userStatus.setTypeface(userStatus.getTypeface(), Typeface.BOLD);
            }else {    userStatus.setTypeface(userStatus.getTypeface(), Typeface.NORMAL);}
        }
        public void setUserOnline (String onlineIcon){
            ImageView userOnlineView = (ImageView) itemView.findViewById(R.id.user_single_online_status);
            if(onlineIcon.equals("Online")){
                userOnlineView.setVisibility(View.VISIBLE);
            }else{
                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }

    }
}
