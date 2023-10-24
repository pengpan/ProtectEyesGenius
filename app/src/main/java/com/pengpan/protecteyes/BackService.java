package com.pengpan.protecteyes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

public class BackService extends Service {

	private NotificationManager notificationManager;
	private IBinder binder = new LocalService();
	WindowManager.LayoutParams wmParams;
	WindowManager mWindowManager;
	private LinearLayout mFloatLayout;
	Context mContext;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		createFloatView();
		startNotification();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mFloatLayout
				.setBackgroundColor(intent.getIntExtra("color", 0xffffffff));
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		mWindowManager.removeView(mFloatLayout);
		super.onDestroy();
	}

	private void startNotification() {
		// 开启通知栏
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.icon_notif,
				"护眼精灵已启用", System.currentTimeMillis());
		RemoteViews remoteViews = new RemoteViews(getPackageName(),
				R.layout.setting_notification_layout);
		remoteViews.setTextViewText(R.id.textvView_click_enable, "点击设置");
		remoteViews.setImageViewResource(R.id.app_icon, R.drawable.icon);

		// set的点击监听
		Intent set = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent
				.getActivity(this, 0, set, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageView_set, pendingIntent);

		notification.contentIntent = pendingIntent;

		notification.contentView = remoteViews;
		// 不能手动清理
		notification.flags = Notification.FLAG_NO_CLEAR;
		// 把定义的notification 传递给 notificationManager
		notificationManager.notify(1, notification);
		startForeground(1, notification);
	}

	private void createFloatView() {
		mWindowManager = (WindowManager) getApplication().getSystemService(
				Context.WINDOW_SERVICE);
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = 2006;
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
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout,
				null);
		mWindowManager.addView(mFloatLayout, wmParams);
	}

	public void changeColor(int color) {
		mFloatLayout.setBackgroundColor(color);
	}

	public class LocalService extends Binder {
		public BackService getService() {
			return BackService.this;
		}
	}


}
