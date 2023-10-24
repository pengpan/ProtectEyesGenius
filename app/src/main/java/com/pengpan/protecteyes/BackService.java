package com.pengpan.protecteyes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

public class BackService extends Service {

    private NotificationManager notificationManager;
    private Timer mTimer;
    private IBinder binder = new LocalService();
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    private LinearLayout brightLayout;
    private LinearLayout blueRayLayout;
    Context mContext;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        createFloatView();
        notifier("护眼精灵已启动", "点击设置");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (AppConfig.getInstance().isOpenRemind()) {
            startTimer();
        }
        return START_NOT_STICKY;
    }

    public void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    if (AppConfig.getInstance().isOpenRemind()) {
                        countDown();
                    }
                }
            }, 0L, 1000L);
        }
    }

    private void countDown() {
        long count = (System.currentTimeMillis() - AppConfig.getInstance()
                .getStartAt()) / 1000;
        long targetCount = AppConfig.getInstance().getDurition() * 60;
        if (count < targetCount) {
            sendBroadcast(new Intent(Constants.ACTION_REGIST_REST_TIME_BR));
        } else if (count == targetCount) {
            notifier("护眼提醒", String.format(
                    "用户您好，您已经连续用眼%s分钟，建议休息一会儿，以避免眼睛过度疲劳影响视力！", AppConfig
                            .getInstance().getDurition()));
        }
    }

    private void notifier(String tickerText, String content) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        notification.icon = R.mipmap.ic_launcher;
        notification.tickerText = tickerText;
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.textvView_click_enable, content);
        remoteViews.setImageViewResource(R.id.app_icon, R.mipmap.ic_launcher);

        Intent set = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, set, 0);
        remoteViews.setOnClickPendingIntent(R.id.imageView_set, pendingIntent);
        notification.contentIntent = pendingIntent;
        notification.contentView = remoteViews;
        notification.flags = Notification.FLAG_NO_CLEAR;
        notificationManager.notify(R.mipmap.ic_launcher, notification);
        notificationManager.cancel(R.mipmap.ic_launcher);
        startForeground(1, notification);
    }

    private void createFloatView() {
        mWindowManager = (WindowManager) getApplication().getSystemService(
                Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_NOT_TOUCHABLE
                | LayoutParams.FLAG_FULLSCREEN
                | LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        brightLayout = (LinearLayout) inflater.inflate(
                R.layout.layout_screen_cover, null);
        blueRayLayout = (LinearLayout) inflater.inflate(
                R.layout.layout_screen_cover, null);
        mWindowManager.addView(brightLayout, wmParams);
        mWindowManager.addView(blueRayLayout, wmParams);
    }

    public void changeBright(int color) {
        brightLayout.setBackgroundColor(color);
    }

    public void changeBlueRay(int color) {
        blueRayLayout.setBackgroundColor(color);
    }

    public class LocalService extends Binder {
        public BackService getService() {
            return BackService.this;
        }
    }

}
