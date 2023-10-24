package com.pengpan.protecteyes;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingActivity extends Dialog implements OnClickListener {
	private int layoutRes;
	Context context;

	LinearLayout linearLayout_boot_completed_start, linearLayout_check_update,
			linearLayout_good_way_for_protect_eyes, linearLayout_about,
			linearLayout_exit;

	public SettingActivity(Context context) {
		super(context);
		this.context = context;

	}

	public SettingActivity(Context context, int theme, int resLayout) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(layoutRes);

		linearLayout_boot_completed_start = (LinearLayout) findViewById(R.id.linearLayout_boot_completed_start);
		linearLayout_check_update = (LinearLayout) findViewById(R.id.linearLayout_check_update);
		linearLayout_good_way_for_protect_eyes = (LinearLayout) findViewById(R.id.linearLayout_good_way_for_protect_eyes);
		linearLayout_about = (LinearLayout) findViewById(R.id.linearLayout_about);
		linearLayout_exit = (LinearLayout) findViewById(R.id.linearLayout_exit);

		linearLayout_boot_completed_start.setOnClickListener(this);
		linearLayout_check_update.setOnClickListener(this);
		linearLayout_good_way_for_protect_eyes.setOnClickListener(this);
		linearLayout_about.setOnClickListener(this);
		linearLayout_exit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linearLayout_boot_completed_start:
			// 定时护眼
			new TimerActivity(context, R.style.settingDialog).show();
			break;
		case R.id.linearLayout_check_update:
			// 检查更新
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT)
							.show();
					Looper.loop();
				}
			}, 1000);

			break;
		case R.id.linearLayout_good_way_for_protect_eyes:
			// 评价
			Uri uri = Uri.parse("market://details?id="
					+ context.getPackageName());
			Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			try {
				context.startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(context, "无法启动应用市场！", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.linearLayout_about:
			// 关于
			new AboutActivity(context, R.style.settingDialog).show();
			break;
		case R.id.linearLayout_exit:
			// 关闭SettingActivity
			SettingActivity.this.cancel();
			// 清除notification
			// ((MainActivity) context).notificationCancel();
			// 关闭BackService
			context.stopService(new Intent(context, BackService.class));
			// 停止程序
			System.exit(0);
			break;
		}
	}
}
