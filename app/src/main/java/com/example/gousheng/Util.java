package com.example.gousheng;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //判断是不是淘口令
    public static boolean checkTkl(String text){
        //String pattern = “.([€₤₳¢¤฿฿₵₡₫ƒ₲₭£₥₦₱〒₮₩₴₪៛﷼₢ℳ₰₯₠₣₧ƒ][a−z0−9A−Z]9,11[€₤₳¢¤฿฿₵₡₫ƒ₲₭£₥₦₱〒₮₩₴₪៛﷼₢ℳ₰₯₠₣₧ƒ][a-z0-9A-Z]{9,11}[€₤₳¢¤฿฿₵₡₫ƒ₲₭£₥₦₱〒₮₩₴₪៛﷼₢M₰₯₠₣₧ƒ][a−z0−9A−Z]9,11[€₤₳¢¤฿฿₵₡₫ƒ₲₭£₥₦₱〒₮₩₴₪៛﷼₢ℳ₰₯₠₣₧ƒ]).”;
        String pattern = "([\\p{Sc}])\\w{8,12}([\\p{Sc}])";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(text);
        if (m.find()){
            return true;
        }
        return false;
    }
}
