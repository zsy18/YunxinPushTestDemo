package com.example.nimlogin;

import static com.example.nimlogin.NotificationDataClickActivity.SESSION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.pushlib.pushpayload.NotifyClickAction;
import com.example.pushlib.pushpayload.NotifyEffectMode;
import com.example.pushlib.pushpayload.PushPayloadBuilder;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SendMessageActivity extends AppCompatActivity {
    private EditText etToAccid;
    private SwitchCompat swClickNotify, swCustomData;
    private Button btnSendMessage, btnSendCustomNotify;
    private RadioGroup radioGroup;
    private int selectIndex = 0;
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
        radioGroup = findViewById(R.id.rg_click_notification);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_main:
                        selectIndex = 0;
                        break;
                    case R.id.rb_action:
                        selectIndex = 1;
                        break;
                    case R.id.rb_data:
                        selectIndex = 2;
                        break;
                }
            }
        });
        btnSendCustomNotify = findViewById(R.id.btn_send_custom_notify);
        btnSendCustomNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCustomNotify();
            }
        });

    }

    private void sendMessage() {
        String toAccid = etToAccid.getText().toString();
        String content = "测试消息：" + sendCount++;
        IMMessage message = MessageBuilder.createTextMessage(toAccid, SessionTypeEnum.P2P, content);
        message.setPushPayload(getPushPayload(swClickNotify.isChecked(), swCustomData.isChecked()));
        //如需自定义推送内容，则需要直接通过IMMessage#setPushContent设置，不配置默认使用消息内容。
//        message.setPushContent("测试测试测试");
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                if (code == 200) {
                    Toast.makeText(SendMessageActivity.this, "消息发送成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SendMessageActivity.this, "消息发送失败", Toast.LENGTH_SHORT).show();
                    NIMClient.getService(FriendService.class).addFriend(new AddFriendData(toAccid, VerifyType.DIRECT_ADD));
                }
            }
        });


    }

    private void sendCustomNotify() {
        String toAccid = etToAccid.getText().toString();
        // 构造自定义通知，指定接收者
        CustomNotification notification = new CustomNotification();
        notification.setSendToOnlineUserOnly(false);
        notification.setSessionId(toAccid);
        notification.setSessionType(SessionTypeEnum.P2P);
        CustomNotificationConfig config = new CustomNotificationConfig();

        // 需要推送
        config.enablePush = true;
        //通知标题为用户昵称，优先级低于pushpayload中title设置
        config.enablePushNick = true;
        notification.setConfig(config);
        //自定义系统通知必须配置改字段，作为推送的内容，否则推送不触发。
        notification.setApnsText("自定义通知测试");
        notification.setPushPayload(getPushPayload(swClickNotify.isChecked(), swCustomData.isChecked()));

        // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
        JSONObject json = new JSONObject();
        try {
            json.put("id", "2");
            JSONObject data = new JSONObject();
            data.put("body", "the_content_for_display");
            data.put("url", "url_of_the_game_or_anything_else");
            json.put("data", data);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        notification.setContent(json.toString());
        NIMClient.getService(MsgService.class).sendCustomNotification(notification).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                if (code == 200) {
                    Toast.makeText(SendMessageActivity.this, "消息发送成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SendMessageActivity.this, "消息发送失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Map<String, Object> getPushPayload(boolean clickActionEnable, boolean customDataEnable) {
        if (!clickActionEnable && !customDataEnable) {
            return null;
        }

        PushPayloadBuilder builder = new PushPayloadBuilder();
        NotifyClickAction clickAction;

        if (clickActionEnable) {
            switch (selectIndex) {
                case 0:
                    clickAction = new NotifyClickAction.Builder()
                            .setNotifyEffect(NotifyEffectMode.EFFECT_MODE_APP)
                            .build();
                    builder.setClickAction(clickAction);

                    break;
                case 1:
                    clickAction = new NotifyClickAction.Builder()
                            .setNotifyEffect(NotifyEffectMode.EFFECT_MODE_CONTENT)
                            .setIntentAction(BuildConfig.APPLICATION_ID + ".openP2PView")
                            .build();
                    builder.setClickAction(clickAction);

                    break;
                case 2:
                    clickAction = new NotifyClickAction.Builder()
                            .setNotifyEffect(NotifyEffectMode.EFFECT_MODE_CONTENT)
                            .setIntentDataScheme("im")
                            .setIntentDataHost(BuildConfig.APPLICATION_ID)
                            .setIntentDataPath("/p2pPage")
                            .setIntentDataPort("8080")
                            .build();
                    builder.setClickAction(clickAction);
                    break;
            }
        }
        if (customDataEnable) {
            builder.addCustomData(SESSION, etToAccid.getText().toString());
        }
        return builder.generatePayload();
    }
}