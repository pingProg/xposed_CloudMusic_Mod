package com.ping.cloudmusicmod;

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
        DataStoreContract d = new DataStoreContract(DataStoreContract.VALUE_INIT_REPLAY, DataStoreContract.VALUE_INIT_TOGGLE);
        api.writeJson(new Gson().toJson(d));
        LogInfo("Data初始化");
    }

    public void resetPlayingData() {
        api.set(DataStoreContract.KEY_REPLAY, DataStoreContract.REP_FALSE);
    }

    public void resetPlayingDataToInit() {
        api.set(DataStoreContract.KEY_REPLAY, DataStoreContract.VALUE_INIT_REPLAY);
    }

    public String getReplay() {
        return get(DataStoreContract.KEY_REPLAY);
    }

    public void setReplay(String replay) {
        set(DataStoreContract.KEY_REPLAY, replay);
    }

    public boolean getToggle() {
        return Boolean.parseBoolean(get(DataStoreContract.KEY_TOGGLE));
    }

    public void setToggle(boolean toggle) {
        set(DataStoreContract.KEY_TOGGLE, String.valueOf(toggle));
    }

    public String get(String key) {
        return api.get(key);
    }

    public void set(String key, String value) {
        api.set(key, value);
    }

    public void debugPrint() {
        LogDebug(api.readJson());
    }
}
