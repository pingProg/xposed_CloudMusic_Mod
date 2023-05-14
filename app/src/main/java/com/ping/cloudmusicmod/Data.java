package com.ping.cloudmusicmod;

import static com.ping.cloudmusicmod.DataDB.insertOrUpdate;
import static com.ping.cloudmusicmod.DataDB.queryValue;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogInfo;


public class Data {
    public static final String REP_INIT = "REP_INIT";
    public static final String REP_FALSE = "REP_FALSE";
    public static final String REP_TRUE = "REP_TRUE";
    public static final String REP_WILL_REPLAY = "REP_WILL_REPLAY";
    public static final String REP_REPLAYED = "REP_REPLAYED";

    public static final String KEY_REPLAY = "replay";
    public static final String VALUE_REPLAY_INIT = REP_INIT;
    public static final String KEY_TOGGLE = "toggle";
    public static final String VALUE_TOGGLE_INIT = Boolean.toString(true);

    public static void resetAllData() {
        LogInfo("重置所有数据");
        insertOrUpdate(KEY_REPLAY, VALUE_REPLAY_INIT);
        insertOrUpdate(KEY_TOGGLE, VALUE_TOGGLE_INIT);
    }

    public static void resetPlayingData() {
        LogInfo("重置播放数据");
        insertOrUpdate(KEY_REPLAY, REP_FALSE);
    }

    public static String getReplay() {
        return queryValue(KEY_REPLAY);
    }

    public static void setReplay(String replay) {
        insertOrUpdate(KEY_REPLAY, String.valueOf(replay));
    }

    public static boolean getToggle() {
        return Boolean.parseBoolean(queryValue(KEY_TOGGLE));
    }

    public static void setToggle(boolean toggle) {
        insertOrUpdate(KEY_TOGGLE, String.valueOf(toggle));
    }
}
