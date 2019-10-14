package per.ydd.gousheng.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.TypedValue;

import java.util.ArrayList;

public class CommonUtil {
    /**
     * 检查是否安装了某应用
     */
    public static boolean isApkInstalled(Context context, String paramString) {
        if (TextUtils.isEmpty(paramString))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(paramString, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断服务是否启动
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningService) {
            if (runningServiceInfo.service.getClassName().equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * dip2px
     */
    public static int dip2px(Context context, float dip) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics()
        );
    }

    /**
     * 获取文件保存路径 sdcard根目录/download/文件名称
     * @param fileUrl
     * @return
     */
    public static String getSaveFilePath(String fileUrl){
        String fileName=fileUrl.substring(fileUrl.lastIndexOf("/")+1);//获取文件名称
        String newFilePath= Environment.getExternalStorageDirectory() + "/"+fileName;
        return newFilePath;
    }

}
