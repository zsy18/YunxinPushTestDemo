package com.example.nimlogin;

import static com.example.nimlogin.NotificationDataClickActivity.SESSION;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

public class NotificationActionClickActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_action_click);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        intent.getExtras();
        String sessionID = intent.getStringExtra(SESSION);
        if (TextUtils.isEmpty(sessionID)){
            Toast.makeText(this, "没有自定义字段", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, SESSION+":"+sessionID, Toast.LENGTH_SHORT).show();
        }
    }
}