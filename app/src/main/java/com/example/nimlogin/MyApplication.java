package com.example.nimlogin;

import android.app.Application;
import android.text.TextUtils;

import com.example.pushlib.MixPushConfigGenerator;
import com.hihonor.push.sdk.HonorPushClient;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

public class MyApplication extends Application {
    /**
     * 上次调用登录登录接口是否成功。
     */
    public static boolean hasLogined = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Preferences.setContext(this);
        LoginInfo loginInfo = null;
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            //之前已经登录过，可以走自动登录。
            hasLogined = true;
            loginInfo = new LoginInfo(account, token);
        }
        SDKOptions sdkOptions = new SDKOptions();
        sdkOptions.statusBarNotificationConfig = new StatusBarNotificationConfig();
        sdkOptions.mixPushConfig = MixPushConfigGenerator.loadPushConfig();
        sdkOptions.sdkStorageRootPath = getExternalCacheDir().getPath() + "/nim";
        sdkOptions.disableAwake = true;
        NIMClient.init(this, loginInfo, sdkOptions);
        if (NIMUtil.isMainProcess(this)) {

            // 在此处添加以下代码
            com.huawei.hms.support.common.ActivityMgr.INST.init(this);
            HonorPushClient.getInstance().init(getApplicationContext(), true);

        }
    }

}
