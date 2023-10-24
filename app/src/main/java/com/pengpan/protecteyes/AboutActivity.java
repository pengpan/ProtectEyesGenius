package com.pengpan.protecteyes;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class AboutActivity extends Dialog implements OnClickListener {

	Context context;
	LinearLayout back;

	public AboutActivity(Context context) {
		super(context);
		this.context = context;
	}

	public AboutActivity(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		back = (LinearLayout) findViewById(R.id.back);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.back) {
			AboutActivity.this.cancel();
		}
	}
}
