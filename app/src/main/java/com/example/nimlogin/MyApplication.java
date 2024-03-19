package com.example.nimlogin;

import android.app.Application;
import android.graphics.Color;
import android.text.TextUtils;

import com.netease.nimlib.push.net.lbs.IPVersion;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NotificationFoldStyle;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.ServerAddresses;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusBarNotificationFilter;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.mixpush.MixPushConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
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
        sdkOptions.mixPushConfig = loadPushConfig();
        sdkOptions.sdkStorageRootPath = getExternalCacheDir().getPath() + "/nim";

        NIMClient.init(this, loginInfo, sdkOptions);
        if (NIMUtil.isMainProcess(this)) {

            // 在此处添加以下代码
            com.huawei.hms.support.common.ActivityMgr.INST.init(this);

        }
    }
    private MixPushConfig loadPushConfig(){
        MixPushConfig pushConfig = new MixPushConfig();
        //获取config.gradle文件中的厂商推送配置信息
        pushConfig.fcmCertificateName = BuildConfig.fcmCertificateName;
        // 传入从小米推送平台获取到的AppId与AppKey,以及云信控制台上小米推送对应的证书名
        pushConfig.xmAppId = BuildConfig.xmAppId;
        pushConfig.xmAppKey = BuildConfig.xmAppKey;
        pushConfig.xmCertificateName = BuildConfig.xmCertificateName;

        // 华为推送
        pushConfig.hwAppId = BuildConfig.hwAppId;
        pushConfig.hwCertificateName = BuildConfig.hwCertificateName;
        return pushConfig;
    }
}
