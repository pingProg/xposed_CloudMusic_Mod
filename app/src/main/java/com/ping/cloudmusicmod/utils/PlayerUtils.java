package com.ping.cloudmusicmod.utils;

import static com.ping.cloudmusicmod.utils.CommonUtils.LogInfo;

import java.lang.reflect.Field;

import de.robv.android.xposed.XposedHelpers;

public class PlayerUtils {
    private static final long maxShortSongsDuration_ms = 3 * 60 * 1000;

    public static void stop(Object playerObject) {
        LogInfo("主动调用player的stop停止");
        XposedHelpers.callMethod(playerObject, "stop");
    }

    public static void prev(Object playerObject) {
        LogInfo("主动调用player的prev上一曲");
        XposedHelpers.callMethod(playerObject, "prev");
    }

    @SuppressWarnings("unused")
    private static int getCurrentTime_ms(ClassLoader classLoader) {
        Class<?> c = XposedHelpers.findClass("com.netease.cloudmusic.service.PlayService", classLoader);
        return (int) XposedHelpers.callStaticMethod(c, "getCurrentTime");
    }

    public static int getDuration_ms(ClassLoader classLoader) {
        // NOTE 示例：主动调用静态static函数
        Class<?> c = XposedHelpers.findClass("com.netease.cloudmusic.service.PlayService", classLoader);
        Object returnObjectOfInvoke = XposedHelpers.callStaticMethod(c, "getPlayingMusicInfo");
        return (int) XposedHelpers.callMethod(returnObjectOfInvoke, "getDuration");
    }

    @SuppressWarnings("unused")
    public static int getDuration_ms(Object playerObject) {
        // NOTE 示例：主动调用object对象的成员函数
        Object musicInfo = XposedHelpers.callMethod(playerObject, "getCurrentMusic");
        return (int) XposedHelpers.callMethod(musicInfo, "getDuration");
    }

    @SuppressWarnings("unused")
    private long getCurrentMusicID(ClassLoader classLoader) throws Throwable {
        Class<?> c = XposedHelpers.findClass("com.netease.cloudmusic.service.PlayService", classLoader);
        Object returnObjectOfInvoke = XposedHelpers.callStaticMethod(c, "getPlayingMusicInfo");
        Field fieldOfPrivateVariable = returnObjectOfInvoke.getClass().getDeclaredField("id");
        fieldOfPrivateVariable.setAccessible(true);
        Object privateVariable = fieldOfPrivateVariable.get(returnObjectOfInvoke);
        return privateVariable == null ? 0 : (long) privateVariable;
    }

    public static boolean isShortSongs(ClassLoader classLoader) {
        return getDuration_ms(classLoader) <= maxShortSongsDuration_ms;
    }
}
