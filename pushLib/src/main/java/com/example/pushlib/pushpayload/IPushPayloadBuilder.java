package com.example.pushlib.pushpayload;

import java.util.Map;

public interface IPushPayloadBuilder {
    /**
     * 设置推送标题，各厂商的PushPayloadBuilder不需要实现该方法，因为推送的标题会放在pushPayload的最外层结构中。
     * 另外推送的内容直接通过{@link com.netease.nimlib.sdk.msg.model.IMMessage#setPushContent(String)}
     * 设置
     * @param title
     * @return
     */
    IPushPayloadBuilder setPushTitle(String title);

    /**
     * 添加自定义推送字段
     * @param key
     * @param data
     * @return
     */
    IPushPayloadBuilder addCustomData(String key,String data);

    /**
     * 开启推送的测试开关，默认关闭，上架前必须要关闭。部分厂商支持审核中应用测试推送，如vivo、华为、荣耀
     * @param enable
     * @return
     */
    IPushPayloadBuilder enableTestPushMode(boolean enable);

    /**
     * 添加点击跳转行为
     * @param clickAction
     * @return
     */
    IPushPayloadBuilder setClickAction(NotifyClickAction clickAction);

    IPushPayloadBuilder addChannelId(PushPayloadBuilderType builderType,String channelId);
    /**
     * 生成需要的payload map数据
     * @return
     */
    Map<String, Object> generatePayload();


}
