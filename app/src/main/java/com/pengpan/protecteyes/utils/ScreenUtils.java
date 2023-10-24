package com.pengpan.protecteyes.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import com.pengpan.protecteyes.BaseApp;

public class ScreenUtils {
    private static int screenW;
    private static int screenH;
    private static float screenDensity;

    static {
        DisplayMetrics metric = BaseApp.getInstance().getResources()
                .getDisplayMetrics();
        screenW = metric.widthPixels;
        screenH = metric.heightPixels;
        screenDensity = metric.density;
    }

    public static int getScreenW() {
        return screenW;
    }

    public static int getScreenH() {
        return screenH;
    }

    public static float getScreenDensity() {
        return screenDensity;
    }

    public static int dp2px(float dpValue) {
        return (int) (dpValue * getScreenDensity() + 0.5f);
    }

    public static int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }

    /**
     * 截屏，只截取中间部分，去掉了statusBar和smartBar
     *
     * @param activity
     * @return
     */
    public static Bitmap takeScreenShotPure(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmapFull = view.getDrawingCache();

        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        Bitmap bitmap = Bitmap.createBitmap(bitmapFull, 0, frame.top, screenW,
                frame.bottom - frame.top);
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 截屏，全屏截取
     *
     * @param activity
     * @return
     */
    public static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmapFull = view.getDrawingCache();
        return bitmapFull;
    }

}
