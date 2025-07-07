package com.example.nimlogin;

import static com.example.nimlogin.NotificationDataClickActivity.SESSION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pushlib.BuildConfig;
import com.example.pushlib.pushpayload.NotifyClickAction;
import com.example.pushlib.pushpayload.NotifyEffectMode;
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
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvLoginStatus, tvToken;
    private Button btnLogout;
    String[] channelIds = new String[]{
            BuildConfig.oppoChannelId,
            BuildConfig.hwChannelId,
            BuildConfig.xmChannelId,
            BuildConfig.fcmChannelId
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
            if (!TextUtils.isEmpty(channelId)) {
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
        if (TextUtils.isEmpty(sessionID)) {
            //小米推送sdk内部特殊处理，把自定义字段放到了MiPushMessage对象里了。
            MiPushMessage pushMessage = (MiPushMessage) intent.getSerializableExtra(PushMessageHelper.KEY_MESSAGE);
            if (pushMessage != null && !pushMessage.getExtra().isEmpty()) {
                sessionID = pushMessage.getExtra().get(SESSION);
            }
        }
        if (!TextUtils.isEmpty(sessionID)) {
            Toast.makeText(this, SESSION + ":" + sessionID, Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void buildMessageChannel(String channelId) {
        NotificationManager manager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        NotificationChannel channel = new NotificationChannel(channelId, "会话提醒" + channelId, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("NIM_CHANNEL_DESC" + channelId);
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
                Intent intent = new Intent(MainActivity.this, SendMessageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showNotification() {


// 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("My Notification")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("This is a notification message")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

// 点击通知后跳转到特定的 Activity
        NotifyClickAction clickAction = new NotifyClickAction.Builder()
                .setIntentAction("android.intent.action.VIEW")
                .addIntentCategory("android.intent.category.DEFAULT")
                .setNotifyEffect(NotifyEffectMode.EFFECT_MODE_CONTENT)
                .setIntentDataScheme("im")
                .setIntentDataHost(com.example.nimlogin.BuildConfig.APPLICATION_ID)
                .setIntentDataPath("/p2pPage?")
                .build();
        Intent intent = null;

        try {
            String click = clickAction.getIntentFilterString();
            Log.e("ActivityTaskManager", click);
            intent = Intent.parseUri(click, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        builder.setChannelId("1");

        // 显示通知
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void registerImListener(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineStatusObserver, register);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(receiveMessageObserver, register);
        NIMClient.getService(MixPushServiceObserve.class).observeMixPushToken(mixPushTokenObserver, register);
    }

    private void checkPermission() {
        String permission = "android.permission.POST_NOTIFICATIONS";
        boolean hasPermission = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if (!hasPermission) {
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

            tvToken.setText(mixPushToken.getTokenName() + "证书的token: " + mixPushToken.getToken());

        }
    };
    private Observer receiveMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> imMessages) {
            String toast = "收到消息，会话：" + imMessages.get(0).getSessionId();
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
            Log.e("mytest", "登录状态：" + userStatus);
            //判断当前状态是否要进行手动登录。
            if (LoginActivity.shouldJumpToLoginActivity()) {
                LoginActivity.startLoginActivity(MainActivity.this);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        boolean isHonor = isHonorNewDevice();
        Log.e("mytest", "isHonor:" + isHonor);

    }

    private boolean isHonorOldDevice() {
        String isEmotionOs = getBuildVersion("ro.build.version.emui");
        if (isHonorDevice() && !TextUtils.isEmpty(isEmotionOs)) {
            return true;
        }
        return false;
    }

    public static String getBuildVersion(String key) {
        String buildVersion = "";
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getDeclaredMethod("get", String.class);
            buildVersion = (String) method.invoke(clazz, key);
        } catch (ClassNotFoundException e) {
            Log.e("HONOR", "getBuildVersion ClassNotFoundException" + e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e("HONOR", "getBuildVersion NoSuchMethodException" +
                    e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("HONOR", "getBuildVersion IllegalAccessException" + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("HONOR", "getBuildVersion InvocationTargetException" +
                    e.getMessage());
        } catch (Exception e) {
            Log.e("HONOR", "getBuildVersion Exception" + e.getMessage());
        }
        Log.i("HONOR", "getBuildVersion: " + buildVersion);
        return buildVersion;
    }

    private boolean isHonorDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("HONOR");
    }

    private boolean isHonorNewDevice() {
        if (isHonorDevice() && !isHonorOldDevice()) {
            return true; // 新荣耀产品(无 HMS 预装) }
        } else {
            return false; //荣耀产品(有 HMS 预装) }}
        }
    }
}