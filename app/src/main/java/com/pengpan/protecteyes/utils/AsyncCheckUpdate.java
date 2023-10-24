package com.pengpan.protecteyes.utils;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AsyncCheckUpdate extends AsyncTask<String, String, AppUpdate> {
    private final Context context;
    private String url, packageName;
    private int versionCode;
    private ProgressDialog downloadDialog;

    public AsyncCheckUpdate(Context context, String url, String packageName, int versionCode) {
        super();
        this.context = context;
        this.url = url;
        this.packageName = packageName;
        this.versionCode = versionCode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        downloadDialog = new ProgressDialog(context);
        downloadDialog.setTitle("提示");
        downloadDialog.setMessage("正在检查更新...");
    }

    @Override
    protected AppUpdate doInBackground(String... params) {
        try {
            AppUpdate appUpdate = parser(url, packageName, versionCode);
            return appUpdate;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(final AppUpdate appUpdate) {
        downloadDialog.dismiss();
        if (appUpdate == null) {
            ToastUtil.showToast(context, "暂无更新");
        } else {
            Builder builder = new Builder(context);
            builder.setTitle("检查更新");
            builder.setMessage("检查的新版本，是否现在更新？");
            builder.setPositiveButton("立即更新", new OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    AppUtil.downloadApp(context, appUpdate);
                }
            });
            builder.setNegativeButton("暂不更新", new OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            builder.create().show();
        }
    }

    public static final AppUpdate parser(String uri, String packageName, int versionCode) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(uri);
        Element rootElement = document.getDocumentElement();
        NodeList nodeList = rootElement.getElementsByTagName("app");

        for (int i = 0; i < nodeList.getLength(); i++) {
            AppUpdate app = new AppUpdate();
            Node node = nodeList.item(i);
            NamedNodeMap namedNodeMap = node.getAttributes();
            if (namedNodeMap == null)
                continue;
            Node _package = namedNodeMap.getNamedItem("package");
            Node _versionCode = namedNodeMap.getNamedItem("versionCode");
            Node _versionName = namedNodeMap.getNamedItem("versionName");
            Node _url = namedNodeMap.getNamedItem("url");
            Node _size = namedNodeMap.getNamedItem("size");

            app._package = _package == null ? "" : _package.getNodeValue();
            app._package = app._package == null ? "" : app._package;
            if (app._package.trim().equals(packageName)) {
                String _versionCodeString = _versionCode == null || _versionCode.getNodeValue() == null ? "0" : _versionCode.getNodeValue();
                _versionCodeString = _versionCodeString == null || _versionCodeString.trim().equals("") ? "0" : _versionCodeString.trim();
                int _versionCodeInt = 0;
                try {
                    _versionCodeInt = Integer.parseInt(_versionCodeString);
                } catch (Throwable e) {
                    _versionCodeInt = 0;
                }
                app._versionCode = _versionCodeInt;
                if (versionCode < app._versionCode) {
                    app._versionName = _versionName == null ? "" : _versionName.getNodeValue();
                    app._versionName = app._versionName == null ? "" : app._versionName;
                    app._url = _url == null ? "" : _url.getNodeValue();
                    app._url = app._url == null ? "" : app._url;
                    String _sizeString = _size == null ? "0" : _size.getNodeValue();
                    _sizeString = _sizeString == null || _sizeString.trim().equals("") ? "0" : _sizeString.trim();
                    long _sizeLong = 0L;
                    try {
                        _sizeLong = Long.parseLong(_sizeString);
                    } catch (Throwable e) {
                        _sizeLong = 0L;
                    }
                    app._size = _sizeLong;
                    return app;
                }
            }
        }
        return null;
    }
}