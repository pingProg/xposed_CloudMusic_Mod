package com.ping.cloudmusicmod;

import static com.ping.cloudmusicmod.utils.CommonUtils.LogDebug;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogInfo;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

import java.lang.reflect.Field;

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

        handler_initOnStart(classLoader);

        handler_monitorClickFunction(classLoader);

        handler_monitorPlayButton(classLoader);
    }

    public void handler_initOnStart(ClassLoader classLoader) {
        //NOTE ：监听APP初始化时调用的打印SDKCache的函数，详情查看Log的SDKCache在APP初始化时的调用时机
        XposedHelpers.findAndHookMethod("j.l.s.f.d.b", classLoader, "a", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                LogInfo("APP初始化");
                Data.resetData();
            }
        });
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
}
