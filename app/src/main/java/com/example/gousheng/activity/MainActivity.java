package com.example.gousheng.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gousheng.R;
import com.example.gousheng.network.Update;
import com.example.gousheng.service.FloatBallService;
import com.example.gousheng.util.ActivityUtil;
import com.example.gousheng.util.CommonUtil;
import com.example.gousheng.util.PermissionUtil;
import com.example.gousheng.util.ProperUtil;
import com.example.gousheng.view.DialogView;

import org.json.JSONObject;

import java.io.File;
import java.util.Properties;


public class MainActivity extends AppCompatActivity {

    public static MainActivity mActivity;
    private Intent floatBallService;
    private Switch mSwitchFloatBall;

    private TextView bottomTV;
    private Button bottomBtn;

    Properties proper;

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
        bottomTV = findViewById(R.id.bottom_ll_tv);
        bottomBtn = findViewById(R.id.bottom_ll_btn);

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

        //更新按钮
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersion();
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

        proper = ProperUtil.getProperties(mActivity,"appConfig");
        String name =  "v"+CommonUtil.getVersionName(mActivity);
        bottomTV.setText(name);

        //判断服务是否启动
        if (CommonUtil.isServiceRunning(mActivity,FloatBallService.class.getName())){
            Log.d("TAG", "initData: true");
            mSwitchFloatBall.setChecked(true);
        }

    }

    /**
     * 检查更新
     */
    Handler handler = new Handler();
    private void checkVersion(){
        Update.getUpdate(CommonUtil.getVersionCode(mActivity), new Update.UpdateCallBack() {
            @Override
            public void Call(String result, Exception err) {
                if (err == null){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        Integer code = jsonObject.getInt("code");
                        if (code == 2 ){
                            String msg = jsonObject.getString("message");
                            final String downUrl = jsonObject.getString("down_url");
                            DialogView.showInstance(mActivity, "更新", msg, "忽略", new DialogView.CancleCallback() {
                                @Override
                                public void onCancle() {

                                }
                            }, "更新", new DialogView.ConfirmCallback() {
                                @Override
                                public void onConfirm() {
                                    Log.d("TAG", "onConfirm: "+downUrl);
                                    startDown(downUrl);
                                }
                            });
                        }else{
                            String msg = jsonObject.getString("message");
                            DialogView.showInstance(mActivity, "更新", msg, "确定", new DialogView.CancleCallback() {
                                @Override
                                public void onCancle() {                            }
                            }, null,null);
                        }
                    }catch (Exception e){
                        Log.d("TAG", "checkVersion: "+e.toString());
                    }
                }else{
                    Log.d("TAG", "err: "+err.toString());
                    final String errr = err.toString();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity,"post"+errr.toString(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void startDown(String downUrl){
        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage("正在下载");
        dialog.setCancelable(false);
        dialog.show();

        String name = downUrl.substring(downUrl.lastIndexOf("/")+1);
        String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +"/"+ name;
        Update.downloadApk(downUrl, savePath, new Update.UpdateProgress() {
            @Override
            public void setMax(Integer max) {
                dialog.setMax(max);
            }

            @Override
            public void setProgress(Integer progress) {
                dialog.setProgress(progress);
            }

            @Override
            public void end(File file) {
                dialog.cancel();
                installApk(file);
            }

            @Override
            public void error(Exception err) {
                Log.d("TAG", "error: "+err.toString());
                dialog.cancel();
                final String errr = err.toString();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity,"ret"+errr.toString(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /**
     * 下载完成,提示用户安装
     */
    private void installApk(File file) {
        //调用系统安装程序
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // 当前Activity静态引用赋空
        mActivity = null;
        super.onDestroy();
    }
}