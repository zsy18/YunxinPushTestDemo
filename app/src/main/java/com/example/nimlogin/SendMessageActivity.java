package com.example.nimlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class SendMessageActivity extends AppCompatActivity {
    private EditText etToAccid;
    private SwitchCompat swClickNotify,swCustomData;
    private Button btnSendMessage;
    private int sendCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        etToAccid = findViewById(R.id.et_to_accid);
        swClickNotify = findViewById(R.id.switch_click_notification);
        swCustomData = findViewById(R.id.switch_custom_data);
        btnSendMessage = findViewById(R.id.btn_send_message);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }
    private void sendMessage(){
        String toAccid = etToAccid.getText().toString();
        String content = "测试消息："+sendCount++;
        IMMessage message = MessageBuilder.createTextMessage(toAccid, SessionTypeEnum.P2P,content);
        String pushPayload = loadPushPayload();
        if (!TextUtils.isEmpty(pushPayload)){
//            message.setPushPayload(pushPayload);

        }
        NIMClient.getService(MsgService.class).sendMessage(message,false).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                if (code == 200){
                    Toast.makeText(SendMessageActivity.this, "消息发送成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SendMessageActivity.this, "消息发送失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String loadPushPayload() {

        return null;
    }
}