package com.ping.cloudmusicmod;

import static com.ping.cloudmusicmod.DataStoreContract.KEY_REPLAY;
import static com.ping.cloudmusicmod.DataStoreContract.KEY_REP_TIMES;
import static com.ping.cloudmusicmod.DataStoreContract.KEY_TOGGLE;
import static com.ping.cloudmusicmod.DataStoreContract.REP_FALSE;
import static com.ping.cloudmusicmod.DataStoreContract.VALUE_INIT_REPLAY;
import static com.ping.cloudmusicmod.DataStoreContract.VALUE_INIT_REP_TIMES;
import static com.ping.cloudmusicmod.DataStoreContract.VALUE_INIT_TOGGLE;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogDebug;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogInfo;

import com.google.gson.Gson;

public class DataStoreApi {
    private static com.ping.cloudmusicmod.DataStoreApi instance;
    private static final String jsonFileName = "modData";
    private final JsonApi api;

    // 私有构造方法，防止外部实例化
    private DataStoreApi() {
        api = new JsonApi(jsonFileName);
    }

    // 提供静态方法返回实例
    public static synchronized DataStoreApi getInstance() {
        if (instance == null) {
            instance = new DataStoreApi();
        }
        return instance;
    }

    public void resetForInit() {
        DataStoreContract d = new DataStoreContract(VALUE_INIT_REPLAY, VALUE_INIT_TOGGLE, VALUE_INIT_REP_TIMES);
        api.writeJson(new Gson().toJson(d));
        LogInfo("Data初始化");
    }

    public void resetPlayingData() {
        api.set(KEY_REPLAY, REP_FALSE);
        api.set(KEY_REP_TIMES, VALUE_INIT_REP_TIMES);
        LogInfo("Data重置");
    }

    public void resetPlayingDataToInit() {
        setReplay(VALUE_INIT_REPLAY);
        setRepTimes(Integer.parseInt(VALUE_INIT_REP_TIMES));
    }

    public String getReplay() {
        return get(KEY_REPLAY);
    }

    public void setReplay(String replay) {
        set(KEY_REPLAY, replay);
    }

    public boolean getToggle() {
        return Boolean.parseBoolean(get(KEY_TOGGLE));
    }

    public void setToggle(boolean toggle) {
        set(KEY_TOGGLE, String.valueOf(toggle));
    }

    public int getRepTimes() {
        return Integer.parseInt(get(KEY_REP_TIMES));
    }

    private void setRepTimes(int repTimes) {
        set(KEY_REP_TIMES, String.valueOf(repTimes));
    }
    public void increaseRepTimes() {
        setRepTimes(getRepTimes() + 1);
        LogInfo("repTimes : + 1");
    }
    public void decreaseRepTimes() {
        setRepTimes(getRepTimes() - 1);
        LogInfo("repTimes : - 1");
    }
    public void setRepTimesToZero() { setRepTimes(0); }
    @SuppressWarnings("unused")
    public void setRepTimesTo1() { setRepTimes(1); }

    private String get(String key) {
        return api.get(key);
    }

    private void set(String key, String value) {
        api.set(key, value);
    }

    @SuppressWarnings("unused")
    public void debugPrint() {
        LogDebug(api.readJson());
    }
}
