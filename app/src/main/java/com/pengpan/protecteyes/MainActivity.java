package com.pengpan.protecteyes;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	Context context;
	private View view1, view2, view3, view4;
	private ImageView imageView_more;
	private SeekBar seekBar;
	private int alpha;
	private int red;
	private int green;
	private int blue;
	private BackService backService;
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			BackService.LocalService bind = (BackService.LocalService) service;
			backService = bind.getService();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SharedPreferences settings = getSharedPreferences("setting",
				MODE_PRIVATE);
		alpha = settings.getInt("alpha", alpha);
		red = settings.getInt("red", red);
		green = settings.getInt("green", green);
		blue = settings.getInt("blue", blue);
		view();
		seekBar();
		imageViewMore();
	}

	@Override
	protected void onStop() {
		super.onStop();
		createShortcut();
	}

	/**
	 * 创建桌面快捷方式
	 */
	private void createShortcut() {
		SharedPreferences setting = getSharedPreferences("silent.preferences",
				0);
		boolean firstStart = setting.getBoolean("FIRST_START", true);
		if (firstStart) {
			Intent shortcut = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					getString(R.string.app_name));
			shortcut.putExtra("duplicate", false);
			ComponentName comp = new ComponentName(this.getPackageName(), "."
					+ this.getLocalClassName());
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
					Intent.ACTION_MAIN).setComponent(comp));
			ShortcutIconResource iconRes = Intent.ShortcutIconResource
					.fromContext(this, R.drawable.icon);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
			sendBroadcast(shortcut);
			Editor editor = setting.edit();
			editor.putBoolean("FIRST_START", false);
			editor.commit();
			Toast.makeText(this, "成功创建快捷方式", Toast.LENGTH_SHORT).show();
		}
	}

	public void seekBar() {
		seekBar = (SeekBar) findViewById(R.id.seekbar);
		seekBar.setMax(220);
		seekBar.setProgress(alpha);
		// 开启护眼模式
		startEye();

		// seekBar的点击事件
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// 停止进度条保存数据
				SharedPreferences settings = getSharedPreferences("setting",
						MODE_PRIVATE);
				Editor edit = settings.edit();
				edit.putInt("alpha", alpha);
				edit.putInt("red", red);
				edit.putInt("green", green);
				edit.putInt("blue", blue);
				// 提交，保存
				edit.commit();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				alpha = progress;
				if (backService != null) {
					backService.changeColor(Color.argb(alpha, red, green, blue));

				}
			}
		});

	}

	/**
	 * 开启夜间模式
	 */
	public void startEye() {

		Intent intent = new Intent(MainActivity.this, BackService.class);
		intent.putExtra("color", Color.argb(alpha, red, green, blue));
		//开启服务
		startService(intent);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	public void view() {
		view1 = (View) findViewById(R.id.helpForCloseView_top);
		view2 = (View) findViewById(R.id.helpForCloseView_left);
		view3 = (View) findViewById(R.id.helpForCloseView_right);
		view4 = (View) findViewById(R.id.helpForCloseView_bottom);
		view1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.this.finish();
			}
		});
		view2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.this.finish();
			}
		});
		view3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.this.finish();
			}
		});
		view4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.this.finish();
			}
		});

	}

	/**
	 * 更多按钮监听事件
	 */
	private void imageViewMore() {
		imageView_more = (ImageView) findViewById(R.id.imageView_more);
		imageView_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SettingActivity(MainActivity.this, R.style.settingDialog,
						R.layout.setting).show();

			}
		});
	}

}
