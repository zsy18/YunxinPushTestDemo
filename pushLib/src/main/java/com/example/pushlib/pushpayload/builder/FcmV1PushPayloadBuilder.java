package com.example.pushlib.pushpayload.builder;

import android.text.TextUtils;

import com.example.pushlib.BuildConfig;
import com.example.pushlib.pushpayload.IPushPayloadBuilder;
import com.example.pushlib.pushpayload.NotifyClickAction;
import com.example.pushlib.pushpayload.NotifyEffectMode;
import com.example.pushlib.pushpayload.PushPayloadBuilderType;

import java.util.HashMap;
import java.util.Map;

public class FcmV1PushPayloadBuilder implements IPushPayloadBuilder {
    private Map<String, String> mCustomDataMap;
    private NotifyClickAction mClickAction;
    private String mChannelId = BuildConfig.fcmChannelId;

    private Map<String, Object> mPayloadMap = new HashMap<>();
    private Map<String, Object> mMessageMap = new HashMap<>();

    private Map<String, Object> mAndroidMap = new HashMap<>();
    private Map<String, Object> mNotificationMap = new HashMap<>();


    @Override
    public IPushPayloadBuilder setPushTitle(String title) {
        return null;
    }

    @Override
    public IPushPayloadBuilder addCustomData(String key, String data) {
        if (mCustomDataMap == null) {
            mCustomDataMap = new HashMap<>();
        }
        mCustomDataMap.put(key, data);
        return null;
    }

    /**
     * fcm推送没有提供测试开关字段
     *
     * @param enable
     * @return
     */
    @Override
    public IPushPayloadBuilder enableTestPushMode(boolean enable) {
        return null;
    }

    @Override
    public IPushPayloadBuilder setClickAction(NotifyClickAction clickAction) {
        this.mClickAction = clickAction;

        return null;
    }

    @Override
    public IPushPayloadBuilder addChannelId(PushPayloadBuilderType builderType, String channelId) {
        mChannelId = channelId;
        return null;
    }

    /**
     * fcm暂时不支持该配置
     * @param builderType
     * @param channelId
     * @return
     */
    @Override
    public IPushPayloadBuilder addCategory(PushPayloadBuilderType builderType, String channelId) {
        return null;
    }

    @Override
    public Map<String, Object> generatePayload() {
        if (mClickAction != null) {
            NotifyEffectMode effectMode = mClickAction.getNotifyEffect();
            switch (effectMode) {
                case EFFECT_MODE_APP:
                    break;
                case EFFECT_MODE_CONTENT:
                    //目前fcm推送只支持action的启动自定义页面
                    mNotificationMap.put("click_action", mClickAction.getIntentAction());
                    break;
                case EFFECT_MODE_WEB:
                    mNotificationMap.put("click_action", mClickAction.getWebUrl());

                    break;
            }
        }
        if (mCustomDataMap != null && mCustomDataMap.size() > 0) {
            mAndroidMap.put("data", mCustomDataMap);
        }
        if (!TextUtils.isEmpty(mChannelId)) {
            mNotificationMap.put("channel_id", mChannelId);
        }
        if (mNotificationMap.size() > 0) {
            mAndroidMap.put("notification", mNotificationMap);
        }
        mMessageMap.put("data", mCustomDataMap);
        //使用数据消息，需要添加该配置，并且联系技术支持邮件开通
//        mMessageMap.put("needNotification",false);
        mMessageMap.put("android", mAndroidMap);
        mPayloadMap.put("message", mMessageMap);
        return mPayloadMap;
    }
}
