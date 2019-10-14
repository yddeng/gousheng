package per.ydd.gousheng.view;


import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.CycleInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import per.ydd.gousheng.R;
import per.ydd.gousheng.service.FloatBallService;
import per.ydd.gousheng.util.ActivityUtil;
import per.ydd.gousheng.util.CommonUtil;

public class FloatBallView extends LinearLayout {
    private View mFloatBallView; //悬浮窗
    private TextView mTextView; //悬浮窗文本
    private static FloatBallService mService; //service

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private int screenWidth;

    private int mStatusBarHeight;
    private int mOffsetToParent;
    private int mOffsetToParentY;

    private long mLastDownTime;
    private int mLastDownY,mLastDownX;
    private boolean mIsMove;

    private float mTouchSlop; //轻微滑动
    private final static long CLICK_LIMIT = 200; //点击时间限制

    /**
     * 显示文本控制
     */
    private final static int SHOW_NONE = 0x000; //没有展示任何东西
    private final static int SHOW_TEXT = 0x001; //展示普通文本
    private final static int SHOW_COUPON = 0x002; //领卷文本

    private int showType;
    private Runnable runnable;
    private String couponClickUrl; //领卷地址
    private final static long SHOW_TIME = 4000; //内容显示时间

    // handler
    private Handler handler;

    public FloatBallView(Context context) {
        super(context);
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();

        mStatusBarHeight = getStatusBarHeight();
        mOffsetToParent = CommonUtil.dip2px(context,25);
        mOffsetToParentY = mStatusBarHeight + mOffsetToParent;

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mService = (FloatBallService) context;

        mFloatBallView = inflate(getContext(), R.layout.item_float_ball, this);
        mTextView = mFloatBallView.findViewById(R.id.float_ball_tv);

        handler = new Handler();
        initViewListener();
    }

    private void initViewListener(){
        mFloatBallView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastDownTime = System.currentTimeMillis();
                        mLastDownY = (int)event.getY();
                        mLastDownX = (int)event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isTouchSlop(event)){
                            return true;
                        }
                        mIsMove = true;
                        mLayoutParams.x = (int) (event.getRawX() - mOffsetToParent);
                        mLayoutParams.y = (int) (event.getRawY() - mOffsetToParentY);
                        mWindowManager.updateViewLayout(mFloatBallView,mLayoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!mIsMove && isClick(event)){
                            doClick();
                        }
                        if (mLayoutParams.x != screenWidth){
                            mLayoutParams.x = screenWidth;
                            mWindowManager.updateViewLayout(mFloatBallView,mLayoutParams);
                        }
                        mIsMove = false;

                        break;
                }
                return true;
            }
        });
    }

    private void doClick(){
        Log.d("doClick", "doClick: doClick");
        switch (showType){
            case SHOW_NONE:
                showText(mService.getString(R.string.float_ball_none_click_txt),SHOW_TEXT);
                break;
            case SHOW_TEXT:
                removeText();
                break;
            case SHOW_COUPON:
                if (CommonUtil.isApkInstalled(mService,"com.taobao.taobao")) {
                    ActivityUtil.openCouponActivity(mService,couponClickUrl);
                }else {
                    Toast.makeText(mService, "还没有安装淘宝app", Toast.LENGTH_SHORT).show();
                }
                removeText();
                break;
        }
        //cancelAnim();
    }

    private void showText(String text,int mShowType){
        showType = mShowType;
        mTextView.setText(text);
        Log.d("showText", "showText: "+text);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (showType != SHOW_NONE){
                    removeText();
                }
            }
        };
        handler.postDelayed(runnable,SHOW_TIME);
    }

    private void removeText(){
        showType = SHOW_NONE;
        mTextView.setText(mService.getString(R.string.float_ball_hint_txt));
        handler.removeCallbacks(runnable);
    }

    /**
     * 查卷提示动作
     */
    private void startAnim(){
        mTextView.animate().rotation(3f);
        mTextView.animate().setInterpolator(new CycleInterpolator(40));
        mTextView.animate().setDuration(5000);
        mTextView.animate().start();
    }

    private void cancelAnim(){
        mTextView.animate().cancel();
        mTextView.setRotation(0);
    }

    /**
     * 外部调用
     */
    public void postCoupon(final String text, String url){
        if (!TextUtils.isEmpty(url)){
            couponClickUrl = url;
            Log.d("TAG", "couponResp: "+ url);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                cancelAnim();
                showText(text,SHOW_COUPON);
            }
        });
    }

    public void postAnim(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                removeText();
                startAnim();
            }
        });
    }

    /**
     * 判断是否是单击
     */
    private boolean isClick(MotionEvent event) {
        float offsetY = Math.abs(event.getY() - mLastDownY);
        long time = System.currentTimeMillis() - mLastDownTime;
        if (offsetY < mTouchSlop * 2 && time < CLICK_LIMIT) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否是轻微滑动
     */
    private boolean isTouchSlop(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if ( Math.abs(x - mLastDownX) < mTouchSlop && Math.abs(y - mLastDownY) < mTouchSlop) {
            return true;
        }
        return false;
    }

    /**
     * 获取状态栏的高度
     */
    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }

    public void setLayoutParams(WindowManager.LayoutParams params) {
        mLayoutParams = params;
    }



}
