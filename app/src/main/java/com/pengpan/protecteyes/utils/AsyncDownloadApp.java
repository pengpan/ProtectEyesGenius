package com.pengpan.protecteyes.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static android.content.DialogInterface.OnClickListener;

/**
 * 下载并安装更新
 *
 * @author Tailyou0506
 */
public class AsyncDownloadApp extends AsyncTask<String, Long, File> {
    private final AtomicLong length = new AtomicLong(0L);
    private ProgressDialog downloadDialog = null;
    private final Context context;
    private AtomicBoolean isContinue = new AtomicBoolean(false);

    public AsyncDownloadApp(Context context) {
        super();
        this.context = context;
        isContinue.getAndSet(true);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPreExecute() {
        if (Looper.myLooper() == null)
            Looper.prepare();
        downloadDialog = new ProgressDialog(context);
        downloadDialog.setTitle("提示");
        downloadDialog.setMessage("正在下载...");
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setIcon(android.R.drawable.ic_dialog_info);
        downloadDialog.setMax(100);
        downloadDialog.setProgress(0);
        downloadDialog.setButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                isContinue.getAndSet(false);
                downloadDialog.dismiss();
            }
        });
        downloadDialog.show();

    }

    @SuppressWarnings("deprecation")
    @Override
    protected File doInBackground(String... params) {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            String fileUrl = params[0];
            String fileName = params[1];
            long len = Long.valueOf(params[2]);

            String targetFilePath = context.getFilesDir() + File.separator
                    + fileName;
            File file = new File(targetFilePath);
            if (file.exists())
                file.delete();
            file.createNewFile();
            url = new URL(fileUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            length.getAndSet(len);
            is = conn.getInputStream();
            os = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            byte[] buf = new byte[1024];
            int count = 0;
            long sum = 0;
            while ((count = is.read(buf)) > 0 && isContinue.get()) {
                sum += count;
                publishProgress(sum);
                os.write(buf, 0, count);
            }
            return file;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
                if (conn != null)
                    conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        int progress = Float.valueOf(
                (Long.valueOf(values[0]).floatValue() / Long.valueOf(
                        length.get()).floatValue()) * 100).intValue();
        downloadDialog.setProgress(progress);
    }

    @Override
    protected void onPostExecute(final File result) {
        if (result == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("错误")
                    .setMessage("下载错误!")
                    .setPositiveButton("确定",
                            new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
        } else {
            if (isContinue.get()) {
                downloadDialog.setProgress(100);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("提示")
                        .setMessage("下载完成,是否安装?")
                        .setPositiveButton("安装",
                                new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent i = new Intent(
                                                Intent.ACTION_VIEW);
                                        i.setDataAndType(Uri.fromFile(result),
                                                "application/vnd.android.package-archive");
                                        context.startActivity(i);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("取消",
                                new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
            }

        }
        downloadDialog.dismiss();
    }
}
