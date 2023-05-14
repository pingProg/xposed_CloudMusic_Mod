package com.ping.cloudmusicmod.utils;

import static com.ping.cloudmusicmod.utils.CommonUtils.LogDebug;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.media.AudioManager;
import android.view.KeyEvent;

import java.lang.reflect.Field;

import de.robv.android.xposed.XposedHelpers;

public class PlayerUtils {
    private static final long maxShortSongsDuration_ms = 3 * 60 * 1000;

    private static void KeyPlayPause() throws Throwable {
        LogDebug("%%%% KeyPlayPause");
        Context context = (Context) AndroidAppHelper.currentApplication();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
    }

    private static void KeyPrevious() throws Throwable {
        LogDebug("%%%% KeyPrevious");
        Context context = (Context) AndroidAppHelper.currentApplication();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
    }

    private static int getCurrentTime_ms(ClassLoader classLoader) {
        Class c = XposedHelpers.findClass("com.netease.cloudmusic.service.PlayService", classLoader);
        return (int) XposedHelpers.callStaticMethod(c, "getCurrentTime");
    }

    private int getDuration_ms(ClassLoader classLoader) throws Throwable {
        // NOTE : 使用反射，获取java的私有成员变量
        Class c = XposedHelpers.findClass("com.netease.cloudmusic.service.PlayService", classLoader);
        Object returnObjectOfInvoke = XposedHelpers.callStaticMethod(c, "getPlayingMusicInfo");
        Field fieldOfPrivateVariable = returnObjectOfInvoke.getClass().getDeclaredField("duration");
        fieldOfPrivateVariable.setAccessible(true);
        Object privateVariable = fieldOfPrivateVariable.get(returnObjectOfInvoke);
        return (int) privateVariable;
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

    private boolean isShortSongs(ClassLoader classLoader) throws Throwable {
        return getDuration_ms(classLoader) <= maxShortSongsDuration_ms;
    }
}
