package com.example.pushlib.pushpayload.builder;

import android.text.TextUtils;

import com.example.pushlib.BuildConfig;
import com.example.pushlib.pushpayload.IPushPayloadBuilder;
import com.example.pushlib.pushpayload.NotifyClickAction;
import com.example.pushlib.pushpayload.NotifyEffectMode;
import com.example.pushlib.pushpayload.PushPayloadBuilderType;

import java.util.HashMap;
import java.util.Map;

public class FcmPushPayloadBuilder implements IPushPayloadBuilder {
    private Map<String, String> mCustomDataMap;
    private NotifyClickAction mClickAction;
    private String mChannelId = BuildConfig.fcmChannelId;
    private Map<String, Object> mPayloadMap = new HashMap<>();


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

    @Override
    public Map<String, Object> generatePayload() {
        if (mClickAction != null) {
            NotifyEffectMode effectMode = mClickAction.getNotifyEffect();
            switch (effectMode) {
                case EFFECT_MODE_APP:
                    break;
                case EFFECT_MODE_CONTENT:
                    String intentFilterString = mClickAction.getIntentFilterString();
                    mPayloadMap.put("click_action",intentFilterString);
                    break;
                case EFFECT_MODE_WEB:
                    mPayloadMap.put("click_action",mClickAction.getWebUrl());

                    break;
            }
        }

        if (mCustomDataMap != null && mCustomDataMap.size() > 0) {
            mPayloadMap.put("data", mCustomDataMap);
        }

        if (!TextUtils.isEmpty(mChannelId)){
            mPayloadMap.put("channel_id",mChannelId);
        }
        return mPayloadMap;
    }
}
