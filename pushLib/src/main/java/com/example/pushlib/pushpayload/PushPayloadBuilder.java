package com.example.pushlib.pushpayload;

import android.text.TextUtils;
import android.util.Log;

import com.example.pushlib.BuildConfig;
import com.example.pushlib.pushpayload.builder.APNsPushPayloadBuilder;
import com.example.pushlib.pushpayload.builder.FcmPushPayloadBuilder;
import com.example.pushlib.pushpayload.builder.FcmV1PushPayloadBuilder;
import com.example.pushlib.pushpayload.builder.HonorPushPayloadBuilder;
import com.example.pushlib.pushpayload.builder.HwPushPayloadBuilder;
import com.example.pushlib.pushpayload.builder.OppoPushPayloadBuilder;
import com.example.pushlib.pushpayload.builder.VivoPushPayloadBuilder;
import com.example.pushlib.pushpayload.builder.XmPushPayloadBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PushPayloadBuilder implements IPushPayloadBuilder {
    private static final String TAG = "PushPayloadBuilder";
    private Map<String, Object> mPushPayloadMap = new HashMap<>();
    private String pushTitle;
    private Map<PushPayloadBuilderType, IPushPayloadBuilder> mBuilderMap = new HashMap<>();

    public PushPayloadBuilder() {
        //根据config.gradle文件中是否配置了云信平台的推送证书来决定是否生成对应的payload字段
        if (!TextUtils.isEmpty(BuildConfig.hwCertificateName)) {
            mBuilderMap.put(PushPayloadBuilderType.HUAWEI, new HwPushPayloadBuilder());
        }
        if (!TextUtils.isEmpty(BuildConfig.oppoCertificateName)) {
            mBuilderMap.put(PushPayloadBuilderType.OPPO, new OppoPushPayloadBuilder());
        }
        if (!TextUtils.isEmpty(BuildConfig.xmCertificateName)){
            mBuilderMap.put(PushPayloadBuilderType.XIAOMI, new XmPushPayloadBuilder());
        }
        if (!TextUtils.isEmpty(BuildConfig.honorCertificateName)){
            mBuilderMap.put(PushPayloadBuilderType.HONER, new HonorPushPayloadBuilder());
        }

        if (!TextUtils.isEmpty(BuildConfig.vivoCertificateName)){
            mBuilderMap.put(PushPayloadBuilderType.VIVO, new VivoPushPayloadBuilder());
        }
        if (!TextUtils.isEmpty(BuildConfig.fcmCertificateName)){
            if (BuildConfig.fcmHttpV1Enable){
                mBuilderMap.put(PushPayloadBuilderType.FCMV1, new FcmV1PushPayloadBuilder());
            }else {
                mBuilderMap.put(PushPayloadBuilderType.FCM, new FcmPushPayloadBuilder());
            }
        }
        if (BuildConfig.apnsEnable){
            mBuilderMap.put(PushPayloadBuilderType.APNS, new APNsPushPayloadBuilder());
        }


    }

    /**
     * 设置推送标题，各厂商的PushPayloadBuilder不需要实现该方法，因为推送的标题会放在pushPayload的最外层结构中。
     * 另外推送的内容直接通过{@link com.netease.nimlib.sdk.msg.model.IMMessage#setPushContent(String)}
     * 设置
     *
     * @param title
     * @return
     */
    @Override
    public IPushPayloadBuilder setPushTitle(String title) {
        pushTitle = title;
        return this;
    }

    /**
     * 添加自定义的厂商推送字段的PushPayloadBuilder
     *
     * @param type
     * @param payloadBuilder
     */
    public void addCustomPushPayloadBuilder(PushPayloadBuilderType type, IPushPayloadBuilder payloadBuilder) {
        mBuilderMap.put(type, payloadBuilder);
    }
    @Override
    public IPushPayloadBuilder addCustomData(String key, String data) {
        for (IPushPayloadBuilder value : mBuilderMap.values()) {
            value.addCustomData(key, data);
        }
        return this;
    }

    @Override
    public IPushPayloadBuilder enableTestPushMode(boolean enable) {
        for (IPushPayloadBuilder value : mBuilderMap.values()) {
            value.enableTestPushMode(enable);
        }
        return this;
    }

    @Override
    public IPushPayloadBuilder setClickAction(NotifyClickAction clickAction) {
        for (IPushPayloadBuilder value : mBuilderMap.values()) {
            value.setClickAction(clickAction);
        }
        return this;
    }

    @Override
    public IPushPayloadBuilder addChannelId(PushPayloadBuilderType builderType, String channelId) {
        IPushPayloadBuilder payloadBuilder = mBuilderMap.get(builderType);
        if (payloadBuilder != null) {
            payloadBuilder.addChannelId(builderType, channelId);
        }
        return this;
    }

    @Override
    public IPushPayloadBuilder addCategory(PushPayloadBuilderType builderType, String channelId) {
        IPushPayloadBuilder payloadBuilder = mBuilderMap.get(builderType);
        if (payloadBuilder != null) {
            payloadBuilder.addCategory(builderType, channelId);
        }
        return this;
    }

    @Override
    public Map<String, Object> generatePayload() {
        for (PushPayloadBuilderType pushPayloadBuilderType : mBuilderMap.keySet()) {
            //获取各个厂商实际的payloadbuilder
            IPushPayloadBuilder payloadBuilder = mBuilderMap.get(pushPayloadBuilderType);
            //生成各个厂商所需的payload字段
            Map<String, Object> payloadMap = payloadBuilder.generatePayload();
            if (payloadMap == null) {
                continue;
            }
            if (pushPayloadBuilderType == PushPayloadBuilderType.XIAOMI) {
                //如果是小米，其厂商的payload字段在json结构的最外层。
                mPushPayloadMap.putAll(payloadMap);
            } else {
                //其他厂商的字段在json中有固定的json属性，其属性为PushPayloadBuilderType对应的string值。
                mPushPayloadMap.put(pushPayloadBuilderType.getType(), payloadMap);
            }
        }
        if (!TextUtils.isEmpty(pushTitle)) {
            mPushPayloadMap.put("pushTitle", pushTitle);
        }
        JSONObject json = new JSONObject(mPushPayloadMap);
        String jsonString = json.toString();
        Log.e(TAG, "jsonString: " + jsonString);
        return mPushPayloadMap;
    }

}
