package com.ping.cloudmusicmod.utils;

import static com.ping.cloudmusicmod.utils.CommonUtils.LogDebug;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogInfo;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.media.AudioManager;
import android.view.KeyEvent;

import java.lang.reflect.Field;

import de.robv.android.xposed.XposedHelpers;

public class PlayerUtils {
    private static final long maxShortSongsDuration_ms = 3 * 60 * 1000;

    public static void KeyPlayPause() throws Throwable {
        LogInfo("模拟点击：开始/暂停");
        Context context = (Context) AndroidAppHelper.currentApplication();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
    }

    public static void KeyPrevious() throws Throwable {
        LogInfo("模拟点击：上一曲");
        Context context = (Context) AndroidAppHelper.currentApplication();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
    }

    private static int getCurrentTime_ms(ClassLoader classLoader) {
        Class c = XposedHelpers.findClass("com.netease.cloudmusic.service.PlayService", classLoader);
        return (int) XposedHelpers.callStaticMethod(c, "getCurrentTime");
    }

    public static int getDuration_ms(ClassLoader classLoader) throws Throwable {
        // NOTE 示例：主动调用静态static函数
        Class c = XposedHelpers.findClass("com.netease.cloudmusic.service.PlayService", classLoader);
        Object returnObjectOfInvoke = XposedHelpers.callStaticMethod(c, "getPlayingMusicInfo");
        return (int) XposedHelpers.callMethod(returnObjectOfInvoke, "getDuration");
    }

    public static int getDuration_ms(Object playerClassObject) throws Throwable  {
        // NOTE 示例：主动调用object对象的成员函数
        Object musicInfo = XposedHelpers.callMethod(playerClassObject, "getCurrentMusic");
        return (int) XposedHelpers.callMethod(musicInfo, "getDuration");
    }

//    private boolean isPlayFinished() throws Throwable {
//        int delta_ms = 2 * 1000;
//        return Math.abs(getDuration_ms() - getCurrentTime_ms()) <= delta_ms;
//    }

//    private long getCurrentMusicID() throws Throwable {
//        Class c = XposedHelpers.findClass("com.netease.cloudmusic.service.PlayService", classLoader);
//        Object returnObjectOfInvoke = XposedHelpers.callStaticMethod(c, "getPlayingMusicInfo");
//        Field fieldOfPrivateVariable = returnObjectOfInvoke.getClass().getDeclaredField("id");
//        fieldOfPrivateVariable.setAccessible(true);
//        Object privateVariable = fieldOfPrivateVariable.get(returnObjectOfInvoke);
//        return (long) privateVariable;
//    }

    public static boolean isShortSongs(ClassLoader classLoader) throws Throwable {
        return getDuration_ms(classLoader) <= maxShortSongsDuration_ms;
    }
}
