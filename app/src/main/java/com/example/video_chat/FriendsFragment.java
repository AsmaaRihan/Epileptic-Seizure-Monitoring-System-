//FirendFragment
package com.example.video_chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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
public class FriendsFragment extends Fragment {
    private View FriendsFragView;
    private RecyclerView FriendList;

    private DatabaseReference UserRef,FriendRef;
    private FirebaseAuth auth;
    String Current_User;
    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FriendsFragView = inflater.inflate(R.layout.fragment_friends, container, false);
        FriendList = FriendsFragView.findViewById(R.id.FriendList);
        FriendList.setHasFixedSize(true);
        FriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        Current_User= auth.getCurrentUser().getUid();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        UserRef.keepSynced(true);
        FriendRef.keepSynced(true);
        return FriendsFragView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Query query= FriendRef.child(Current_User);
        FirebaseRecyclerOptions<Contacts> options= new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(query, Contacts.class)
                .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Contacts, FriendViewHolder>(options) {
            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout,parent,false);
                FriendViewHolder holder = new FriendViewHolder(view);
                return holder;
            }
            @Override
            protected void onBindViewHolder(@NonNull final FriendViewHolder friendViewHolder, int position, @NonNull Contacts get_setDataUser) {
                final String users = getRef(position).getKey();
                UserRef.child(users).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        final String image = dataSnapshot.child("Image").getValue().toString();
                        if(!image.equals("default")){
                            Picasso.get().load(image).into(friendViewHolder.UserImage);
                        }
                        friendViewHolder.username.setText(name);
                        if(dataSnapshot.child("UserState").hasChild("state")){
                            String state = dataSnapshot.child("UserState").child("state").getValue().toString();
                            String date = dataSnapshot.child("UserState").child("date").getValue().toString();
                            String time = dataSnapshot.child("UserState").child("time").getValue().toString();
                            friendViewHolder.setUserOnline(state);
                            if(state.equals("Online")){
                                friendViewHolder.userStatus.setText("Online");
                        }else if(state.equals("Offline")){
                                friendViewHolder.userStatus.setText("LastSeen:"+date+" "+time);
                            }

                        }else{
                            friendViewHolder.userStatus.setText("Offline");
                        }

                        friendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]= new CharSequence[]
                                        {"Open Profile", "Send Message" , "Video Call"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if(i == 0){
                                            Intent profile_intent = new Intent(getContext(), ProfileActivityChat.class);
                                            profile_intent.putExtra("user_id",users);
                                            startActivity(profile_intent);

                                        }
                                        if(i == 1){
                                            Intent Chat_intent = new Intent(getContext(), ChatActivity.class);
                                            Chat_intent.putExtra("user_id",users);
                                            Chat_intent.putExtra("username",name);
                                            startActivity(Chat_intent);

                                        }
                                        if(i == 2){
                                            Intent Call_intent = new Intent(getContext(), ContactActivity.class);
                                            startActivity(Call_intent);
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
        FriendList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder{
        TextView username,userStatus ;
        CircleImageView UserImage;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.user_single_name);
            userStatus=itemView.findViewById(R.id.user_single_status);
            UserImage=itemView.findViewById(R.id.user_single_image);
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

