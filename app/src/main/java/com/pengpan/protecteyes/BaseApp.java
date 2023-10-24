package com.pengpan.protecteyes;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

public class BaseApp extends Application {
    private static BaseApp mInstance;
    private List<Activity> activityList = new LinkedList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    public static synchronized BaseApp getInstance() {
        return mInstance;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : activityList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
