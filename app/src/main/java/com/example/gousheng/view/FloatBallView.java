package com.example.gousheng.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gousheng.R;
import com.example.gousheng.service.FloatBallService;
import com.example.gousheng.util.ActivityUtil;
import com.example.gousheng.util.CommonUtil;

import java.lang.reflect.Field;

public class FloatBallView extends LinearLayout {
    private TextView mTextView;
    private WindowManager.LayoutParams mLayoutParams;
    private static FloatBallService mService;

    private long mLastDownTime;
    private float mLastDownX;
    private float mLastDownY;

    private boolean mIsTouch;
    private boolean mIsCopy;
    private boolean mIsMove;

    private float mTouchSlop;
    private final static long LONG_CLICK_LIMIT = 500; //长按复制
    private final static long CLICK_LIMIT = 200;

    //文本控制
    private boolean isShowText;
    private boolean isHaveCoupon;
    private String couponClickUrl;
    private static String staticText = "购省";
    private final static long SHOW_TIME = 5000;

    //
    private Handler handler;

    public FloatBallView(Context context) {
        super(context);
        inflate(getContext(), R.layout.lay_floatball, this);
        mTextView = findViewById(R.id.ball_text);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mService = (FloatBallService) context;
        handler = new Handler();
    }

    @Override
    public boolean onTouchEvent( MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouch = true;
                mLastDownTime = System.currentTimeMillis();
                mLastDownX = event.getX();
                mLastDownY = event.getY();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsTouch && isCopy()) {
                            mIsCopy = true;
                            doCopy();
                        }
                    }
                }, LONG_CLICK_LIMIT);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsCopy && isTouchSlop(event)) {
                    return true;
                }
                mIsMove = true;
                break;
            case MotionEvent.ACTION_UP:
                if (mIsTouch && !mIsCopy && isClick(event)){
                    doClick();
                }
                mIsTouch = false;
                mIsMove = false;
                mIsCopy = false;
                break;
        }
        return true;
    }

    private void doCopy(){
        Log.d("doCopy", "doCopy: copy");
        Toast.makeText(mService,"copy",Toast.LENGTH_SHORT).show();

    }

    private void doClick(){
        Log.d("doClick", "doClick: doClick");
        if (isShowText && isHaveCoupon) {
            if (CommonUtil.isApkInstalled(mService,"com.taobao.taobao")) {
                ActivityUtil.openCouponActivity(mService,couponClickUrl);
            }
        }else{
            showText("clicked");
        }
    }

    private void showText(String text){
        isShowText = true;
        mTextView.setText(text);
        Log.d("showText", "showText: "+text);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isShowText) {
                    isShowText = false;
                    mTextView.setText(staticText);
                }
                if (isHaveCoupon){
                    isHaveCoupon = false;
                }
            }
        }, SHOW_TIME);
    }

    public void couponResp(final String text, String url){
        if (!TextUtils.isEmpty(url)){
            isHaveCoupon = true;
            couponClickUrl = url;
            Log.d("TAG", "couponResp: "+url);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                showText(text);
            }
        });
    }

    /**
     * 判断是否是单击
     */
    private boolean isClick(MotionEvent event) {
        float offsetX = Math.abs(event.getX() - mLastDownX);
        float offsetY = Math.abs(event.getY() - mLastDownY);
        long time = System.currentTimeMillis() - mLastDownTime;

        if (offsetX < mTouchSlop * 2 && offsetY < mTouchSlop * 2 && time < CLICK_LIMIT) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCopy() {
        long time = System.currentTimeMillis();
        if (!mIsMove && (time - mLastDownTime >= LONG_CLICK_LIMIT)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是轻微滑动
     */
    private boolean isTouchSlop(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (Math.abs(x - mLastDownX) < mTouchSlop && Math.abs(y - mLastDownY) < mTouchSlop) {
            return true;
        }
        return false;
    }

    public void setLayoutParams(WindowManager.LayoutParams params) {
        mLayoutParams = params;
    }
}
