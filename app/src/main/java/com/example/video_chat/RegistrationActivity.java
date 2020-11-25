package com.example.video_chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {
    private CountryCodePicker ccp;
    private EditText phoneText;
    private EditText codeText;
    private Button continueAndNextBtn;
    private String checker ="",phoneNumber="";
    private RelativeLayout relativeLayout;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    private ProgressDialog loadingBar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        phoneText=findViewById(R.id.phoneText);


        mAuth=FirebaseAuth.getInstance();
        loadingBar=new ProgressDialog(this);


        codeText=findViewById(R.id.codeText);
        continueAndNextBtn=findViewById(R.id.continueNextButton);
        relativeLayout=findViewById(R.id.phoneAuth);
        ccp=(CountryCodePicker)findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);
        continueAndNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(continueAndNextBtn.getText().equals("Submit")|| checker.equals("code sent"))
                {
                    String verificationcode=codeText.getText().toString();
                    if(verificationcode.equals("")){
                        Toast.makeText(RegistrationActivity.this, "please write verification code", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        loadingBar.setTitle("Code Verification");
                        loadingBar.setMessage("please wait , while we verifying your CODE");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,verificationcode);
                        signInWithPhoneAuthCredential(credential);
                    }

                }
                else
                {
                    phoneNumber=ccp.getFullNumberWithPlus();
                    if (!phoneNumber.equals(""))
                    {
                        loadingBar.setTitle("phone Number Verification");
                        loadingBar.setMessage("please wait , while we verifying your phone number");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60,TimeUnit.SECONDS,RegistrationActivity.this ,mCallbacks );
                    }
                    else
                    {
                        Toast.makeText(RegistrationActivity.this, "Please Write valid phone number", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(RegistrationActivity.this, "Invalid phone Number", Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.VISIBLE);
                continueAndNextBtn.setText("Continue");
                codeText.setVisibility(View.GONE);

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId=s;
                mResendingToken=forceResendingToken;
                loadingBar.dismiss();
                relativeLayout.setVisibility(View.GONE);
                checker="code sent";
                continueAndNextBtn.setText("Submit");
                codeText.setVisibility(View.VISIBLE);
                Toast.makeText(RegistrationActivity.this, "Code has been sent ", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Intent homeintent=new Intent(RegistrationActivity.this, ContactActivity.class);
            startActivity(homeintent);
            finish();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(RegistrationActivity.this, "login successfully", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        }
                        else {
                            loadingBar.dismiss();
                            String e= task.getException().toString();
                            Toast.makeText(RegistrationActivity.this, "ERROR:"+ e, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    private void sendUserToMainActivity(){
        Intent intent=new Intent(RegistrationActivity.this , ContactActivity.class);
        startActivity(intent);
        finish();

    }
}
