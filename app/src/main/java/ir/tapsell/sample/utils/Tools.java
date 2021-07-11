package ir.tapsell.sample.utils;

import android.app.Activity;
import android.os.Build;

public class Tools {

    public static boolean isActivityDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.isDestroyed();
        }
        return false;
    }
}
