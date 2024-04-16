package com.example.pushlib.pushpayload.builder;

import android.content.Intent;
import android.text.TextUtils;

import com.example.pushlib.BuildConfig;
import com.example.pushlib.pushpayload.IPushPayloadBuilder;
import com.example.pushlib.pushpayload.NotifyClickAction;
import com.example.pushlib.pushpayload.NotifyEffectMode;
import com.example.pushlib.pushpayload.PushPayloadBuilderType;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OppoPushPayloadBuilder implements IPushPayloadBuilder {
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
     * oppo官网内容
     * 点击通知栏后触发的动作类型。
     * 点击动作类型值的定义和含义如下：
     * 0.启动应用；
     * 1.跳转指定应用内页（action标签名）；
     * 2.跳转网页；
     * 4.跳转指定应用内页（全路径类名）；【非必填，默认值为0】;
     * 5.跳转Intent scheme URL
     *
     * @return
     */
    @Override
    public Map<String, Object> generatePayload() {
        Map<String, Object> payloadMap = new HashMap<>();
        if (mClickAction != null) {
            NotifyEffectMode effectMode = mClickAction.getNotifyEffect();
            switch (effectMode) {
                case EFFECT_MODE_APP:
                    payloadMap.put("click_action_type", 0);
                    break;
                case EFFECT_MODE_CONTENT:
                    if (mClickAction.getClickActivity() != null) {
                        //如果配置了跳转的activity优先使用 vivo 跳转指定应用内页（全路径类名）的方式
                        payloadMap.put("click_action_type", 4);
                        payloadMap.put("click_action_activity", mClickAction.getClickActivity().getName());
                    } else if (!Intent.ACTION_VIEW.equals(mClickAction.getIntentAction())){
                       //如果配置了自定义的action，使用action标签的方式
                        payloadMap.put("click_action_type", 1);
                        payloadMap.put("click_action_activity", mClickAction.getIntentAction());
                    }else if (mClickAction.getIntentSchemeUri()!=null){
                        //如果配置了data数据，使用vivo跳转Intent scheme URL的方式
                        payloadMap.put("click_action_type", 5);
                        payloadMap.put("click_action_url", mClickAction.getIntentSchemeUri().toString());
                    }
                    break;
                case EFFECT_MODE_WEB:
                    payloadMap.put("click_action_type", 2);
                    payloadMap.put("click_action_url", mClickAction.getWebUrl());
                    break;
            }
        }
        if (mCustomDataMap != null && mCustomDataMap.size() > 0) {
            JSONObject json = new JSONObject(mCustomDataMap);
            //oppo的自定义字段只能通过改字段携带，放在Intent scheme URL中无效的。
            payloadMap.put("action_parameters", json.toString());
        }
        if (!TextUtils.isEmpty(mChannelId)){
            payloadMap.put("channel_id",mChannelId);
        }
        return payloadMap;
    }
}
