package com.ping.cloudmusicmod.utils;

import android.app.ActivityManager;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

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
        if(e != null){
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

    public static void makeToast(String msg) {
        Toast.makeText(AndroidAppHelper.currentApplication(), msg, Toast.LENGTH_SHORT).show();
    }

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
