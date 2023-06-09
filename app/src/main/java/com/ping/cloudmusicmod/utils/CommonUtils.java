package com.ping.cloudmusicmod.utils;

import android.app.ActivityManager;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
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

    @SuppressWarnings("unused")
    public static void printStackTrace() {
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        LogDebug("{{{{ {{{{ {{{{ STACK TRACK");
        for (StackTraceElement element : stackElements) {
            LogDebug("at " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element.getLineNumber() + ")");
        }
        LogDebug("}}}} }}}} }}}} STACK TRACK");
    }

    @SuppressWarnings("unused")
    // NOTE 此函数可以在某个类的所有函数执行的时候，打印出执行的函数名
    public static void detectAllRunningFunctionOfTheClass(ClassLoader classLoader, String className) {
        Class<?> c;
        try {
            c = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Method[] mds = c.getDeclaredMethods();
        for (final Method md : mds) {
            int mod = md.getModifiers();
            //去除抽象、native、接口方法
            if (!Modifier.isAbstract(mod)
                    && !Modifier.isNative(mod)
                    && !Modifier.isAbstract(mod)) {
                XposedBridge.hookMethod(md, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        LogDebug(md.toString());
                    }
                });
            }
        }
    }

    public static void makeToastShortTime(String msg) {
        Toast.makeText(AndroidAppHelper.currentApplication(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void makeToastLongTime(String msg) {
        Toast.makeText(AndroidAppHelper.currentApplication(), msg, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("unused")
    public static void makeToastForDebug(String msg) {
        makeToastShortTime(msg);
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
