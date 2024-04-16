package com.example.pushlib.pushpayload;

import android.content.Intent;
import android.text.TextUtils;

import com.example.pushlib.BuildConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VivoPushPayloadBuilder implements IPushPayloadBuilder{
    private Map<String, Object> payloadMap = new HashMap<>();

    private Map<String, String> mCustomDataMap;
    private NotifyClickAction mClickAction;
    private String mChannelId = BuildConfig.vivoChannelId;
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
     * 推送模式 0：正式推送；1：测试推送，不填默认为0（测试推送，只能给web界面录入的测试用户推送；审核中应用，只能用测试推送）
     * @param enable
     * @return
     */
    @Override
    public IPushPayloadBuilder enableTestPushMode(boolean enable) {
        if (enable){
//            payloadMap.put("pushMode",1);
        }else {
//            payloadMap.put("pushMode",0);

        }
        //测试配置
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
     * 点击跳转类型 1：打开APP首页 2：打开链接 3：自定义 4:打开app内指定页面
     * @return
     */
    @Override
    public Map<String, Object> generatePayload() {
        if (mClickAction != null) {
            NotifyEffectMode effectMode = mClickAction.getNotifyEffect();
            switch (effectMode) {
                case EFFECT_MODE_APP:
                    payloadMap.put("skipType", 1);
                    break;
                case EFFECT_MODE_CONTENT:
                    payloadMap.put("skipType",4);
                    //vivo的clientCustomMap并不生效，在这里把vivo的自定义参数添加到intentUri中。
                    //另外vivo这里的intentUri没有办法显示方式直接打开actvity，只能隐式方式通过data或者action打开页面
                    String intentFilterString = mClickAction.getIntentFilterString(mCustomDataMap);
                    payloadMap.put("skipContent",intentFilterString);
                    break;
                case EFFECT_MODE_WEB:
                    payloadMap.put("skipType", 2);
                    payloadMap.put("skipContent", mClickAction.getWebUrl());
                    break;
            }
        }
        //这里vivo的clientCustomMap并不生效，需要在pushpayload的根目录添加，或者在添加到intentUri中。
//        if (mCustomDataMap != null && mCustomDataMap.size() > 0) {
//            payloadMap.put("clientCustomMap", mCustomDataMap);
//        }

        //默认使用IM类型的消息分类
        payloadMap.put("classification",1);
        payloadMap.put("category","IM");

        return payloadMap;    }
}
