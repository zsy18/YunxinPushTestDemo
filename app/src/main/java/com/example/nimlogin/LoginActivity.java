package com.example.nimlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.v2.V2NIMError;
import com.netease.nimlib.sdk.v2.V2NIMFailureCallback;
import com.netease.nimlib.sdk.v2.V2NIMSuccessCallback;
import com.netease.nimlib.sdk.v2.auth.V2NIMLoginService;
import com.netease.nimlib.sdk.v2.auth.option.V2NIMLoginOption;

public class LoginActivity extends AppCompatActivity {
    private static Boolean hasStart = false;
    private EditText mEtAccid, mEtToken;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
        }
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

    public static void startLoginActivityAndCleanAccount(Context context){
        Preferences.saveUserAccount("");
        Preferences.saveUserToken("");
        hasStart = true;
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
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
        V2NIMLoginOption option = new V2NIMLoginOption();
        //启动离线模式。
        option.setOfflineMode(false);
        //本次登录用户手动输入账号密码，所以走强制模式登录。
        option.setForceMode(true);
        NIMClient.getService(V2NIMLoginService.class).login(accid, token, option, new V2NIMSuccessCallback<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //保存accid、token，用于下次自动登录。
                        Preferences.saveUserAccount(accid);
                        Preferences.saveUserToken(token);
                        Toast.makeText(LoginActivity.this, R.string.tip_login_success, Toast.LENGTH_SHORT).show();
                        // 登录成功跳转到主页面
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        //把启动页收到的推送数据传递给主页面。
                        intent.putExtras(getIntent());
                        startActivity(intent);
                        finish();
                    }
                },
                new V2NIMFailureCallback() {
                    @Override
                    public void onFailure(V2NIMError error) {
                        //自动登录失败，返回登录页面
                        int code = error.getCode();
                        String desc = error.getDesc();
                        Toast.makeText(LoginActivity.this, R.string.tip_login_fail+",code:"+code+",desc:"+desc, Toast.LENGTH_SHORT).show();
                    }
                });

    }

}