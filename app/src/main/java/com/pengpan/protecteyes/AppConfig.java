package com.pengpan.protecteyes;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {
    private static AppConfig appConfig;

    private AppConfig() {
        super();
    }

    public static synchronized AppConfig getInstance() {
        if (appConfig == null) {
            appConfig = new AppConfig();
        }
        return appConfig;
    }

    public SharedPreferences getSharedPreferences() {
        SharedPreferences preferences = BaseApp.getInstance()
                .getSharedPreferences(Constants.SHARED_PREFERENCE_APP_CONFIG,
                        Context.MODE_PRIVATE);
        return preferences;
    }

    public int getBlueRay() {
        return getSharedPreferences().getInt(Constants.BLUE_RAY, 0);
    }

    public void setBlueRay(int blueRay) {
        getSharedPreferences().edit().putInt(Constants.BLUE_RAY, blueRay)
                .commit();
    }

    public int getBrightness() {
        return getSharedPreferences().getInt(Constants.BRIGHTNESS, 0);
    }

    public void setBrightness(int brightness) {
        getSharedPreferences().edit().putInt(Constants.BRIGHTNESS, brightness)
                .commit();
    }

    public boolean isAutoStart() {
        return getSharedPreferences().getBoolean(Constants.AUTO_START, true);
    }

    public void setAutoStart(boolean falg) {
        getSharedPreferences().edit().putBoolean(Constants.AUTO_START, falg)
                .commit();
    }

    public boolean isOpenRemind() {
        return getSharedPreferences().getBoolean(Constants.OPEN_REMIND, true);
    }

    public void setOpenRemind(boolean falg) {
        getSharedPreferences().edit().putBoolean(Constants.OPEN_REMIND, falg)
                .commit();
    }

    public boolean isFirstStart() {
        return getSharedPreferences().getBoolean(Constants.FIRST_START, true);
    }

    public void setFirstStart(boolean falg) {
        getSharedPreferences().edit().putBoolean(Constants.FIRST_START, falg)
                .commit();
    }

    public long getStartAt() {
        return getSharedPreferences().getLong(Constants.START_AT,
                System.currentTimeMillis());
    }

    public void setStartAt(long startAt) {
        getSharedPreferences().edit().putLong(Constants.START_AT, startAt)
                .commit();
    }

    public int getDurition() {
        return getSharedPreferences().getInt(Constants.DURITION, 60);
    }

    public void setDurition(int durition) {
        getSharedPreferences().edit().putInt(Constants.DURITION, durition)
                .commit();
    }
}
