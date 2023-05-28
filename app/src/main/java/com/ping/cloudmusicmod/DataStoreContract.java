package com.ping.cloudmusicmod;

public class DataStoreContract {
    private String replay;
    public static final String KEY_REPLAY = "replay";
    public static final String REP_INIT = "REP_INIT";
    public static final String REP_FALSE = "REP_FALSE";
    public static final String REP_TRUE = "REP_TRUE";
    public static final String REP_WILL_REPLAY = "REP_WILL_REPLAY";
    public static final String REP_REPLAYED = "REP_REPLAYED";
    public static final String VALUE_INIT_REPLAY = REP_INIT;
    private String toggle;
    public static final String KEY_TOGGLE = "toggle";
    public static final String VALUE_INIT_TOGGLE = Boolean.toString(true);
    private String repTimes;
    public static final String KEY_REP_TIMES = "repTimes";
    public static final String VALUE_INIT_REP_TIMES = "0";



    public DataStoreContract(String replay, String toggle, String repTimes) {
        this.replay = replay;
        this.toggle = toggle;
        this.repTimes = repTimes;
    }

    public String getReplay() {
        return replay;
    }

    public void setReplay(String replay) {
        this.replay = replay;
    }

    public String getToggle() {
        return toggle;
    }

    public void setToggle(String toggle) {
        this.toggle = toggle;
    }

    public String getRepTimes() {
        return repTimes;
    }

    public void setRepTimes(String repTimes) {
        this.repTimes = repTimes;
    }
}
