package com.example.video_chat;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class videoChatActivity extends AppCompatActivity implements Session.SessionListener,Publisher.PublisherListener {
    private static String API_Key="46781614";
    private static String SESSION_ID= "1_MX40Njc4MTYxNH5-MTU5NjY2MzAwOTkwM35uUVZIeG5xUWZPbnFBa0ZWWkRPTStJLzZ-fg";
            //"1_MX40Njc4MTYxNH5-MTU5MTUwNzYwMTkyNH5BTFF1RGN1S2hwV1BuSGJQK01iQU05NTZ-fg";
    private static String TOKEN="T1==cGFydG5lcl9pZD00Njc4MTYxNCZzaWc9NzI4Y2M1YjMwOTAwYTFiMDcyMGJiOGIyZWE0MWIyZTFmNTVkZmMzZjpzZXNzaW9uX2lkPTFfTVg0ME5qYzRNVFl4Tkg1LU1UVTVOalkyTXpBd09Ua3dNMzV1VVZaSWVHNXhVV1pQYm5GQmEwWldXa1JQVFN0Skx6Wi1mZyZjcmVhdGVfdGltZT0xNTk2NjYzMDgyJm5vbmNlPTAuNTYxNzMzMjQ5MTM0Njc5MSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTk5MjU1MDk0JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
                    //"T1==cGFydG5lcl9pZD00Njc4MTYxNCZzaWc9ZjU1NDA2ZjAxYWE5MjFhNjhmMTg2YmE1Y2E2ZWQzZDQ3NWI3YjNhMTpzZXNzaW9uX2lkPTFfTVg0ME5qYzRNVFl4Tkg1LU1UVTVNVFV3TnpZd01Ua3lOSDVCVEZGMVJHTjFTMmh3VjFCdVNHSlFLMDFpUVUwNU5UWi1mZyZjcmVhdGVfdGltZT0xNTkxNTA3NjU1Jm5vbmNlPTAuOTgxNTkyNTc1NDE0MzQ2OSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTk0MDkyNDY3JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_TAG=videoChatActivity.class.getSimpleName();
    private static final  int RC_VIDEO_APP_PERM=123;

    private FrameLayout mPublisherVideoChatController;
    private FrameLayout mSubscriberVideoChatController;
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;


    private ImageView closeVideoChatBtn;
    private DatabaseReference userRef;
    private String useID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        useID=FirebaseAuth.getInstance().getCurrentUser().getUid();

        closeVideoChatBtn=findViewById(R.id.close_video_chat_btn);

         requestPermissions();

        closeVideoChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(useID).hasChild("Ringing")){
                            userRef.child(useID).child("Ringing").removeValue();

                            if(mPublisher!=null){
                                mPublisher.destroy();
                            }

                            if(mSubscriber!=null){
                                mSubscriber.destroy();
                            }

                            startActivity(new Intent(videoChatActivity.this,RegistrationActivity.class));
                            finish();
                        }
                        if (dataSnapshot.child(useID).hasChild("Calling")){
                            userRef.child(useID).child("Calling").removeValue();

                            if(mPublisher!=null){
                                mPublisher.destroy();
                            }

                            if(mSubscriber!=null){
                                mSubscriber.destroy();
                            }
                            startActivity(new Intent(videoChatActivity.this,RegistrationActivity.class));
                            finish();
                        }
                        else{
                            if(mPublisher!=null){
                                mPublisher.destroy();
                            }

                            if(mSubscriber!=null){
                                mSubscriber.destroy();
                            }

                            startActivity(new Intent(videoChatActivity.this,RegistrationActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,videoChatActivity.this);
    }
    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions(){
        String[] perms={Manifest.permission.INTERNET,Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA};
        if(EasyPermissions.hasPermissions(this,perms))
        {
            mPublisherVideoChatController=findViewById(R.id.publisher_container);
            mSubscriberVideoChatController=findViewById(R.id.subscriber_container);

            // initialize and connect to the session
            mSession=new com.opentok.android.Session.Builder(this,API_Key,SESSION_ID).build();
            mSession.setSessionListener(videoChatActivity.this);
            mSession.connect(TOKEN);
        }
        else {
            EasyPermissions.requestPermissions(this,"this app needs Mic and camera,Please allow",RC_VIDEO_APP_PERM,perms);
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }


    // Publishing a stream to the  session
    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG,"Session Connected");
        mPublisher=new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(videoChatActivity.this);

        mPublisherVideoChatController.addView(mPublisher.getView());

        if(mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }
        mSession.publish(mPublisher);


    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG,"Stream Disconnected");
        if(mSubscriber!=null){
            mSubscriber=null;
            mSubscriberVideoChatController.removeAllViews();
        }
    }

    //Subscribing a stream to the  session
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG,"Stream Received");
        if(mSubscriber==null){
            mSubscriber=new Subscriber.Builder(this,stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberVideoChatController.addView(mSubscriber.getView());
        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG,"Stream Dropped");

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.i(LOG_TAG,"Stream Error");

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
