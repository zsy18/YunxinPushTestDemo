package com.example.nimlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.v2.V2NIMError;
import com.netease.nimlib.sdk.v2.V2NIMFailureCallback;
import com.netease.nimlib.sdk.v2.V2NIMSuccessCallback;
import com.netease.nimlib.sdk.v2.auth.V2NIMLoginListener;
import com.netease.nimlib.sdk.v2.auth.V2NIMLoginService;
import com.netease.nimlib.sdk.v2.auth.enums.V2NIMLoginClientChange;
import com.netease.nimlib.sdk.v2.auth.enums.V2NIMLoginStatus;
import com.netease.nimlib.sdk.v2.auth.model.V2NIMKickedOfflineDetail;
import com.netease.nimlib.sdk.v2.auth.model.V2NIMLoginClient;
import com.netease.nimlib.sdk.v2.auth.option.V2NIMLoginOption;

import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            loginImAuto(account,token);
        }else {
            LoginActivity.startLoginActivity(this);
            finish();
        }
    }
    private V2NIMLoginListener loginListener = new V2NIMLoginListener() {
        @Override
        public void onLoginStatus(V2NIMLoginStatus status) {
            Log.e(TAG,"loginListener onLoginStatus:"+status.toString());
        }
        @Override
        public void onLoginFailed(V2NIMError error) {
            Log.e(TAG,"loginListener onLoginFailed:"+error.toString());
        }
        @Override
        public void onKickedOffline(V2NIMKickedOfflineDetail detail) {
            Log.e(TAG,"loginListener onKickedOffline:"+detail.toString());
        }
        @Override
        public void onLoginClientChanged(V2NIMLoginClientChange change, List<V2NIMLoginClient> clients) {
        }
    };
    private void loginImAuto(String account, String token){
        NIMClient.getService(V2NIMLoginService.class).addLoginListener(loginListener);

        V2NIMLoginOption option = new V2NIMLoginOption();
        //本次登录采用离线登录，无网络下也可以登录成功，后面sdk内部会自动重连
        option.setOfflineMode(true);
        //本次登录不需要用户输入账号密码，属于自动登录，防止被踢出后还能继续登录，所以走非强制模式登录。
//        option.setForceMode(false);
        NIMClient.getService(V2NIMLoginService.class).login(account, token, option, new V2NIMSuccessCallback<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG,"login  onSuccess");

                        // 登录成功跳转到主页面
                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                        //把启动页收到的推送数据传递给主页面。
                        intent.putExtras(getIntent());
                        startActivity(intent);
                        finish();

                    }
                },
                    new V2NIMFailureCallback() {
                    @Override
                    public void onFailure(V2NIMError error) {
                        Preferences.saveUserAccount("");
                        Preferences.saveUserToken("");
                        //自动登录失败，返回登录页面
                        int code = error.getCode();
                        String desc = error.getDesc();
                        Toast.makeText(SplashActivity.this, R.string.tip_login_fail+",code:"+code+",desc:"+desc, Toast.LENGTH_SHORT).show();
                        LoginActivity.startLoginActivity(SplashActivity.this);
                        finish();
                    }
                });

    }
}









