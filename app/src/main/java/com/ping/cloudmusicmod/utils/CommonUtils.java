package com.ping.cloudmusicmod.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import de.robv.android.xposed.XposedBridge;

public class CommonUtils {
    public static void LogDebug(String msg) {
        Log.d("CLOUD_MUSIC_MOD", msg);
        XposedBridge.log(msg);
    }

    public static void LogTemp(String msg) {
        Log.d("CLOUD_MUSIC_MOD", msg);
        XposedBridge.log(msg);
    }

    public static void LogInfo(String msg) {
        Log.i("CLOUD_MUSIC_MOD", msg);
        XposedBridge.log(msg);
    }

    public static void LogError(String msg, Exception e) {
        Log.e("CLOUD_MUSIC_MOD", msg);
        XposedBridge.log(msg);
        if (e != null) {
            XposedBridge.log(e);
        }
    }

    public static void printStackTrace() throws Throwable {
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        LogDebug("{{{{ {{{{ {{{{ STACK TRACK");
        for (int i = 0; i < stackElements.length; i++) {
            StackTraceElement element = stackElements[i];
            LogDebug("at " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element.getLineNumber() + ")");
        }
        LogDebug("}}}} }}}} }}}} STACK TRACK");
    }

    public static void makeToastShortTime(String msg) {
        Toast.makeText(AndroidAppHelper.currentApplication(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void makeToastLongTime(String msg) {
        Toast.makeText(AndroidAppHelper.currentApplication(), msg, Toast.LENGTH_LONG).show();
    }

//    @SuppressLint("MissingPermission")
//    public static void makeVibration(Context context) {
//        // 在合适的位置获取Vibrator实例
//        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        // 检查设备是否支持震动
//        if (vibrator.hasVibrator()) {
//            // 定义震动模式，以数组形式表示持续时间和等待时间的交替
//            long[] pattern = {0, 100, 200, 300, 400};
//            // -1表示不重复，0表示从指定位置开始循环震动模式
//            int repeat = -1;
//            // 生成自定义的震动
//            vibrator.vibrate(pattern, repeat);
//        }
//    }

    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses()) {
                if (processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }

        return null;
    }
}
