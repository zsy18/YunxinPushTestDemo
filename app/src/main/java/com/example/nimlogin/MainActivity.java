package com.example.nimlogin;

import static com.example.nimlogin.NotificationDataClickActivity.SESSION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pushlib.BuildConfig;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.mixpush.MixPushServiceObserve;
import com.netease.nimlib.sdk.mixpush.model.MixPushToken;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvLoginStatus,tvToken;
    private Button btnLogout;
    private static final String TEST_ACCID = "改为发送端的accid";
    String[] channelIds = new String[]{
            BuildConfig.oppoChannelId,
            BuildConfig.hwChannelId
    };
    private boolean hasUpdate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        setContentView(R.layout.activity_main);
        initView();
        registerImListener(true);
        checkPermission();
        //关闭消息提醒，防止干扰推送测试。
        NIMClient.toggleNotification(false);
        for (String channelId : channelIds) {
            if (!TextUtils.isEmpty(channelId)){
                buildMessageChannel(channelId);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        intent.getExtras();
        String sessionID = intent.getStringExtra(SESSION);
        if (!TextUtils.isEmpty(sessionID)){
            Toast.makeText(this, SESSION+":"+sessionID, Toast.LENGTH_SHORT).show();

        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void buildMessageChannel(String channelId) {
        NotificationManager manager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        NotificationChannel channel = new NotificationChannel(channelId, "会话提醒", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("NIM_CHANNEL_DESC");
        manager.createNotificationChannel(channel);
    }

    private void initView() {
        tvLoginStatus = findViewById(R.id.tv_login_status);
        btnLogout = findViewById(R.id.btn_logout);
        tvToken = findViewById(R.id.tv_token);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.saveUserAccount("");
                Preferences.saveUserToken("");
                MyApplication.hasLogined = false;
                NIMClient.getService(AuthService.class).logout();
            }
        });
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SendMessageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerImListener(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineStatusObserver,register);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(receiveMessageObserver,register);
        NIMClient.getService(MixPushServiceObserve.class).observeMixPushToken(mixPushTokenObserver,register);
    }

    private void checkPermission() {
        String permission = "android.permission.POST_NOTIFICATIONS";
        boolean hasPermission = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if (!hasPermission){
            String[] permissions = new String[]{permission};
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerImListener(false);

    }
    private Observer mixPushTokenObserver = new Observer<MixPushToken>() {
        @Override
        public void onEvent(MixPushToken mixPushToken) {

            tvToken.setText(mixPushToken.getTokenName()+"证书的token: "+mixPushToken.getToken());

        }
    };
    private Observer receiveMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> imMessages) {
            String toast = "收到消息，会话："+imMessages.get(0).getSessionId();
            Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * 用户在线状态观察者
     */
    private Observer<StatusCode> onlineStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode statusCode) {
            String userStatus = null;
            switch (statusCode) {
                case INVALID:
                    userStatus = "未定义";
                    break;
                case UNLOGIN:
                    userStatus = "未登录";
                    break;
                case LOGINED:
                    userStatus = "成功登录";
                    break;
                case NET_BROKEN:
                    userStatus = "网络连接已断开";
                    break;
                case CONNECTING:
                    userStatus = "正在连接服务器";
                    break;
                case LOGINING:
                    userStatus = "正在登录中";
                    break;
                case SYNCING:
                    userStatus = "正在同步数据";
                    break;
                case KICKOUT:
                    userStatus = "被其他端的登录踢掉";
                    break;
                case KICK_BY_OTHER_CLIENT:
                    userStatus = "被同时在线的其他端主动踢掉";
                    break;
                case FORBIDDEN:
                    userStatus = "被服务器禁止登录";
                    break;
                case VER_ERROR:
                    userStatus = "客户端版本错误";
                    break;
                case PWD_ERROR:
                    userStatus = "用户名或密码错误";
                    break;
//                case NEED_RECONNECT:
//                    userStatus = "需要重连";
//                    break;
//                case NEED_CHANGE_LBS:
//                    userStatus = "需要更新LBS";
//                    break;
                default:
                    break;
            }
            tvLoginStatus.setText(userStatus);
            Log.e("mytest","登录状态："+userStatus);
            //判断当前状态是否要进行手动登录。
            if (LoginActivity.shouldJumpToLoginActivity()){
                LoginActivity.startLoginActivity(MainActivity.this);
            }
        }
    };
}