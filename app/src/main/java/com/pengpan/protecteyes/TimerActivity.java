package com.pengpan.protecteyes;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

public class TimerActivity extends Dialog implements OnClickListener {

	int time = 0;
	Thread thread = null;
	Button btn_ok;
	RadioButton rb1, rb2, rb3, rb4, rb5;
	Context context;
	LinearLayout back;
	final Calendar calendar = Calendar.getInstance();

	public TimerActivity(Context context) {
		super(context);
		this.context = context;
	}

	public TimerActivity(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timer);
		init();
	}

	private void init() {
		back = (LinearLayout) findViewById(R.id.back);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		rb1 = (RadioButton) findViewById(R.id.rb1);
		rb2 = (RadioButton) findViewById(R.id.rb2);
		rb3 = (RadioButton) findViewById(R.id.rb3);
		rb4 = (RadioButton) findViewById(R.id.rb4);
		rb5 = (RadioButton) findViewById(R.id.rb5);
		back.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		rb1.setOnClickListener(this);
		rb2.setOnClickListener(this);
		rb3.setOnClickListener(this);
		rb4.setOnClickListener(this);
		rb5.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			TimerActivity.this.cancel();
			break;
		case R.id.btn_ok:
			thread = new Thread() {
				public void run() {
					try {
						Thread.sleep(time * 60 * 60 * 1000);
						// Thread.sleep(time * 10 * 1000);
						context.stopService(new Intent(context,
								BackService.class));
						Looper.prepare();
						Toast.makeText(context, "时间到---护眼精灵已关闭!",
								Toast.LENGTH_SHORT).show();
						Looper.loop();
						System.exit(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			};
			thread.start();
			TimerActivity.this.cancel();
			Toast.makeText(context, "护眼精灵将在" + time + "小时后关闭!",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.rb1:
			time = 1;
			break;
		case R.id.rb2:
			time = 2;
			break;
		case R.id.rb3:
			time = 3;
			break;
		case R.id.rb4:
			time = 4;
			break;
		case R.id.rb5:
			time = 5;
			break;
		}
	}

}
