package com.pengpan.protecteyes;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.pengpan.protecteyes.utils.AppUtil;
import com.pengpan.protecteyes.utils.ScreenUtils;
import com.pengpan.protecteyes.utils.ToastUtil;
import com.pengpan.protecteyes.utils.ViewHolder;

import java.util.List;

public class MainActivity extends Activity implements OnSeekBarChangeListener, OnClickListener {

    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_REGIST_REST_TIME_BR)) {
                int count = (int) ((System.currentTimeMillis() - AppConfig.getInstance().getStartAt()) / 60000);
                int targetCount = AppConfig.getInstance().getDurition();
                sbTime.setProgress(targetCount - count);
            }
        }
    };
    private Context mContext;
    private ImageView imageViewSet;
    private SeekBar sbTime;
    private SeekBar sbBrightness;
    private SeekBar sbBlueRay;
    private View viewTop, viewBottom, viewRight, viewLeft;
    private LinearLayout llTime;
    private BackService backService;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackService.LocalService bind = (BackService.LocalService) service;
            backService = bind.getService();
            setBlueRay(AppConfig.getInstance().getBlueRay());
            setBrightness(AppConfig.getInstance().getBrightness());
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        findViewAndSetListener();

        sbTime.setMax(180);
        sbBrightness.setMax(200);
        sbBlueRay.setMax(150);
        sbTime.setProgress(AppConfig.getInstance().getDurition());
        sbBrightness.setProgress(AppConfig.getInstance().getBrightness());
        sbBlueRay.setProgress(AppConfig.getInstance().getBlueRay());
        if (AppConfig.getInstance().isOpenRemind()) {
            AppConfig.getInstance().setStartAt(System.currentTimeMillis());
            AppConfig.getInstance().setDurition(sbTime.getProgress());
        }
        //授权dialog在视窗上弹出
        if (!Settings.canDrawOverlays(mContext)) {
            Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent1, 10);
        } else {
            Intent intent = new Intent(MainActivity.this, BackService.class);
            startService(intent);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConfig.getInstance().isOpenRemind()) {
            llTime.setVisibility(View.VISIBLE);
        } else {
            llTime.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_REGIST_REST_TIME_BR);
        registerReceiver(br, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (br != null) {
            unregisterReceiver(br);
        }
        if (conn != null) {
            unbindService(conn);
        }
    }

    private void findViewAndSetListener() {
        View root = findViewById(R.id.relativeLayout_main);
        imageViewSet = ViewHolder.get(root, R.id.imageView_more);
        sbTime = ViewHolder.get(root, R.id.seekbar_time);
        sbBrightness = ViewHolder.get(root, R.id.seekbar_brightness);
        sbBlueRay = ViewHolder.get(root, R.id.seekbar_blue_ray);
        viewBottom = ViewHolder.get(root, R.id.helpForCloseView_bottom);
        viewTop = ViewHolder.get(root, R.id.helpForCloseView_top);
        viewLeft = ViewHolder.get(root, R.id.helpForCloseView_left);
        viewRight = ViewHolder.get(root, R.id.helpForCloseView_right);
        llTime = ViewHolder.get(root, R.id.ll_time);

        imageViewSet.setOnClickListener(this);
        viewBottom.setOnClickListener(this);
        viewTop.setOnClickListener(this);
        viewLeft.setOnClickListener(this);
        viewRight.setOnClickListener(this);
        sbTime.setOnSeekBarChangeListener(this);
        sbBrightness.setOnSeekBarChangeListener(this);
        sbBlueRay.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        createShortCut();
    }

    public void createShortCut() {
        if (AppConfig.getInstance().isFirstStart()) {
            Intent shortCut = new Intent(
                    "com.android.launcher.action.INSTALL_SHORTCUT");
            shortCut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                    getString(R.string.app_name));
            shortCut.putExtra("duplicate", false);
            ComponentName comp = new ComponentName(this.getPackageName(), "."
                    + this.getLocalClassName());
            shortCut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
                    Intent.ACTION_MAIN).setComponent(comp));
            ShortcutIconResource iconRes = Intent.ShortcutIconResource
                    .fromContext(this, R.mipmap.ic_launcher);
            shortCut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
            sendBroadcast(shortCut);
            AppConfig.getInstance().setFirstStart(false);
            Toast.makeText(this, "成功创建快捷方式", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSetDialog() {
        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.layout_setting_dialog, null);
        final Dialog dialog = new Dialog(mContext,
                R.style.no_rounded_corner_dialog);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = 0;
        wl.width = (int) (ScreenUtils.getScreenW() * 0.8);
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);

        LinearLayout exit = ViewHolder.get(view, R.id.linearLayout_exit);
        LinearLayout update = ViewHolder.get(view, R.id.linearLayout_check_update);
        LinearLayout makeScore = ViewHolder.get(view,
                R.id.linearLayout_make_score);
        exit.setOnClickListener(this);
        makeScore.setOnClickListener(this);
        update.setOnClickListener(this);
        CheckBox cbOpenRemind = ViewHolder.get(view, R.id.cb_open_remind);
        cbOpenRemind.setChecked(AppConfig.getInstance().isOpenRemind());
        cbOpenRemind.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                AppConfig.getInstance().setOpenRemind(arg1);
                onResume();
            }
        });

        dialog.show();
    }


    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        switch (arg0.getId()) {
            case R.id.seekbar_time:

                break;
            case R.id.seekbar_brightness:
                if (backService != null) {
                    setBrightness(arg1);
                }
                break;
            case R.id.seekbar_blue_ray:
                if (backService != null) {
                    setBlueRay(arg1);
                }
                break;

            default:
                break;
        }
    }

    private void setBrightness(int alpha) {
        backService.changeBright(Color.argb(alpha, 0, 0, 0));
    }

    private void setBlueRay(int blueRay) {
        backService.changeBlueRay(Color.argb(blueRay * 2 < 50 ? blueRay * 2
                : 50, 255, 255, 255 - blueRay));
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        switch (arg0.getId()) {
            case R.id.seekbar_time:
                ToastUtil.showToast(this, arg0.getProgress() + "分钟后提醒");
                if (AppConfig.getInstance().isOpenRemind()) {
                    AppConfig.getInstance().setStartAt(System.currentTimeMillis());
                    AppConfig.getInstance().setDurition(sbTime.getProgress());
                }
                break;
            case R.id.seekbar_brightness:
                AppConfig.getInstance().setBrightness(arg0.getProgress());
                break;
            case R.id.seekbar_blue_ray:
                AppConfig.getInstance().setBlueRay(arg0.getProgress());
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView_more:
                showSetDialog();
                break;
            case R.id.linearLayout_check_update:
                AppUtil.checkUpdate(MainActivity.this);
                break;
            case R.id.linearLayout_exit:
                if (conn != null)
                    mContext.unbindService(conn);
                mContext.stopService(new Intent(MainActivity.this,
                        BackService.class));
                System.exit(0);
                break;
            case R.id.linearLayout_make_score:
                Uri uri = Uri.parse("market://details?id="
                        + MainActivity.this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    MainActivity.this.startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    ToastUtil.showToast(MainActivity.this, "无法启动应用市场");
                }
                break;
            case R.id.helpForCloseView_bottom:
                MainActivity.this.finish();
                break;
            case R.id.helpForCloseView_left:
                MainActivity.this.finish();
                break;
            case R.id.helpForCloseView_right:
                MainActivity.this.finish();
                break;
            case R.id.helpForCloseView_top:
                MainActivity.this.finish();
                break;

            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            // 不同意授权
            if (!Settings.canDrawOverlays(mContext)) {
                Toast.makeText(MainActivity.this, "not granted", Toast.LENGTH_SHORT);
                MainActivity.this.finish();
            }
        }
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

}
