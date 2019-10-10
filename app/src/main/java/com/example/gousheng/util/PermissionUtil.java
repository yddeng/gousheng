package com.example.gousheng.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/*权限检查工具类*/

public class PermissionUtil {

    /**
     * 检查应用是否有悬浮窗权限（Android 6.0以上需要检查悬浮窗权限）
     */
    public static boolean hasOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 跳转系统悬浮窗授权页
     */
    public static void turnToOverlayPermission(Context context) {
        // Android 6.0以上需要配置
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }


}
