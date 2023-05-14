package com.ping.cloudmusicmod.utils;

import android.util.Log;

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

    public static void LogError(String msg) {
        Log.e("CLOUD_MUSIC_MOD", msg);
        XposedBridge.log(msg);
    }

    private static void printStackTrace() throws Throwable {
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        LogDebug("{{{{ {{{{ {{{{ STACK TRACK");
        for (int i = 0; i < stackElements.length; i++) {
            StackTraceElement element = stackElements[i];
            LogDebug("at " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element.getLineNumber() + ")");
        }
        LogDebug("}}}} }}}} }}}} STACK TRACK");
    }
}
