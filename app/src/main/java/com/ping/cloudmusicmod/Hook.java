package com.ping.cloudmusicmod;

import static com.ping.cloudmusicmod.Data.REP_FALSE;
import static com.ping.cloudmusicmod.Data.REP_INIT;
import static com.ping.cloudmusicmod.Data.REP_REPLAYED;
import static com.ping.cloudmusicmod.Data.REP_TRUE;
import static com.ping.cloudmusicmod.Data.REP_WILL_REPLAY;
import static com.ping.cloudmusicmod.Data.resetAllData;
import static com.ping.cloudmusicmod.Data.resetPlayingData;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogDebug;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogError;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogInfo;
import static com.ping.cloudmusicmod.utils.PlayerUtils.KeyPlayPause;
import static com.ping.cloudmusicmod.utils.PlayerUtils.KeyPrevious;
import static com.ping.cloudmusicmod.utils.PlayerUtils.isShortSongs;

import android.app.AndroidAppHelper;
import android.widget.Toast;

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

        handler_moduleToggle(classLoader);

        handler_playNext(classLoader);

        handler_playPrev(classLoader);
    }

    public void handler_initOnStart(ClassLoader classLoader) {
        //NOTE ：监听APP初始化时调用的打印SDKCache的函数，详情查看Log的SDKCache在APP初始化时的调用时机
        XposedHelpers.findAndHookMethod("j.l.s.f.d.b", classLoader, "a", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                LogInfo("APP初始化");
                resetAllData();
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

    public void handler_monitorPlayButton(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.activity.p7", classLoader, "m7", android.view.View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });
    }

    public void handler_moduleToggle(ClassLoader classLoader) {
        //NOTE : 监控播放器右上角“投屏”按钮
        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.activity.p7", classLoader, "k7", android.view.View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Data.setToggle(!Data.getToggle());
                resetPlayingData();
                Toast.makeText(AndroidAppHelper.currentApplication(), String.format("xposed模块功能：%s", Data.getToggle() ? "开启" : "关闭"), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void handler_playNext(ClassLoader classLoader) throws Throwable {
        // HOOK的方法：
        // [只在播放自然完成时调用] public void next()
        // [所有next逻辑都需要调用] public void next(boolean z, boolean z2, @Nullable MusicEndConfig musicEndConfig)
        // public void next()也会调用一次public void next(boolean z, boolean z2, @Nullable MusicEndConfig musicEndConfig)，
        // 所以需要按照参数个数区分开来


        final Class c = classLoader.loadClass("com.netease.cloudmusic.service.PlayService");
        XposedBridge.hookAllMethods(c, "next", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (!Data.getToggle()) {
                    return;
                }

                //自然完成播放
                if (param.args.length == 0) {
                    String replayStatus = Data.getReplay();
                    LogInfo(String.format("next：自然完成播放, isReplay：%s", replayStatus));
                    if (isShortSongs(classLoader)) {
                        LogInfo("next: 较短的曲目");
                        // 第一次启动时的shortSongs默认重放
                        switch (replayStatus) {
                            case REP_INIT: {
                                LogInfo("next: 首次启动");
                                Data.setReplay(REP_TRUE);
                                break;
                            }
                            case REP_FALSE: {
                                LogInfo("next：设置：下一曲预订再次播放");
                                Data.setReplay(REP_TRUE);
                                break;
                            }
                            case REP_REPLAYED: {
                                LogInfo("next：上一曲完成了MOD的自动重放");
                                break;
                            }
                            case REP_TRUE:
                            case REP_WILL_REPLAY:
                            default:
                                LogError("!!!! ERROR : 未知错误 replay : " + Data.getReplay());
                        }
                    }
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (!Data.getToggle()) {
                    return;
                }

                String replayStatus = Data.getReplay();
                if (param.args.length == 0) {
                    //自然完成播放
                    LogDebug(String.format("next(自然完成播放): 准备判断下一步的操作 : isReplay %s", replayStatus));
                    boolean isNeedReplay = false;
                    switch (replayStatus) {
                        case REP_TRUE: {
                            LogInfo("next：下一曲准备再次播放");
                            Data.setReplay(REP_WILL_REPLAY);
                            isNeedReplay = true;
                            break;
                        }
                        case REP_WILL_REPLAY:
                        case REP_FALSE:
                        case REP_INIT:
                        case REP_REPLAYED:
                        default: {
                            LogDebug("next：不需要操作，所以重置data");
                            Data.resetPlayingData();
                            break;
                        }
                    }

                    if (isNeedReplay) {
                        KeyPrevious();
                    } else {
                        KeyPlayPause();
                    }
                } else {
                    //所有next逻辑
                    LogDebug(String.format("next(监听所有next): 准备判断下一步的操作 : isReplay : %s", replayStatus));
                    if (replayStatus == REP_REPLAYED) {
                        LogInfo("不是通过自然播放完成 切换到下一曲，所以重置data");
                        Data.resetPlayingData();
                    }
                }
            }
        });
    }

    public void handler_playPrev(ClassLoader classLoader) throws Throwable {
        final Class c = classLoader.loadClass("com.netease.cloudmusic.service.PlayService");
        XposedBridge.hookAllMethods(c, "prev", new XC_MethodHook() {
            // HOOK的方法：
            // [所有prev逻辑都需要调用] public void prev(boolean z, boolean z2, @Nullable MusicEndConfig musicEndConfig)
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (!Data.getToggle()) {
                    return;
                }
                //判断是否是人为点击上一曲
                if (param.args.length != 3) {
                    return;
                }

                String replayStatus = Data.getReplay();
                LogDebug(String.format("prev: 准备判断MOD是否需要操作， isReplay : %s", replayStatus));
                switch (replayStatus) {
                    case REP_WILL_REPLAY: {
                        LogInfo("prev：记录为：已经执行了再次播放短曲");
                        Data.setReplay(REP_REPLAYED);
                        break;
                    }
                    case REP_REPLAYED:
                    case REP_FALSE:
                    case REP_INIT:
                    case REP_TRUE:
                    default: {
                        LogInfo("prev：不是MOD调用跳转到上一曲，所以重置data");
                        Data.resetPlayingData();
                    }
                }
            }
        });
    }
}
