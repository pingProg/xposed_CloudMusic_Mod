package com.ping.cloudmusicmod;

import static com.ping.cloudmusicmod.DataDB.insertOrUpdate;
import static com.ping.cloudmusicmod.DataDB.isKeyExist;
import static com.ping.cloudmusicmod.DataDB.queryValue;
import static com.ping.cloudmusicmod.Hook.LogInfo;

public class Data {
    public static final int REP_INIT = -1;
    public static final int REP_FALSE = 0;
    public static final int REP_TRUE = 1;
    public static final int REP_WILL_REPLAY = 2;
    public static final int REP_REPLAYED = 3;

    public static final String KEY_REPLAY = "replay";
    public static final String VALUE_REPLAY_INIT = Integer.toString(REP_INIT);
    public static final String KEY_TOGGLE = "toggle";
    public static final String VALUE_TOGGLE_INIT = Boolean.toString(true);

    public static void resetData() {
        LogInfo("重置所有数据");
        insertOrUpdate(KEY_REPLAY, VALUE_REPLAY_INIT);
        insertOrUpdate(KEY_TOGGLE, VALUE_TOGGLE_INIT);
    }

    public static int getReplay() {
        return Integer.parseInt(queryValue(KEY_REPLAY));
    }

    public static void setReplay(int replay) {
        insertOrUpdate(KEY_REPLAY, String.valueOf(replay));
    }

    public static boolean getToggle() {
        return Boolean.parseBoolean(queryValue(KEY_TOGGLE));
    }

    public static void setToggle(boolean toggle) {
        insertOrUpdate(KEY_TOGGLE, String.valueOf(toggle));
    }
}
