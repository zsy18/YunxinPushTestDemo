package com.example.pushlib.pushpayload;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;
import java.util.Map;

public class NotifyClickAction {
    private NotifyClickAction() {

    }

    private NotifyEffectMode notifyEffect;
    private Class<? extends Activity> clickActivity;
    private String intentAction = Intent.ACTION_VIEW;
    private List<String> intentCategoryList;
    private String intentDataScheme;
    private String intentDataHost;
    private String intentDataPort;
    private String intentDataPath;
    private String webUrl;

    public NotifyEffectMode getNotifyEffect() {
        return notifyEffect;
    }

    public Class<? extends Activity> getClickActivity() {
        return clickActivity;
    }

    public String getIntentAction() {
        return intentAction;
    }

    public List<String> getIntentCategoryList() {
        return intentCategoryList;
    }

    public String getIntentDataScheme() {
        return intentDataScheme;
    }

    public String getIntentDataHost() {
        return intentDataHost;
    }

    public String getIntentDataPath() {
        return intentDataPath;
    }

    public String getWebUrl() {
        return webUrl;
    }

    protected String getIntentFilterString() {
        return getIntentFilterString(null);

    }

    public String getIntentFilterString(Map<String, String> customData) {
        String intentFilterString = "";
        Intent intent = new Intent(intentAction);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (intentCategoryList != null && intentCategoryList.size() > 0) {
            for (String s : intentCategoryList) {
                intent.addCategory(s);
            }
        }
        Uri intentSchemeUri = getIntentSchemeUri();
        if (intentSchemeUri != null) {
            intent.setData(intentSchemeUri);
        }
        if (customData != null && customData.size() > 0) {
            for (String s : customData.keySet()) {
                intent.putExtra(s, customData.get(s));
            }
        }
        intentFilterString = intent.toUri(Intent.URI_INTENT_SCHEME);
        return intentFilterString;
    }

    public Uri getIntentSchemeUri() {
        if (!TextUtils.isEmpty(intentDataScheme)) {
            StringBuilder uriBuilder = new StringBuilder();
            uriBuilder.append(intentDataScheme);
            if (!TextUtils.isEmpty(intentDataHost)) {
                uriBuilder.append("://").append(intentDataHost);
                if (!TextUtils.isEmpty(intentDataPort)) {
                    uriBuilder.append(":").append(intentDataPort);
                }
                if (!TextUtils.isEmpty(intentDataPath)) {
                    uriBuilder.append(intentDataPath);
                }
            }
            Uri uri = Uri.parse(uriBuilder.toString());
            return uri;
        } else {
            return null;
        }
    }

    public String getIntentDataPort() {
        return intentDataPort;
    }

    public static class Builder {
        private NotifyClickAction notifyClickAction;

        public Builder() {
            notifyClickAction = new NotifyClickAction();
            notifyClickAction.notifyEffect = NotifyEffectMode.EFFECT_MODE_APP;
        }

        /**
         * 设置点击通知栏跳转的行为s
         *
         * @param notifyEffect
         * @return
         */
        public Builder setNotifyEffect(NotifyEffectMode notifyEffect) {
            notifyClickAction.notifyEffect = notifyEffect;
            return this;
        }

        /**
         * 当推送厂商支持指定activity页面的时候，优先使用这种方式
         *
         * @param clickActivity 需要打开的activiyt页面
         * @return
         */
        public Builder setClickActivity(Class<? extends Activity> clickActivity) {
            notifyClickAction.clickActivity = clickActivity;
            return this;
        }

        /**
         * 设置需要打开的activity的intent-filter标签的action字段
         * 如不设置，默认使用android.intent.action.VIEW
         *
         * @param intentAction
         * @return
         */
        public Builder setIntentAction(String intentAction) {
            notifyClickAction.intentAction = intentAction;
            return this;

        }

        /**
         * 天际需要打开的activity的intent-filter标签的category字段
         * 如不设置，默认使用android.intent.category.DEFAULT
         *
         * @param intentCategory
         * @return
         */
        public Builder addIntentCategory(String intentCategory) {
            notifyClickAction.intentCategoryList.add(intentCategory);
            return this;
        }

        /**
         * 设置需要打开的activity的intent-filter标签的data中的Scheme字段
         *
         * @param intentDataScheme
         * @return
         */
        public Builder setIntentDataScheme(String intentDataScheme) {
            notifyClickAction.intentDataScheme = intentDataScheme;
            return this;

        }

        /**
         * 设置需要打开的activity的intent-filter标签的data中的Host字段
         *
         * @param intentDataHost
         * @return
         */
        public Builder setIntentDataHost(String intentDataHost) {
            notifyClickAction.intentDataHost = intentDataHost;
            return this;
        }

        /**
         * 设置需要打开的activity的intent-filter标签的data中的Port字段
         *
         * @param intentDataPort
         * @return
         */
        public Builder setIntentDataPort(String intentDataPort) {
            notifyClickAction.intentDataPort = intentDataPort;
            return this;
        }

        /**
         * 设置需要打开的activity的intent-filter标签的data中的Path字段
         *
         * @param intentDataPath
         * @return
         */
        public Builder setIntentDataPath(String intentDataPath) {
            notifyClickAction.intentDataPath = intentDataPath;
            return this;
        }

        /**
         * 仅当NotifyEffectMode为EFFECT_MODE_WEB，需要启动一个web页面的时候才需要配置
         *
         * @param url 需要打开的url地址
         * @return
         */
        public Builder setWebUrl(String url) {
            notifyClickAction.webUrl = url;
            return this;
        }

        public NotifyClickAction build() {
            return notifyClickAction;
        }

    }
}
