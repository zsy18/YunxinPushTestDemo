package com.example.pushlib.pushpayload;

import android.app.Activity;
import android.app.Notification;

import java.util.List;

public class NotifyClickAction {
    private NotifyEffectMode notifyEffect;
    private Class<? extends Activity> clickActivity;
    private String intentAction;
    private List<String> intentCategory;
    private String intentDataScheme;
    private String intentDataHost;
    private String intentDataPath;



    public class Builder {
        private NotifyClickAction notifyClickAction;

        public Builder() {
            notifyClickAction = new NotifyClickAction();
            notifyClickAction.notifyEffect = NotifyEffectMode.EFFECT_MODE_APP;
        }

        public Builder setNotifyEffect(NotifyEffectMode notifyEffect) {
            notifyClickAction.notifyEffect = notifyEffect;
            return this;
        }

        public Builder setClickActivity(Class<? extends Activity> clickActivity) {
            notifyClickAction.clickActivity = clickActivity;
            return this;

        }

        public Builder setIntentAction(String intentAction) {
            notifyClickAction.intentAction = intentAction;
            return this;

        }

        public Builder addIntentCategory(String intentCategory) {
            notifyClickAction.intentCategory.add(intentCategory);
            return this;
        }

        public Builder setIntentDataScheme(String intentDataScheme) {
            notifyClickAction.intentDataScheme = intentDataScheme;
            return this;

        }

        public Builder setIntentDataHost(String intentDataHost) {
            notifyClickAction.intentDataHost = intentDataHost;
            return this;
        }

        public Builder setIntentDataPath(String intentDataPath) {
            notifyClickAction.intentDataPath = intentDataPath;
            return this;
        }
        public NotifyClickAction build(){
            return notifyClickAction;
        }

    }
}
