package com.example.gousheng.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gousheng.view.CustomDialog;

public class ActivityUtil {
    /**
     * 显示"悬浮窗权限"提醒对话框
     */
    public static void showOverlayAlertDialog(final AppCompatActivity activity) {
        CustomDialog.showInstance(
                activity,
                "需要开启【悬浮窗权限】",
                "取消",
                new CustomDialog.CancleCallback() {
                    @Override
                    public void onCancle() {

                    }
                },
                "去开启",
                new CustomDialog.ConfirmCallback() {
                    @Override
                    public void onConfirm() {
                        PermissionUtil.turnToOverlayPermission(activity);
                    }
                });
    }

    /**
     * 打开淘宝应用粉丝福利购
     * com.taobao.taobao   com.taobao.browser.BrowserActivity
     */
    public static void openCouponActivity(Context context,String clickUrl) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.View");
        intent.setClassName("com.taobao.taobao", "com.taobao.browser.BrowserActivity");
        Uri uri = Uri.parse(clickUrl);//clickUrl,领券地址
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
