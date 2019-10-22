package per.ydd.gousheng.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import per.ydd.gousheng.view.DialogView;

public class ActivityUtil {
    /**
     * 显示"悬浮窗权限"提醒对话框
     */
    public static void showOverlayAlertDialog(final AppCompatActivity activity) {
        DialogView.showInstance(
                activity,
                "授权",
                "需要开启【悬浮窗权限】",
                "取消",
                new DialogView.CancleCallback() {
                    @Override
                    public void onCancle() {

                    }
                },
                "去开启",
                new DialogView.ConfirmCallback() {
                    @Override
                    public void onConfirm() {
                        PermissionUtil.turnToOverlayPermission(activity);
                    }
                });
    }

    /**
     * 打开淘宝应用浏览器
     * com.taobao.taobao   com.taobao.browser.BrowserActivity
     */
    public static void openBrowserActivity(Context context,String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.View");
        intent.setClassName("com.taobao.taobao", "com.taobao.browser.BrowserActivity");
        Uri uri = Uri.parse(url);//clickUrl,领券地址
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
