package com.example.gousheng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gousheng.R;
import com.example.gousheng.service.FloatBallService;
import com.example.gousheng.util.ActivityUtil;
import com.example.gousheng.util.CommonUtil;
import com.example.gousheng.util.PermissionUtil;


public class MainActivity extends AppCompatActivity {

    public static MainActivity mActivity;
    private Intent floatBallService;
    private Switch mSwitchFloatBall;

    static String SWITCH_CLICK = "switch_click";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            initView();
            initViewListener();
            initData();
    }

    private void initView() {
        mActivity = this;
        mSwitchFloatBall = findViewById(R.id.sw_float);
        floatBallService = new Intent(mActivity, FloatBallService.class);
    }

    /**
     * 初始化视图监听器
     */
    private void initViewListener() {
        // "悬浮窗权限"点击监听
        mSwitchFloatBall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!PermissionUtil.hasOverlayPermission(MainActivity.this)) {
                    ActivityUtil.showOverlayAlertDialog(MainActivity.this);
                    mSwitchFloatBall.setChecked(false);
                    return;
                }
                if (isChecked){
                    startService(floatBallService);
                }else {
                    if (CommonUtil.isServiceRunning(mActivity,FloatBallService.class.getName())){
                        stopService(floatBallService);
                    }
                }
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 检查是否有悬浮窗权限，没有给出弹框提醒
        //if (!PermissionUtil.hasOverlayPermission(this)) {
        //    ActivityUtil.showOverlayAlertDialog(this);
        //}

        //判断服务是否启动
        if (CommonUtil.isServiceRunning(mActivity,FloatBallService.class.getName())){
            Log.d("TAG", "initData: true");
            mSwitchFloatBall.setChecked(true);
        }

    }

    @Override
    protected void onDestroy() {
        // 当前Activity静态引用赋空
        mActivity = null;
        super.onDestroy();
    }
}