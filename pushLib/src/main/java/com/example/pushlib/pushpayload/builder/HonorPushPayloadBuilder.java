package com.example.pushlib.pushpayload.builder;

import android.text.TextUtils;

import com.example.pushlib.BuildConfig;
import com.example.pushlib.pushpayload.IPushPayloadBuilder;
import com.example.pushlib.pushpayload.NotifyClickAction;
import com.example.pushlib.pushpayload.NotifyEffectMode;
import com.example.pushlib.pushpayload.PushPayloadBuilderType;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HonorPushPayloadBuilder implements IPushPayloadBuilder {
    private Map<String, String> mCustomDataMap;
    private NotifyClickAction mClickAction;

    private String mImportance = BuildConfig.honorImportance;

    private Map<String, Object> payloadMap = new HashMap<>();

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
    public IPushPayloadBuilder enableTestPushMode(boolean enable) {
        //测试消息，用于测试接入，上线需要注释。
        if (enable){
            payloadMap.put("targetUserType",1);
        }else {
            payloadMap.put("targetUserType",0);
        }
        return null;
    }

    @Override
    public IPushPayloadBuilder setClickAction(NotifyClickAction clickAction) {
        this.mClickAction = clickAction;
        return null;
    }

    /**
     * 荣耀推送没有提供配置channelId的字段。
     * @param builderType
     * @param channelId
     * @return
     */
    @Override
    public IPushPayloadBuilder addChannelId(PushPayloadBuilderType builderType, String channelId) {
        return null;
    }

    /**
     * 荣耀通过importance来实现的消息分类。
     * @param builderType
     * @param importance
     * Android通知消息分类，决定用户设备消息通知行为，取值如下：
     * LOW：资讯营销类消息
     * NORMAL：服务与通讯类消息
     * @return
     */
    @Override
    public IPushPayloadBuilder addCategory(PushPayloadBuilderType builderType, String importance) {
        mImportance = importance;
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
        notificationMap.put("importance",mImportance);
        payloadMap.put("notification",notificationMap);
        if (mCustomDataMap != null && mCustomDataMap.size() > 0) {
            JSONObject json = new JSONObject(mCustomDataMap);
            payloadMap.put("data", json.toString());
        }
        return payloadMap;
    }
}
