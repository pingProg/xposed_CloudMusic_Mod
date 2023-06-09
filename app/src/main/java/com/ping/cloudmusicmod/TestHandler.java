package com.ping.cloudmusicmod;

import static com.ping.cloudmusicmod.utils.CommonUtils.LogDebug;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class TestHandler {
    @SuppressWarnings("commented-out-code")
    public static void handler_monitorPlayButton(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.activity.w4", classLoader, "onClick", android.view.View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                LogDebug("---- ---- PLAY BUTTON");
            }
        });
    }

    public static void handler_test(ClassLoader classLoader) {
//        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.service.PlayService", classLoader, "onStopPlayAfterComplete", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//                printStackTrace();
//            }
//
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//            }
//        });
//
//        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.module.player.y", classLoader, "l", java.lang.String.class, java.lang.String.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                LogTemp(String.format("ARGS 1 : %s , 2 : %s", param.args[0], param.args[1]));
//            }
//        });
    }
}
