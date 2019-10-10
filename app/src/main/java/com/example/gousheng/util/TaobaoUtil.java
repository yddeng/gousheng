package com.example.gousheng.util;

import java.util.regex.Pattern;

public class TaobaoUtil {
    /**
     * 判断是不是淘口令
     */
    public static boolean checkTkl(String paramString)
    {
        return Pattern.compile("([\\p{Sc}])\\w{8,12}([\\p{Sc}])").matcher(paramString).find();
    }
}
