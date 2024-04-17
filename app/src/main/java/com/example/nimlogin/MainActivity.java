package com.example.nimlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvLoginStatus;
    private Button btnLogout;
    private static final String TEST_ACCID = "改为发送端的accid";
    private boolean hasUpdate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        setContentView(R.layout.activity_main);
        initView();
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineStatusObserver,true);
        boolean hasPermission = checkPermission("android.permission.POST_NOTIFICATIONS");
        if (!hasPermission){
            requestPermission();
        }

    }
    // 请求权限
    private void requestPermission() {
        String[] permissions = new String[]{"android.permission.POST_NOTIFICATIONS"};
        ActivityCompat.requestPermissions(this, permissions, 100);
    }
    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }
    private void initView() {
        tvLoginStatus = findViewById(R.id.tv_login_status);
        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.saveUserAccount("");
                Preferences.saveUserToken("");
                MyApplication.hasLogined = false;
                NIMClient.getService(AuthService.class).logout();
            }
        });
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(new Observer<List<IMMessage>>() {
            @Override
            public void onEvent(List<IMMessage> imMessages) {
                if (TEST_ACCID.equals(imMessages.get(0).getSessionId())){
                    RecentContact recentContact = NIMClient.getService(MsgService.class)
                            .queryRecentContact(TEST_ACCID, SessionTypeEnum.P2P);
                    String toast = "收到消息，会话："+recentContact.getContactId()+"的tag值为："+recentContact.getTag();
                    Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
                    if (!hasUpdate&&recentContact.getTag()!=100){
                        //保证永远只更新一次tag值
                        hasUpdate = true;
                        recentContact.setTag(100);
                        NIMClient.getService(MsgService.class).updateRecent(recentContact);
                    }
                }
            }
        },true);
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentContact recentContact = NIMClient.getService(MsgService.class)
                        .queryRecentContact(TEST_ACCID, SessionTypeEnum.P2P);
                String toast = "会话"+recentContact.getContactId()+"的tag值为："+recentContact.getTag();
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineStatusObserver,false);

    }

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
            // TODO: 2022/11/21
            tvLoginStatus.setText(userStatus);
            Log.e("mytest","登录状态："+userStatus);
            //判断当前状态是否要进行手动登录。
            if (LoginActivity.shouldJumpToLoginActivity()){
                LoginActivity.startLoginActivity(MainActivity.this);
            }
        }
    };


}