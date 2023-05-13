package com.ping.cloudmusicmod;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("com.netease.cloudmusic")) {
            return;
        }

        ClassLoader classLoader = loadPackageParam.classLoader;

        handler_monitorClickFunction(classLoader);

        handler_monitorPlayButton(classLoader);
    }

    public void handler_monitorClickFunction(ClassLoader classLoader) {
        Class c = XposedHelpers.findClass("android.view.View", classLoader);
        XposedBridge.hookAllMethods(c, "performClick", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object listenerInfoObject = XposedHelpers.getObjectField(param.thisObject, "mListenerInfo");
                Object mOnClickListenerObject = XposedHelpers.getObjectField(listenerInfoObject, "mOnClickListener");
                String callbackType = mOnClickListenerObject.getClass().getName();
                LogDebug("---- ---- ---- ---- CLICK FUNCTION : " + callbackType);
            }
        });
    }

    public void handler_monitorPlayButton(ClassLoader classLoader) throws Throwable {
        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.activity.p7", classLoader, "m7", android.view.View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });
    }

    public static void LogDebug(String msg) {
        Log.d("CLOUD_MUSIC_MOD", msg);
        XposedBridge.log(msg);
    }

    public static void LogError(String msg) {
        Log.e("CLOUD_MUSIC_MOD", msg);
        XposedBridge.log(msg);
    }
}
