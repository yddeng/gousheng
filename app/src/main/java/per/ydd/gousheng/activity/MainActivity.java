package per.ydd.gousheng.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import per.ydd.gousheng.BuildConfig;
import per.ydd.gousheng.R;
import per.ydd.gousheng.network.Update;
import per.ydd.gousheng.service.FloatBallService;
import per.ydd.gousheng.util.ActivityUtil;
import per.ydd.gousheng.util.CommonUtil;
import per.ydd.gousheng.util.PermissionUtil;
import per.ydd.gousheng.util.ProperUtil;
import per.ydd.gousheng.view.DialogView;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Properties;


public class MainActivity extends AppCompatActivity {

    public static MainActivity mActivity;
    private Intent floatBallService;
    private Switch mSwitchFloatBall;

    private TextView bottomTV;
    private Button bottomBtn;

    Properties proper;
    Handler handler = new Handler();

    private Data data ;

    private static String[] permissions = new String[]{
        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private int mRequestCode = 101;

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
                if (!PermissionUtil.checkOverlayPermission(MainActivity.this)) {
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
                List<String> mPermissionList = PermissionUtil.checkPermissions(mActivity,permissions);
                if (mPermissionList.size() >0){
                    ActivityCompat.requestPermissions(mActivity,mPermissionList.toArray(new String[mPermissionList.size()]),mRequestCode);
                }else {
                    checkVersion();
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        proper = ProperUtil.getProperties(mActivity,"appConfig");
        bottomTV.setText(BuildConfig.VERSION_NAME);

        data = (Data)getApplication();
        Update.getUpdate(Integer.toString(BuildConfig.VERSION_CODE), new Update.UpdateCallBack() {
            @Override
            public void Call(String result, Exception err) {
                if (err == null){
                    data.setUpdateData(result);
                }else {
                    Log.d("TAG", "Call: "+err.toString());
                }
            }
        });


        /*
        //判断服务是否启动
        if (CommonUtil.isServiceRunning(mActivity,FloatBallService.class.getName())){
            Log.d("TAG", "initData: true");
            mSwitchFloatBall.setChecked(true);
        }
        */

        // 如果已经授权，直接开启
        if (PermissionUtil.checkOverlayPermission(MainActivity.this)) {
            mSwitchFloatBall.setChecked(true);
        }

        //读写权限申请
        List<String> mPermissionList = PermissionUtil.checkPermissions(mActivity,permissions);
        if (mPermissionList.size() >0){
            ActivityCompat.requestPermissions(mActivity,mPermissionList.toArray(new String[mPermissionList.size()]),mRequestCode);
        }
    }

    /**
     * 检查更新
     */
    private void checkVersion(){
        String result = data.getUpdateData();
        if (TextUtils.isEmpty(result)) {
            Log.d("TAG", "checkVersion: data is nil");
            DialogView.showInstance(mActivity, "更新", "当前已是最新版本", "确定", new DialogView.CancleCallback() {
                @Override
                public void onCancle() {                            }
            }, null,null);
        }else {
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
        }
    }

    private void startDown(String downUrl){
        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage("下载中...");
        dialog.setCancelable(false);
        dialog.show();

        String savePath = CommonUtil.getSaveFilePath(downUrl);
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
                        Toast.makeText(mActivity,"ret"+errr,Toast.LENGTH_LONG).show();
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
        Log.d("TAG", "installApk: "+Uri.fromFile(file));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID+".fileprovider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // 当前Activity静态引用赋空
        mActivity = null;
        super.onDestroy();
    }
}