package com.github.pengpan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;

public class MainActivity extends AppCompatActivity {

    /**
     * 记录用户首次点击返回键的时间
     */
    private long firstTime = 0;

    /**
     * 双击返回键退出程序
     */
    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            ToastUtils.showShort("再按一次退出程序");
            firstTime = secondTime;
        } else {
            ActivityUtils.finishAllActivities();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToastUtils.showLong("护眼睛灵来啦");
    }

}