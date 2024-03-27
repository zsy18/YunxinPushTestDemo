package com.example.nimlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

public class NotificationClickActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifycation_click);
        Intent intent = getIntent();
        intent.getExtras();
        String sessionID = intent.getStringExtra("sessionID");
        if (TextUtils.isEmpty(sessionID)){
            Toast.makeText(this, "没有自定义字段", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, sessionID, Toast.LENGTH_SHORT).show();

        }
    }
}