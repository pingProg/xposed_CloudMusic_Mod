package com.ping.cloudmusicmod;

import static com.ping.cloudmusicmod.DataStoreContract.REP_FALSE;
import static com.ping.cloudmusicmod.DataStoreContract.REP_INIT;
import static com.ping.cloudmusicmod.DataStoreContract.REP_REPLAYED;
import static com.ping.cloudmusicmod.DataStoreContract.REP_TRUE;
import static com.ping.cloudmusicmod.DataStoreContract.REP_WILL_REPLAY;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogDebug;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogError;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogInfo;
import static com.ping.cloudmusicmod.utils.CommonUtils.getCurrentProcessName;
import static com.ping.cloudmusicmod.utils.CommonUtils.makeToastShortTime;
import static com.ping.cloudmusicmod.utils.CommonUtils.makeToastLongTime;
import static com.ping.cloudmusicmod.utils.PlayerUtils.isShortSongs;
import static com.ping.cloudmusicmod.utils.PlayerUtils.prev;
import static com.ping.cloudmusicmod.utils.PlayerUtils.stop;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.util.Objects;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    DataStoreApi data = null;
    boolean toggleAtUiProcess = false;
    boolean isHookedMainActivity = false;
    boolean isHookedPlayerService = false;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (!loadPackageParam.packageName.equals("com.netease.cloudmusic")) {
            return;
        }

        // NOTE 此处实现破解APP加固的HOOK，attach context后的就是没有加固的ClassLoader
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                Context context = (Context) param.args[0];
                ClassLoader classLoader = context.getClassLoader();
                String processName = getCurrentProcessName(context);
                LogInfo("#### 进程名称： " + processName);

                // NOTE
                // 问题：启动app时，创建一个进程，会触发两次handleLoadPackage(...)函数，所以会出现hook两次函数的问题
                // 解决：因为两次handleLoadPackage函数是在同一个进程中，所以局部变量共享，可以用局部变量记录是否已hook
                // 注意：不同进程的局部变量不共享
                if (Objects.equals(processName, "com.netease.cloudmusic") && !isHookedMainActivity) {
                    isHookedMainActivity = true;
                    LogInfo("#### HOOK 主进程（界面） : com.netease.cloudmusic");
                    // 初始化Data文件
                    data = DataStoreApi.getInstance();
                    data.resetForInit();
                    toggleAtUiProcess = true;

                    handler_printClickFunction(classLoader);
//                    handler_monitorPlayButton(classLoader);

                    handler_moduleToggle(classLoader);
                    handler_showDataInfo(classLoader);

                    handler_replayCountIncrease(classLoader);
                    handler_replayCountDecrease(classLoader);
                    handler_lockScreen_replayCount(classLoader);

                    handler_specialButtonClick(classLoader);
                }

                if (Objects.equals(processName, "com.netease.cloudmusic:play") && !isHookedPlayerService) {
                    isHookedPlayerService = true;
                    LogInfo("#### HOOK 播放服务service : com.netease.cloudmusic:play");
                    data = DataStoreApi.getInstance();
                    handler_playNext(classLoader);
                    handler_playPrev(classLoader);
                }
            }
        });
    }

    private boolean checkToggleForUi() {
        if (!toggleAtUiProcess && data.getToggle()) {
            makeToastShortTime("ERROR : toggle值不一致，需要debug");
            return false;
        }
        return toggleAtUiProcess;
    }

    public void handler_printClickFunction(ClassLoader classLoader) {
        Class<?> c = XposedHelpers.findClass("android.view.View", classLoader);
        XposedBridge.hookAllMethods(c, "performClick", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (!checkToggleForUi()) {
                    return;
                }

                Object listenerInfoObject = XposedHelpers.getObjectField(param.thisObject, "mListenerInfo");
                Object mOnClickListenerObject = XposedHelpers.getObjectField(listenerInfoObject, "mOnClickListener");
                String callbackType = mOnClickListenerObject.getClass().getName();
                LogDebug("---- ---- ---- ---- CLICK FUNCTION : " + callbackType);
            }
        });
    }

    @SuppressWarnings("commented-out-code")
    //    public void handler_monitorPlayButton(ClassLoader classLoader) {
    //        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.activity.w4", classLoader, "onClick", android.view.View.class, new XC_MethodHook() {
    //            @Override
    //            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
    //                super.beforeHookedMethod(param);
    //            }
    //
    //            @Override
    //            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    //                super.afterHookedMethod(param);
    //                LogDebug("---- ---- PLAY BUTTON");
    //            }
    //        });
    //    }

    public void handler_moduleToggle(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.module.hint.view.PlayerShareView$e", classLoader, "onClick", android.view.View.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) {
                data.setToggle(!data.getToggle());
                toggleAtUiProcess = !toggleAtUiProcess;

                data.resetPlayingDataToInit();

                makeToastShortTime(String.format("xposed MOD功能：%s", data.getToggle() ? "开启" : "关闭"));

                return null;
            }
        });
    }

    public void handler_showDataInfo(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("androidx.appcompat.widget.ToolbarWidgetWrapper$1", classLoader, "onClick", android.view.View.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) {
                boolean toggle = data.getToggle();
                @SuppressLint("DefaultLocale") String msg = toggle ? String.format("MOD：开，replay：%s，replayTimes：%d", data.getReplay(), data.getRepTimes()) : "MOD：关";
                makeToastLongTime(msg);
                return null;
            }
        });
    }

    public void handler_replayCountIncrease(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.activity.PlayerActivity$h", classLoader, "onClick", android.view.View.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) {
                if (!checkToggleForUi()) {
                    return null;
                }

                int repTimes = data.getRepTimes() + 1;
                data.setRepTimes(repTimes);
                makeToastShortTime("重播次数+1 ：" + repTimes);
                return null;
            }
        });
    }

    public void handler_replayCountDecrease(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.activity.u4", classLoader, "onClick", android.view.View.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) {
                if (!checkToggleForUi()) {
                    return null;
                }

                int repTimes = data.getRepTimes() - 1;
                if (repTimes < 0) {
                    data.setRepTimes(0);
                    makeToastShortTime("重播次数-1 ：0");
                } else {
                    data.setRepTimes(repTimes);
                    makeToastShortTime("重播次数-1 ：" + repTimes);
                }
                return null;
            }
        });
    }

    public void handler_lockScreen_replayCount(ClassLoader classLoader) {
        // 替换函数：锁屏界面的“喜欢”按钮的函数
        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.activity.LockScreenActivity$i", classLoader, "onClick", android.view.View.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) {
                if (!checkToggleForUi()) {
                    return null;
                }

                int repTimes = data.getRepTimes() + 1;
                data.setRepTimes(repTimes);
                makeToastShortTime("重播次数+1 ：" + repTimes);
                return null;
            }
        });
    }

    public void handler_specialButtonClick(ClassLoader classLoader) {
        handler_musicListItemClick(classLoader);
        handler_historyListItemClick(classLoader);
        handler_playAllButtonCLick(classLoader);
    }

    public void handler_musicListItemClick(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.ui.component.songitem.DefaultMusicListHostImpl$1", classLoader, "onClick", android.view.View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (!checkToggleForUi()) {
                    return;
                }

                LogInfo("点击常规列表，reset");
                data.resetPlayingData();
            }
        });
    }

    public void handler_historyListItemClick(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.module.playerlisthistory.m3", classLoader, "onClick", android.view.View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (!checkToggleForUi()) {
                    return;
                }

                LogInfo("点击右下角列表，reset");
                data.resetPlayingData();
            }
        });
    }

    public void handler_playAllButtonCLick(ClassLoader classLoader) {
        XposedHelpers.findAndHookConstructor("com.netease.cloudmusic.fragment.PlayListFragment", classLoader, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (!checkToggleForUi()) {
                    return;
                }

                makeToastShortTime("测试：点击全部播放按钮，reset");
                LogInfo("点击全部播放按钮，reset");
                data.resetPlayingData();
            }
        });
    }

    public void handler_playNext(ClassLoader classLoader) {
        // HOOK的方法：
        // [只在播放自然完成时调用] public void next()
        // [所有next逻辑都需要调用] public void next(boolean z, boolean z2, @Nullable MusicEndConfig musicEndConfig)
        // public void next()也会调用一次public void next(boolean z, boolean z2, @Nullable MusicEndConfig musicEndConfig)，
        // 所以需要按照参数个数区分开来
        Class<?> c;
        try {
            c = classLoader.loadClass("com.netease.cloudmusic.service.PlayService");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        XposedBridge.hookAllMethods(c, "next", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (!data.getToggle()) {
                    return;
                }

                //自然完成播放
                if (param.args.length == 0) {
                    String replayStatus = data.getReplay();
                    LogInfo(String.format("next：自然完成播放, isReplay：%s", replayStatus));
                    int repTimes = data.getRepTimes();
                    if (repTimes > 0) {                         // 判断replayTimes的逻辑
                        LogInfo("next: 剩余重播次数：" + repTimes);
                        data.setReplay(REP_TRUE);
                        data.setRepTimes(repTimes - 1);
                    } else if (isShortSongs(classLoader)) {     // 判断短曲replay的逻辑
                        LogInfo("next: 较短的曲目");
                        // 第一次启动时的shortSongs默认重放
                        switch (replayStatus) {
                            case REP_INIT: {
                                LogInfo("next: 首次启动");
                                data.setReplay(REP_TRUE);
                                break;
                            }
                            case REP_FALSE: {
                                LogInfo("next：设置：下一曲预订再次播放");
                                data.setReplay(REP_TRUE);
                                break;
                            }
                            case REP_REPLAYED: {
                                LogInfo("next：上一曲完成了MOD的自动重放");
                                break;
                            }
                            case REP_TRUE:
                            case REP_WILL_REPLAY:
                            default:
                                LogError("!!!! ERROR : 未知错误 replay : " + data.getReplay(), null);
                        }
                    }
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (!data.getToggle()) {
                    return;
                }

                String replayStatus = data.getReplay();
                if (param.args.length == 0) {
                    //自然完成播放
                    LogDebug(String.format("next(自然完成播放): 准备判断下一步的操作 : isReplay %s", replayStatus));
                    boolean isNeedReplay = false;
                    switch (replayStatus) {
                        case REP_TRUE: {
                            LogInfo("next：下一曲准备再次播放");
                            data.setReplay(REP_WILL_REPLAY);
                            isNeedReplay = true;
                            break;
                        }
                        case REP_WILL_REPLAY:
                        case REP_FALSE:
                        case REP_INIT:
                        case REP_REPLAYED:
                        default: {
                            LogDebug("next：不需要操作，所以重置data");
                            data.resetPlayingData();
                            break;
                        }
                    }

                    if (isNeedReplay) {
                        prev(param.thisObject);
                    } else {
//                        pause(param.thisObject);
                        stop(param.thisObject);
                    }
                } else {
                    //所有next逻辑
                    LogDebug(String.format("next(监听所有next): 准备判断下一步的操作 : isReplay : %s", replayStatus));
                    if (replayStatus.equals(REP_REPLAYED)) {
                        LogInfo("不是通过自然播放完成 切换到下一曲，所以重置data");
                        data.resetPlayingData();
                    }
                }
            }
        });
    }

    public void handler_playPrev(ClassLoader classLoader) {
        Class<?> c;
        try {
            c = classLoader.loadClass("com.netease.cloudmusic.service.PlayService");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        XposedBridge.hookAllMethods(c, "prev", new XC_MethodHook() {
            // HOOK的方法：
            // [所有prev逻辑都需要调用] public void prev(boolean z, boolean z2, @Nullable MusicEndConfig musicEndConfig)
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (!data.getToggle()) {
                    return;
                }
                //判断是否是人为点击上一曲
                if (param.args.length != 3) {
                    return;
                }

                String replayStatus = data.getReplay();
                LogDebug(String.format("prev: 准备判断MOD是否需要操作， isReplay : %s", replayStatus));
                switch (replayStatus) {
                    case REP_WILL_REPLAY: {
                        LogInfo("prev：记录为：已经执行了再次播放短曲");
                        data.setReplay(REP_REPLAYED);
                        break;
                    }
                    case REP_REPLAYED:
                    case REP_FALSE:
                    case REP_INIT:
                    case REP_TRUE:
                    default: {
                        LogInfo("prev：不是MOD调用跳转到上一曲，所以重置data");
                        data.resetPlayingData();
                    }
                }
            }
        });
    }
}
