package per.ydd.gousheng.service;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import per.ydd.gousheng.view.FloatBallView;

import org.json.JSONObject;

public class FloatWindowManager {
    private static FloatBallView mBallView;
    private static WindowManager mWindowManager;
    private static FloatBallService mFloatBallService;

    public static void addBallView(Context context) {
        if (mBallView == null) {
            mFloatBallService = (FloatBallService)context;
            WindowManager windowManager = getWindowManager(context);
            int screenWidth = windowManager.getDefaultDisplay().getWidth();
            int screenHeight = windowManager.getDefaultDisplay().getHeight();
            mBallView = new FloatBallView(context);
            LayoutParams params = new LayoutParams();
            params.x = screenWidth;
            params.y = screenHeight / 2;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.LEFT | Gravity.TOP;
            //params.type = LayoutParams.TYPE_PHONE;
            //params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            // type
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                params.type = LayoutParams.TYPE_SYSTEM_ALERT;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                params.type = LayoutParams.TYPE_TOAST;
            } else {
                params.type = LayoutParams.TYPE_SYSTEM_ALERT;
            }
            params.format = PixelFormat.RGBA_8888;
            params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL  | LayoutParams.FLAG_NOT_FOCUSABLE;
            mBallView.setLayoutParams(params);
            windowManager.addView(mBallView, params);
        }
    }

    public static void removeBallView(Context context) {
        if (mBallView != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(mBallView);
            mBallView = null;
        }
    }

    public static void couponText(String text){
        if (mBallView != null){
            try {
                Log.d("TAG", "couponText: "+text);
                JSONObject jsonObject = new JSONObject(text);
                Integer code = jsonObject.getInt("code");
                if (code == 200) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String coupon_info, coupon_click_url;
                    String max_commission_rate;
                    String item_url;

                    if ((!data.isNull("coupon_info") && !data.isNull("coupon_click_url")) &&
                    (!data.isNull("max_commission_rate"))){ //有卷有佣金
                        coupon_info = data.getString("coupon_info");
                        coupon_click_url = data.getString("coupon_click_url");
                        //max_commission_rate = Float.parseFloat(data.getString("max_commission_rate"));
                        max_commission_rate = data.getString("max_commission_rate");
                        mBallView.postCoupon("点击领取 " + coupon_info + "卷,利率"+max_commission_rate, coupon_click_url);
                    }else  if ((!data.isNull("coupon_info") && !data.isNull("coupon_click_url")) &&
                            (data.isNull("max_commission_rate"))) { //有卷无佣金
                        coupon_info = data.getString("coupon_info");
                        coupon_click_url = data.getString("coupon_click_url");
                        mBallView.postCoupon("点击领取 " + coupon_info + "卷", coupon_click_url);
                    }else if ((data.isNull("coupon_info") || data.isNull("coupon_click_url")) &&
                            (!data.isNull("max_commission_rate"))) {//无卷有佣金
                        max_commission_rate = data.getString("max_commission_rate");
                        item_url = data.getString("item_url");
                        mBallView.postCoupon("点击领取 利率"+max_commission_rate, item_url);
                    }else {//无卷无佣金
                        mBallView.postCoupon("当前宝贝暂无优惠券", null);
                    }


                } else if (code == -1){ //没有参加活动
                    Log.d("TAG", "code: " + code + jsonObject.getString("msg"));
                    mBallView.postCoupon("当前宝贝暂无优惠券", null);
                }else { //请求错误
                    Log.d("TAG", "code: " + code +"msg: "+ jsonObject.getString("msg"));
                    mBallView.postCoupon("code: " + code +"msg: "+ jsonObject.getString("msg"),null);
                }

            }catch (Exception err){
                Log.d("TAG", "couponText: "+err.toString());
                mBallView.postCoupon(err.toString(),null);
            }

        }
    }

    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public static void postAnim(){
        mBallView.postAnim();
    }
}