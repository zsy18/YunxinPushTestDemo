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

    @Override
    public void onCreate() {
        super.onCreate();
        Preferences.setContext(this);

        SDKOptions sdkOptions = new SDKOptions();
        sdkOptions.statusBarNotificationConfig = new StatusBarNotificationConfig();
        sdkOptions.mixPushConfig = MixPushConfigGenerator.loadPushConfig();
        sdkOptions.sdkStorageRootPath = getExternalCacheDir().getPath() + "/nim";
        sdkOptions.disableAwake = true;
        NIMClient.initV2(this, sdkOptions);
        if (NIMUtil.isMainProcess(this)) {
            // 在此处添加以下代码
            com.huawei.hms.support.common.ActivityMgr.INST.init(this);
            HonorPushClient.getInstance().init(getApplicationContext(), true);

        }
    }

}
