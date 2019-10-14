package per.ydd.gousheng.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/*权限检查工具类*/

public class PermissionUtil {

    /**
     * 检查应用是否有悬浮窗权限（Android 6.0以上需要检查悬浮窗权限）
     */
    public static boolean checkOverlayPermission(Context context) {
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

    /**
     * 读写权限
     */
    public static boolean checkStoragePermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * 安装应用权限
     */
    public static boolean checkInstallPermission(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.REQUEST_INSTALL_PACKAGES)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * 权限检查
     */
    public static List<String> checkPermissions(Context context,String[] permissions){
        // 创建一个权限列表，把需要使用而没用授权的的权限存放在这里
        List<String> permissionList = new ArrayList<>();

        // 判断权限是否已经授予，没有就把该权限添加到列表中
        for (String per : permissions){
            if (ContextCompat.checkSelfPermission(context,per) != PackageManager.PERMISSION_GRANTED){
                permissionList.add(per);
            }
        }

        return permissionList;
    }
}
