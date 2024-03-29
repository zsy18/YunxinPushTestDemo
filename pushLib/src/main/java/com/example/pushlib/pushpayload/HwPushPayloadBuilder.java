package com.example.pushlib.pushpayload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.pushlib.BuildConfig;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class HwPushPayloadBuilder implements IPushPayloadBuilder {
    private String tittle;
    private String body;
    private String clickAction;

    private Context context;

    public HwPushPayloadBuilder(Context context) {
        this.context = context;
    }

    @Override
    public IPushPayloadBuilder setPushTittle(String tittle) {
        return null;
    }

    @Override
    public IPushPayloadBuilder setPushContent(String pushContent) {
        return null;
    }

    @Override
    public IPushPayloadBuilder addCustomData(String key, String data) {
        return null;
    }

    @Override
    public IPushPayloadBuilder setClickAction(NotifyClickAction clickAction) {

        return null;
    }

    @Override
    public Map<String, Object> generatePayload() {
        return null;
    }
}
