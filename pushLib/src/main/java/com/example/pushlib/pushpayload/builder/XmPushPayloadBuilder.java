package com.example.pushlib.pushpayload.builder;

import android.text.TextUtils;

import com.example.pushlib.BuildConfig;
import com.example.pushlib.pushpayload.IPushPayloadBuilder;
import com.example.pushlib.pushpayload.NotifyClickAction;
import com.example.pushlib.pushpayload.NotifyEffectMode;
import com.example.pushlib.pushpayload.PushPayloadBuilderType;

import java.util.HashMap;
import java.util.Map;

public class XmPushPayloadBuilder implements IPushPayloadBuilder {
    private Map<String, String> mCustomDataMap;
    private NotifyClickAction mClickAction;
    private String mChannelId = BuildConfig.oppoChannelId;

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
     * 小米不支持调试模式
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

    @Override
    public IPushPayloadBuilder addCategory(PushPayloadBuilderType builderType, String channelId) {
        return null;
    }

    /**
     * 通过设置extra.notify_effect的值以得到不同的预定义点击行为。
     * “1″：通知栏点击后打开app的Launcher Activity。
     * “2″：通知栏点击后打开app的任一Activity（开发者还需要传入extra.intent_uri）。
     * “3″：通知栏点击后打开网页（开发者还需要传入extra.web_uri）。
     * @return
     */
    @Override
    public Map<String, Object> generatePayload() {
        Map<String, Object> payloadMap = new HashMap<>();
        if (mClickAction != null) {
            NotifyEffectMode effectMode = mClickAction.getNotifyEffect();
            switch (effectMode) {
                case EFFECT_MODE_APP:
                    payloadMap.put("notify_effect","1");
                    break;
                case EFFECT_MODE_CONTENT:
                    payloadMap.put("notify_effect","2");
                    String intentFilterString = mClickAction.getIntentFilterString();
                    payloadMap.put("intent_uri",intentFilterString);
                    break;
                case EFFECT_MODE_WEB:
                    payloadMap.put("notify_effect","3");
                    payloadMap.put("web_uri",mClickAction.getWebUrl());
                    break;
            }
        }
        if (!TextUtils.isEmpty(mChannelId)){
            payloadMap.put("channel_id",mChannelId);
        }
        if (mCustomDataMap!=null&&mCustomDataMap.size()>0){
            payloadMap.putAll(mCustomDataMap);
        }
        return payloadMap;
    }
}
