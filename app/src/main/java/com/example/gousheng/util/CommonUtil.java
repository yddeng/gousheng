package com.example.gousheng.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class CommonUtil {
    /**
     * 检查是否安装了某应用
     */
    public static boolean isApkInstalled(Context context, String paramString)
    {
        if (TextUtils.isEmpty(paramString))
            return false;
        try
        {
            context.getPackageManager().getApplicationInfo(paramString, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return false;
    }



}
