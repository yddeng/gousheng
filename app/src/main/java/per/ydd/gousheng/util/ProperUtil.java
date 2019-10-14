package per.ydd.gousheng.util;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

public class ProperUtil {
    public static Properties getProperties(Context c,String name){
        Properties urlProps;
        Properties props = new Properties();
        try {
            InputStream in = c.getAssets().open(name);
            props.load(in);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        urlProps = props;
        return urlProps;
    }
}
