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

    public static void couponRet(int type,String text,String url,Exception err){
        if (mBallView != null){
            if (err == null){
                mBallView.postCoupon(text,url);
            }else {
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