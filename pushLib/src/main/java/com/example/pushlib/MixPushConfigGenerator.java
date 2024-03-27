package com.example.pushlib;

import com.netease.nimlib.sdk.mixpush.MixPushConfig;

public class MixPushConfigGenerator {
    public static MixPushConfig loadPushConfig(){
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
        pushConfig.honorCertificateName = BuildConfig.honorCertificateName;
        pushConfig .vivoCertificateName = BuildConfig.vivoCertificateName;
        pushConfig.oppoAppId = BuildConfig.oppoAppId;
        pushConfig.oppoAppKey = BuildConfig.oppoAppKey;
        // 注意区分AppSercet与MasterSecret
        pushConfig.oppoAppSercet = BuildConfig.oppoAppSercet;
        // 传入云信控制台上配置的oppo推送证书名
        pushConfig.oppoCertificateName = BuildConfig.oppoCertificateName;
        pushConfig.mzAppId = BuildConfig.mzAppId;
        pushConfig.mzAppKey = BuildConfig.mzAppKey;
        pushConfig.mzCertificateName = BuildConfig.mzCertificateName;
        return pushConfig;
    }
}
