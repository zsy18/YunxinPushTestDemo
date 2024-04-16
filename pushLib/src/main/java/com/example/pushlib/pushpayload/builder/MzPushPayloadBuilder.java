package com.example.pushlib.pushpayload.builder;

import com.example.pushlib.pushpayload.IPushPayloadBuilder;
import com.example.pushlib.pushpayload.NotifyClickAction;
import com.example.pushlib.pushpayload.PushPayloadBuilderType;

import java.util.Map;

/**
 * 魅族推送暂只提供 clickTypeInfo.parameters 的自定义配置，直接使用小米的自定义参数配置，这里不做任何实现。
 */
public class MzPushPayloadBuilder implements IPushPayloadBuilder {
    @Override
    public IPushPayloadBuilder setPushTitle(String title) {
        return null;
    }

    @Override
    public IPushPayloadBuilder addCustomData(String key, String data) {
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
    public IPushPayloadBuilder addCategory(PushPayloadBuilderType builderType, String channelId) {
        return null;
    }

    @Override
    public Map<String, Object> generatePayload() {
        return null;
    }
}
