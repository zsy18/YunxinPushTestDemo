package com.example.nimlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

public class LoginActivity extends AppCompatActivity {
    private static Boolean hasStart = false;
    private EditText mEtAccid, mEtToken;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        hasStart = true;
    }

    private void initView() {
        mEtAccid = findViewById(R.id.et_accid);
        mEtToken = findViewById(R.id.et_token);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accid = mEtAccid.getText().toString();
                String token = mEtToken.getText().toString();
                doIMLogin(accid, token);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hasStart = false;
    }

    public static void startLoginActivity(Context context){
        hasStart = true;
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }
    /**
     * 1、自动登录和手动登录接口不要同时调用，
     * 2、手动登录接口一般是客户输入账号密码的时候才需要代码层调用
     * 3、相信我们的重连逻辑，当MyApplication.hasLogined为ture的时候，除非statusCode.wontAutoLogin()返回为true的时候才不会重连。
     * 4、当出现不可以自动重连状态码的时候才需要调用手动登录，一般交给用户自己手动输入。
     */
    public static boolean shouldJumpToLoginActivity(){
        if (hasStart){
            //登录页面已经存在，不需要重复进入登录页面。
            return false;
        }
        if (!MyApplication.hasLogined){
            //如果尚未登录过，需要进入登录页面，可以根据业务需要选择返回值。
            return true;
        }
        //已经登录过的情况下，当前状态不会走自动登录，需要重新登陆。
        StatusCode statusCode = NIMClient.getStatus();
        return statusCode.wontAutoLogin();
    }

    /**
     * 1、自动登录和手动登录接口不要同时调用，
     * 2、手动登录接口一般是客户输入账号密码的时候才需要代码层调用
     * 3、相信我们的重连逻辑，当MyApplication.hasLogined为ture的时候，除非statusCode.wontAutoLogin()返回为true的时候才不会重连。
     * 4、当出现不可以自动重连状态码的时候才需要调用手动登录，一般交给用户自己手动输入。
     * @param accid
     * @param token
     */
    private void doIMLogin(String accid, String token) {
        if (TextUtils.isEmpty(accid)){
            Toast.makeText(this, R.string.tip_account_is_null, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(token)){
            Toast.makeText(this, R.string.tip_token_is_null, Toast.LENGTH_SHORT).show();
            return;
        }
        LoginInfo loginInfo = new LoginInfo(accid,token);
        NIMClient.getService(AuthService.class).login(loginInfo).setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo result) {
                //保存accid、token，用于下次自动登录。
                Preferences.saveUserAccount(result.getAccount());
                Preferences.saveUserToken(result.getToken());
                MyApplication.hasLogined = true;
                Toast.makeText(LoginActivity.this, R.string.tip_login_success, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailed(int code) {
                //清理登录账号、token缓存，调用login失败后，不允许走自动登录。
                Preferences.saveUserAccount("");
                Preferences.saveUserToken("");
                MyApplication.hasLogined = false;
                // TODO: 2022/11/15 登录出错，
                switch (code) {
                    case 302:
                        //返回302表示账号密码错误，即登录时传入的AppKey、accid、token三者不匹配
                        Toast.makeText(LoginActivity.this, R.string.tip_login_fail_token_error, Toast.LENGTH_SHORT).show();
                        break;
                    case 408:
                        Toast.makeText(LoginActivity.this, R.string.tip_time_out, Toast.LENGTH_SHORT).show();
                        break;
                    case 415:
                        Toast.makeText(LoginActivity.this, R.string.tip_net_error, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(LoginActivity.this, R.string.tip_login_fail, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onException(Throwable exception) {
                //清理登录账号、token缓存，调用login失败后，不允许走自动登录。
                Preferences.saveUserAccount("");
                Preferences.saveUserToken("");
                MyApplication.hasLogined = false;
                // TODO: 2022/11/15 登录过程发生异常，被sdk捕获。
                Toast.makeText(LoginActivity.this, R.string.tip_login_exception+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}