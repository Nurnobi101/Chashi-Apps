package com.example.chashi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {
    private EditText verifyET;
    private Button verifyBtn;
    String phoneNumber;
    String code;
    String verificationId;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        verifyET = findViewById(R.id.verifyET);
        verifyBtn = findViewById(R.id.verifyBtn);
        firebaseAuth = FirebaseAuth.getInstance();

        phoneNumber = getIntent().getStringExtra("phone");

        sendOTP();

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (code.length()==6){
                    verify(code);

                }

            }
        });

    }

    private void sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+88"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                callBacks);

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            code = phoneAuthCredential.getSmsCode();
            if (code!=null){
                verifyET.setText(code);
                verify(code);

            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException exception) {

            Toast.makeText(VerifyActivity.this, ""+exception.getMessage(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId = s;
        }
    };

    private void verify(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithcredential (credential);

    }

    private void signInWithcredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(VerifyActivity.this,MainActivity.class));

                } else {
                    Toast.makeText(VerifyActivity.this, "Error Signin Again", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}