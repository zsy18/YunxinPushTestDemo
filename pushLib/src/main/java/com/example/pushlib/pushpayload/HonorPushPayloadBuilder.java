package com.example.pushlib.pushpayload;

import android.text.TextUtils;

import com.example.pushlib.BuildConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HonorPushPayloadBuilder implements IPushPayloadBuilder{
    private Map<String, String> mCustomDataMap;
    private NotifyClickAction mClickAction;
    private String mChannelId = BuildConfig.hwChannelId;
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
     * 消息点击行为类型，取值如下：
     * 1：打开应用自定义页面
     * 2：点击后打开特定URL
     * 3：点击后打开应用
     * @return
     */
    @Override
    public Map<String, Object> generatePayload() {
        Map<String, Object> payloadMap = new HashMap<>();
        Map<String, Object> notificationMap = new HashMap<>();
        if (mClickAction != null) {
            Map<String, Object> clickActionMap = new HashMap<>();
            NotifyEffectMode effectMode = mClickAction.getNotifyEffect();
            switch (effectMode) {
                case EFFECT_MODE_APP:
                    clickActionMap.put("type",3);
                    break;
                case EFFECT_MODE_CONTENT:
                    clickActionMap.put("type",1);
                    //华为的自定义信息可以携带在intent uri中
                    String intentFilterString = mClickAction.getIntentFilterString();
                    clickActionMap.put("intent",intentFilterString);
                    break;
                case EFFECT_MODE_WEB:
                    clickActionMap.put("type",2);
                    clickActionMap.put("url",mClickAction.getWebUrl());
                    break;
            }
            notificationMap.put("clickAction",clickActionMap);

        }
        notificationMap.put("importance","NORMAL");
        payloadMap.put("notification",notificationMap);

        if (mCustomDataMap != null && mCustomDataMap.size() > 0) {
            JSONObject json = new JSONObject(mCustomDataMap);
            payloadMap.put("data", json.toString());
        }
        //测试消息，用于测试接入，上线需要注释。
        payloadMap.put("target_user_type",1);
        return payloadMap;
    }
}
