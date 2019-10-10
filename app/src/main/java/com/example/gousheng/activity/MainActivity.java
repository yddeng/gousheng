package com.example.gousheng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gousheng.R;
import com.example.gousheng.service.FloatBallService;
import com.example.gousheng.util.ActivityUtil;
import com.example.gousheng.util.PermissionUtil;


public class MainActivity extends AppCompatActivity {

    public static MainActivity mActivity;
    private Intent floatBallService;
    private Switch mSwitchFloatBall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initViewListener();
        initData();
    }

    private void initView() {
        mSwitchFloatBall = findViewById(R.id.switch_floatball);
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
                    floatBallService = new Intent(MainActivity.this, FloatBallService.class);
                    startService(floatBallService);
                }else {
                    if ( floatBallService != null){
                        stopService(floatBallService);
                        floatBallService = null;
                    }
                }
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 当前Activity静态引用赋值
        mActivity = this;
        // 检查是否有悬浮窗权限，没有给出弹框提醒
        if (!PermissionUtil.hasOverlayPermission(this)) {
            ActivityUtil.showOverlayAlertDialog(this);
        }
    }

    @Override
    protected void onDestroy() {
        // 当前Activity静态引用赋空
        mActivity = null;
        super.onDestroy();
    }

}