package com.example.nimlogin;

import static com.example.nimlogin.NotificationDataClickActivity.SESSION;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;

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
        if (TextUtils.isEmpty(sessionID)) {
            //小米推送sdk内部特殊处理，把自定义字段放到了MiPushMessage对象里了。
            MiPushMessage pushMessage = (MiPushMessage) intent.getSerializableExtra(PushMessageHelper.KEY_MESSAGE);
            if (pushMessage != null && !pushMessage.getExtra().isEmpty()) {
                sessionID = pushMessage.getExtra().get(SESSION);
            }
        }
        if (TextUtils.isEmpty(sessionID)){
            Toast.makeText(this, "没有自定义字段", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, SESSION+":"+sessionID, Toast.LENGTH_SHORT).show();

        }
    }
}