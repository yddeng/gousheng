package com.example.gousheng.network;

import android.app.ProgressDialog;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import per.ydd.http.HttpClient;

public class Update {
    public interface UpdateCallBack{
        void Call(String result,Exception err);
    }

    private static String url = "http://www.yddeng.xyz:6363/update";

    public static void getUpdate(String version,final Update.UpdateCallBack callBack) {
        try {
            String param = "version=" + URLEncoder.encode(version, "UTF-8");
            Log.d("TAG", "getCoupon: " + param);
            HttpClient.Post(url, param, new HttpClient.CallBack() {
                @Override
                public void Call(String result, Exception err) {
                    if (err == null) {
                        callBack.Call(result, null);
                    } else {
                        callBack.Call(null, err);
                    }
                }
            });
        } catch (Exception err) {
            callBack.Call(null, err);
        }
    }

    public interface UpdateProgress{
        void setMax(Integer max);
        void setProgress(Integer progress);
        void end(File file);
        void error(Exception err);
    }

    public static void downloadApk(final String downUrl, final String path, final UpdateProgress updateProgress){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(downUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("GET");
                    //设置连接超时时间
                    connection.setConnectTimeout(5000);
                    //设置最大值
                    Integer totalLen = connection.getContentLength();
                    updateProgress.setMax( totalLen);
                    //返回输入流
                    InputStream in = connection.getInputStream();

                    //文件
                    File file = new File(path);
                    FileOutputStream fos = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];
                    int len,currentLen = 0;
                    while ((len = in.read(buffer)) != -1){
                        currentLen += len;
                        fos.write(buffer,0,len);
                        updateProgress.setProgress(currentLen);
                    }

                    if (currentLen == totalLen){
                        updateProgress.end(file);
                    }

                } catch (Exception e) {
                    updateProgress.error(e);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {//关闭连接
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
