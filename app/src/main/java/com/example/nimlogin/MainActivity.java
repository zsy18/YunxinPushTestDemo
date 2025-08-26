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
import com.netease.nimlib.sdk.mixpush.MixPushServiceObserve;
import com.netease.nimlib.sdk.mixpush.model.MixPushToken;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.v2.V2NIMError;
import com.netease.nimlib.sdk.v2.V2NIMFailureCallback;
import com.netease.nimlib.sdk.v2.V2NIMSuccessCallback;
import com.netease.nimlib.sdk.v2.auth.V2NIMLoginListener;
import com.netease.nimlib.sdk.v2.auth.V2NIMLoginService;
import com.netease.nimlib.sdk.v2.auth.enums.V2NIMLoginClientChange;
import com.netease.nimlib.sdk.v2.auth.enums.V2NIMLoginStatus;
import com.netease.nimlib.sdk.v2.auth.model.V2NIMKickedOfflineDetail;
import com.netease.nimlib.sdk.v2.auth.model.V2NIMLoginClient;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView tvLoginStatus, tvToken;
    private Button btnLogout;
    String[] channelIds = new String[]{
            BuildConfig.oppoChannelId,
            BuildConfig.hwChannelId,
            BuildConfig.xmChannelId,
            BuildConfig.fcmChannelId
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                // 请勿在 Activity 的 `onDestroy` 中调用 `logout` 方法
                NIMClient.getService(V2NIMLoginService.class).logout(new V2NIMSuccessCallback<Void>(){
                    @Override
                    public void onSuccess(Void o) {

                        LoginActivity.startLoginActivityAndCleanAccount(MainActivity.this);

                    }
                }, new V2NIMFailureCallback(){
                    @Override
                    public void onFailure(V2NIMError error){
                        int code = error.getCode();
                        String desc = error.getDesc();
                        // TODO
                    }
                });

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
    private void registerImListener(boolean register){
        if (register){
            NIMClient.getService(V2NIMLoginService.class).addLoginListener(loginListener);


        }else {
            NIMClient.getService(V2NIMLoginService.class).removeLoginListener(loginListener);


        }
        NIMClient.getService(MixPushServiceObserve.class).observeMixPushToken(mixPushTokenObserver, register);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(receiveMessageObserver,register);
    }
    private V2NIMLoginListener loginListener = new V2NIMLoginListener() {
        @Override
        public void onLoginStatus(V2NIMLoginStatus status) {
            switch (status){
                case V2NIM_LOGIN_STATUS_LOGOUT:
                    tvLoginStatus.setText("登出");
                    break;
                case V2NIM_LOGIN_STATUS_UNLOGIN:
                    tvLoginStatus.setText("未登录");
                    break;
                case V2NIM_LOGIN_STATUS_LOGINING:
                    tvLoginStatus.setText("登录中");

                    break;
                case V2NIM_LOGIN_STATUS_LOGINED:
                    tvLoginStatus.setText("登录成功");
                    break;
            }
            Log.e(TAG,"loginListener onLoginStatus:"+status.toString());
        }
        @Override
        public void onLoginFailed(V2NIMError error) {
            tvLoginStatus.setText("登录失败");
            Log.e(TAG,"loginListener onLoginFailed:"+error.toString());
        }
        @Override
        public void onKickedOffline(V2NIMKickedOfflineDetail detail) {
            //自动登录失败，返回登录页面
            int code = detail.getReason().getValue();
            String desc = detail.getReasonDesc();
            Toast.makeText(MainActivity.this, R.string.tip_login_fail+",code:"+code+",desc:"+desc, Toast.LENGTH_SHORT).show();
            LoginActivity.startLoginActivityAndCleanAccount(MainActivity.this);
            Log.e(TAG,"loginListener onKickedOffline:"+detail.toString());
        }
        @Override
        public void onLoginClientChanged(V2NIMLoginClientChange change, List<V2NIMLoginClient> clients) {
        }
    };
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