package com.example.chashi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private EditText phoneET;
    private Button nextBtn;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneET = findViewById(R.id.phoneET);
        nextBtn =findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = phoneET.getText().toString().trim();

                Intent intent = new Intent(LoginActivity.this,VerifyActivity.class);
                intent.putExtra("phone",phone);
                startActivity(intent);

            }
        });
    }
}