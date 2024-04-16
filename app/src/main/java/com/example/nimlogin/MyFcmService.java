package com.example.nimlogin;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.netease.nimlib.mixpush.fcm.FCMTokenService;

public class MyFcmService extends FCMTokenService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.e("ActivityTaskManager","onMessageReceived");
        wakeLock();
        Intent intent = new Intent(this, NotificationClassClickActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Toast.makeText(this, "onMessageReceived", Toast.LENGTH_SHORT).show();
        startActivity(intent);

    }
    private static PowerManager mPowerManager;
    private static PowerManager.WakeLock mWakeLock;
    @SuppressLint("InvalidWakeLockTag")
    private void wakeLock(){

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp::MyWakelockTag");
        wakeLock.acquire(30000);
//        boolean screenOn = mPowerManager.isScreenOn();
//        if (!screenOn) {
        //屏幕会持续点亮
        //释放锁，以便3s后熄屏
//            mWakeLock.release();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                wakeLock.release();
//            }
//        }, 3*1000);
//        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("mytest","onNewToken");

    }
}
