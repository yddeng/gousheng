package com.example.gousheng.service;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.gousheng.manager.FloatWindowManager;
import com.example.gousheng.network.Coupon;
import com.example.gousheng.util.TaobaoUtil;

public class FloatBallService extends Service {
    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;
    private Coupon coupon;

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
        coupon = new Coupon();
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
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if (mClipboardManager.hasPrimaryClip() && mClipboardManager.getPrimaryClip().getItemCount() > 0 && !isDoClip) {
                    // 获取复制、剪切的文本内容
                    final CharSequence content =  mClipboardManager.getPrimaryClip().getItemAt(0).getText();
                    Log.d("TAG", "复制、剪切的内容为：" + content);
                    Toast.makeText(FloatBallService.this, content.toString(), Toast.LENGTH_SHORT).show();
                    isDoClip = true;

                    if (TaobaoUtil.checkTkl(content.toString())) {
                        coupon.getCoupon(content.toString(), new Coupon.CouponCallBack() {
                            @Override
                            public void Call(String result, Exception err) {
                                isDoClip = false;
                                if (err == null) {
                                    //传给Manager
                                    FloatWindowManager.couponText(result);
                                } else {
                                    Log.w("HttpClientGET", "Call: " + err.toString());
                                    FloatWindowManager.couponText(err.toString());
                                }
                            }
                        });
                    }else {
                        Log.d("TAG",  content.toString() + "不是一个淘口令");
                        Toast.makeText(FloatBallService.this, "不是一个淘口令", Toast.LENGTH_SHORT).show();
                        isDoClip = false;
                    }

                }
            }
        };
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }



}
