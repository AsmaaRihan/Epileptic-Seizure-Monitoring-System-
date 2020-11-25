package com.example.video_chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String CurrentUserID, mChatUser,mChatUserName;
    private DatabaseReference UserRef,RootRef,MessageRef;
    private FirebaseAuth mauth;
    private TextView LastSeen , Displayname;
    private TextInputEditText edChatMessage;
    private ImageButton btn_addChat, btn_SendChat;
    //RecyclerView
    RecyclerView MessageRecList;
    ArrayList<Messages> list;
    Myadapter adapter;
    LinearLayoutManager linearLayoutManager;
    //To Scroll in the msgs
    private static final int Total_Limits_To_Load=10;
    private int CurrentPage=1;
    private SwipeRefreshLayout RefreshLayout;
    private int itemPosition= 0;
    private String LastKey="";
    private String PrevKey="";
    //Sending_Image
    private static final int Gallery_Pick=1;
    private String myUrl="";
    private Uri fileUri;
    private StorageTask uploadTask;
    private CircleImageView ChatImage;

    public ChatActivity() {
    }
    // private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar mtoolbar = (Toolbar) findViewById(R.id.chatToolbar);
        setSupportActionBar(mtoolbar);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        mChatUser = getIntent().getStringExtra("user_id");
        mChatUserName = getIntent().getStringExtra("username");

        LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarView= inflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(actionbarView);
        //________Custom ActionBar ________//
        Displayname = (TextView) findViewById(R.id.DisplayNameChat);
        LastSeen= (TextView) findViewById(R.id.lastSeenChat);
        ChatImage = (CircleImageView)findViewById(R.id.ChaAcitvitytImage);
        Displayname.setText(mChatUserName);
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mChatUser);
        RootRef= FirebaseDatabase.getInstance().getReference();
        mauth= FirebaseAuth.getInstance();
        CurrentUserID= mauth.getCurrentUser().getUid();

        btn_addChat =findViewById(R.id.btn_addChat);
        btn_SendChat=findViewById(R.id.btn_sendChat);
        edChatMessage = (TextInputEditText) findViewById(R.id.edChatMessage);

        MessageRecList = (RecyclerView) findViewById(R.id.MessagesRecList);
        RefreshLayout=(SwipeRefreshLayout)findViewById(R.id.SwipMessage_SwipeLayout);
        linearLayoutManager = new LinearLayoutManager(this);
        MessageRecList.setLayoutManager(linearLayoutManager);

        list= new ArrayList<Messages>();
        MessageRef= FirebaseDatabase.getInstance().getReference().child("Messages").child(CurrentUserID).child(mChatUser);
        //_________GetMessage_That_was_Sent ___________//
        loadMessages();

        //___________Intialize_Message_Holder_____________//
        RootRef.child("Chat").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUser)){
                    Map ChatAddMap = new HashMap<>();
                    ChatAddMap.put("seen",false);
                    ChatAddMap.put("Timestamp",getTime());


                    Map ChatUserMap = new HashMap<>();
                    ChatUserMap.put("Chat/"+CurrentUserID+"/"+mChatUser, ChatAddMap );
                    ChatUserMap.put("Chat/"+mChatUser+"/"+CurrentUserID, ChatAddMap );
                    RootRef.updateChildren(ChatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d("Chat_LOG",databaseError.getMessage());

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //___________LastSeen ___________//
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("Image").getValue().toString();
                if(!image.equals("default")){
                    Picasso.get().load(image).into(ChatImage);
                }
                if(dataSnapshot.child("UserState").hasChild("state")){

                    String state = dataSnapshot.child("UserState").child("state").getValue().toString();
                    String date = dataSnapshot.child("UserState").child("date").getValue().toString();
                    String time = dataSnapshot.child("UserState").child("time").getValue().toString();

                    if(state.equals("Online")){
                        LastSeen.setText("Online");
                    }else if(state.equals("Offline")){
                        LastSeen.setText("LastSeen:"+date+" "+time);
                    }
                }else{
                    LastSeen.setText("Offline");
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btn_SendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
        RefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CurrentPage++;
                itemPosition=0;
                loadMoreMessages();


            }
        });

        btn_addChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent= new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),Gallery_Pick);


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallery_Pick && resultCode== RESULT_OK && data != null && data.getData() != null){

            fileUri =data.getData();
            StorageReference StorageRef = FirebaseStorage.getInstance().getReference().child("Images_File");

            final String current_user_ref = "Messages/" + CurrentUserID+"/"+ mChatUser;
            final String chat_user_ref = "Messages/" + mChatUser+"/"+ CurrentUserID;
            DatabaseReference user_message_push= RootRef.child("Message").child(current_user_ref).child(chat_user_ref).push();
            final String Push_ID=user_message_push.getKey();

            final StorageReference filePath= StorageRef.child(Push_ID + ".jpg");
            uploadTask = filePath.putFile(fileUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl= task.getResult();
                        myUrl = downloadUrl.toString();
                        Map messageMap=new HashMap();
                        messageMap.put("message",myUrl);
                        messageMap.put("name",fileUri.getLastPathSegment());
                        messageMap.put("seen",false);
                        messageMap.put("type","image");
                        messageMap.put("time",getTime());
                        messageMap.put("from", CurrentUserID);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/"+ Push_ID ,messageMap);
                        messageUserMap.put(chat_user_ref+ "/" + Push_ID,messageMap);

                        edChatMessage.setText("");
                        RootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                if(databaseError != null){
                                    Log.d("CHAT_LOG",databaseError.getMessage());
                                }
                            }
                        });



                    }
                }
            });


        }
    }
    private void loadMoreMessages() {
        Query MessageQuery= MessageRef.orderByKey().endAt(LastKey).limitToLast(10);
        MessageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                String MsgKey= dataSnapshot.getKey();

                if(!PrevKey.equals(MsgKey)){
                    list.add(itemPosition++,messages);
                } else {
                    PrevKey=LastKey;
                }

                if(itemPosition == 1){
                    LastKey=MsgKey;
                }

                adapter = new Myadapter(ChatActivity.this,list);
                MessageRecList.setAdapter(adapter);

                RefreshLayout.setRefreshing(false);
                linearLayoutManager.scrollToPositionWithOffset(10,0);


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

    }
    private void loadMessages() {

        Query MessageQuery= MessageRef.limitToLast(CurrentPage * Total_Limits_To_Load);
        MessageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                itemPosition++;
                if(itemPosition == 1){
                    String MsgKey= dataSnapshot.getKey();
                    LastKey=MsgKey;
                    PrevKey=MsgKey;
                }

                list.add(messages);
                adapter = new Myadapter(ChatActivity.this,list);
                MessageRecList.setAdapter(adapter);

                //to get the latest msg
                MessageRecList.scrollToPosition(list.size() - 1);
                RefreshLayout.setRefreshing(false);

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

    }
    private void SendMessage() {
        String message=edChatMessage.getText().toString();
        if(!TextUtils.isEmpty(message)) {

            String current_user_ref = "Messages/" + CurrentUserID+"/"+ mChatUser;
            String chat_user_ref = "Messages/" + mChatUser+"/"+ CurrentUserID;

            //Ba3mel ll msg an ID 3shan a5zen feeh al data bta3et al msg
            DatabaseReference user_message_push= RootRef.child("Message").child(current_user_ref).child(chat_user_ref).push();
            String Push_ID=user_message_push.getKey();


            Map messageMap=new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",getTime());
            messageMap.put("from",CurrentUserID);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/"+ Push_ID ,messageMap);
            messageUserMap.put(chat_user_ref+ "/" + Push_ID,messageMap);

            edChatMessage.setText("");
            RootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    if(databaseError != null){
                        Log.d("CHAT_LOG",databaseError.getMessage());
                    }
                }
            });



        }
    }
    //___________Get_Time____________////
    private String getTime(){
        SimpleDateFormat currentTime= new SimpleDateFormat("hh:mm a ");
        Calendar calendar= Calendar.getInstance();
        String CurrentTime= currentTime.format(calendar.getTime());

        return CurrentTime;

    }
}
