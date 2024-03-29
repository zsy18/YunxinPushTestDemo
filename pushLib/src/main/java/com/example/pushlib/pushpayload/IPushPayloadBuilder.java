package com.example.pushlib.pushpayload;

import android.app.Activity;

import java.util.Map;

public interface IPushPayloadBuilder {
    IPushPayloadBuilder setPushTittle(String tittle);
    IPushPayloadBuilder setPushContent(String pushContent);
    IPushPayloadBuilder addCustomData(String key,String data);
    IPushPayloadBuilder setClickAction(NotifyClickAction clickAction);
    Map<String, Object> generatePayload();
}
