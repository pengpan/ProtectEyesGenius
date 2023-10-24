package com.pengpan.protecteyes.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;
import java.util.UUID;

public class AppUtil {
    public static final String CHECK_UPDATE_URL = "http://iscs.hebut.edu.cn/icons/share-images/images/publicResources/AppUpdate.xml";

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int currentVersionCode(Context context) {
        try {
            String packageName = context.getPackageName();
            int version = context.getPackageManager().getPackageInfo(
                    packageName, 0).versionCode;
            return version;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String currentVersionName(Context context) {
        try {
            String packageName = context.getPackageName();
            String version = context.getPackageManager().getPackageInfo(
                    packageName, 0).versionName;
            return version;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 判断是否安装了指定包名的应用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static final boolean checkAppInstalled(Context context,
                                                  String packageName) {
        PackageManager pkgMgr = context.getPackageManager();
        if (pkgMgr != null) {
            List<PackageInfo> pkgInfos = pkgMgr
                    .getInstalledPackages(PackageManager.GET_PERMISSIONS);
            for (PackageInfo pkgInfo : pkgInfos) {
                if (pkgInfo.packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据包名获取手机上对应程序的版本号
     *
     * @param context
     * @param packageName
     * @return
     */
    public static int getVersionCodeByPackageName(Context context,
                                                  String packageName) {
        PackageManager pkgMgr = context.getPackageManager();
        if (pkgMgr != null) {
            List<PackageInfo> pkgInfos = pkgMgr
                    .getInstalledPackages(PackageManager.GET_PERMISSIONS);
            for (PackageInfo pkgInfo : pkgInfos) {
                if (pkgInfo.packageName.equals(packageName)) {
                    return pkgInfo.versionCode;
                }
            }
        }
        return -1;
    }

    /**
     * 异步检查更新
     *
     * @param context
     * @param context
     */
    public static void checkUpdate(Context context) {
        String packageName = context.getPackageName();
        int versionCode = getVersionCodeByPackageName(context, packageName);
        try {
            new AsyncCheckUpdate(context, CHECK_UPDATE_URL, packageName,
                    versionCode).execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步下载更新
     *
     * @param context
     * @param app
     */
    public static void downloadApp(Context context, AppUpdate app) {
        String filePath = UUID.randomUUID() + ".apk";
        new AsyncDownloadApp(context).execute(app._url, filePath,
                Long.valueOf(app._size).toString());

    }
}
