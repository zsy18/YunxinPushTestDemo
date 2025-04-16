package com.example.pushlib.pushpayload.builder;

import com.example.pushlib.pushpayload.IPushPayloadBuilder;
import com.example.pushlib.pushpayload.NotifyClickAction;
import com.example.pushlib.pushpayload.PushPayloadBuilderType;

import java.util.HashMap;
import java.util.Map;

public class APNsPushPayloadBuilder implements IPushPayloadBuilder {
    private Map<String, String> mCustomDataMap;
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
        return null;
    }

    @Override
    public IPushPayloadBuilder setClickAction(NotifyClickAction clickAction) {
        return null;
    }

    @Override
    public IPushPayloadBuilder addChannelId(PushPayloadBuilderType builderType, String channelId) {
        return null;
    }

    @Override
    public IPushPayloadBuilder addCategory(PushPayloadBuilderType builderType, String category) {
        return null;
    }

    @Override
    public Map<String, Object> generatePayload() {
        if (mCustomDataMap!=null&& !mCustomDataMap.isEmpty()){
            payloadMap.putAll(mCustomDataMap);
            return payloadMap;
        }else {
            return null;
        }
    }
}
