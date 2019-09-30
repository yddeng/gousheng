package com.example.gousheng;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import pre.ydd.http.HttpClient;

public class FloatBallService extends Service {
    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;
    private HttpClient httpClient;

    private boolean isDoClip;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FloatWindowManager.addBallView(this);
        registerClipEvents();
        httpClient = new HttpClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatWindowManager.removeBallView(this);
        if (mClipboardManager != null && mOnPrimaryClipChangedListener != null) {
            mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }
    }

    /**
     * 注册剪切板复制、剪切事件监听
     */
    private void registerClipEvents() {
        Log.d("TAG", "registerClipEvents" );
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if (mClipboardManager.hasPrimaryClip() && mClipboardManager.getPrimaryClip().getItemCount() > 0 && !isDoClip) {
                    // 获取复制、剪切的文本内容
                    final CharSequence content =  mClipboardManager.getPrimaryClip().getItemAt(0).getText();
                    Log.d("TAG", "复制、剪切的内容为：" + content);
                    isDoClip = true;

                    httpClient.Get("http://10.128.2.252:6363/test", "name=sss", new HttpClient.CallBack() {
                        @Override
                        public void Call(String result, Exception err) {
                            if (err == null){
                                isDoClip = false;
                                //传给Manager
                                FloatWindowManager.postText(content.toString()+ ":" +result);
                            }else {
                                Log.w("HttpClientGET", "Call: " + err.toString() );
                            }
                        }
                    });

                }
            }
        };
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }



}
