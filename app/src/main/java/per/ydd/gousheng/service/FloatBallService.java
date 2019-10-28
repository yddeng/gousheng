package per.ydd.gousheng.service;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import per.ydd.gousheng.network.Coupon;
import per.ydd.gousheng.util.CommonUtil;

public class FloatBallService extends Service {
    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;

    private boolean isDoClip;

    /**
     * 当前类型
     */
    public int type;
    public final static int TYPE_TAOBAO =  1;
    public final static int TYPE_JINGDONG = 2;

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
        type = TYPE_TAOBAO;
        FloatWindowManager.addBallView(this);
        registerClipEvents();
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
                    isDoClip = true;

                    doEvent(content.toString());

                }
            }
        };
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    private void doEvent(String elem){
        switch (type){
            case TYPE_TAOBAO:
                if (CommonUtil.checkTkl(elem)) {
                    //后台查卷提示
                    FloatWindowManager.postAnim();
                    Coupon.tbCoupon(elem, new Coupon.CouponCallBack() {
                        @Override
                        public void Call(String text, String clickUrl, Exception err) {
                            isDoClip = false;
                            FloatWindowManager.couponRet(TYPE_TAOBAO,text,clickUrl,err);
                        }
                    });
                }else{
                    Log.d("TAG",  elem + "不是一个淘口令");
                    //Toast.makeText(FloatBallService.this, "不是一个淘口令", Toast.LENGTH_SHORT).show();
                    isDoClip = false;
                }
                break;
            case TYPE_JINGDONG:
                if (CommonUtil.checkJDUrl(elem)){
                    Coupon.jdCoupon(elem, new Coupon.CouponCallBack() {
                        @Override
                        public void Call(String text, String clickUrl, Exception err) {
                            isDoClip = false;
                            FloatWindowManager.couponRet(TYPE_JINGDONG,text,clickUrl,err);
                        }
                    });
                }else {
                    Log.d("TAG",  elem + "不是一个京东链接");
                    //Toast.makeText(FloatBallService.this, "不是一个淘口令", Toast.LENGTH_SHORT).show();
                    isDoClip = false;
                }
                break;
        }
    }


}
