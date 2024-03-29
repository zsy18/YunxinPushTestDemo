package com.example.pushlib.pushpayload;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.example.pushlib.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public class PushPayloadBuilder implements IPushPayloadBuilder {

    Map<PushPayloadBuilderType,IPushPayloadBuilder> builderMap = new HashMap<>();
    public PushPayloadBuilder(Context context){
        //根据config.gradle文件中是否配置了云信平台的推送证书来决定是否生成对应的payload字段
        if (!TextUtils.isEmpty(BuildConfig.hwCertificateName)){
            builderMap.put(PushPayloadBuilderType.HUAWEI,new HwPushPayloadBuilder(context));
        }
    }

    @Override
    public IPushPayloadBuilder setPushTittle(String tittle) {
        for (IPushPayloadBuilder value : builderMap.values()) {
            value.setPushTittle(tittle);
        }
        return this;
    }

    @Override
    public IPushPayloadBuilder setPushContent(String pushContent) {
        return this;
    }

    @Override
    public IPushPayloadBuilder addCustomData(String key, String data) {
        return this;
    }

    @Override
    public IPushPayloadBuilder setClickAction(NotifyClickAction clickAction){
        return this;
    }

    @Override
    public Map<String, Object> generatePayload() {

        return null;
    }

}
