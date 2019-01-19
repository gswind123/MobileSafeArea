package org.windning.safearea;

import android.app.Activity;
import android.content.QuickViewConstants;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Common utils collection during this project
 */
class SafeAreaUtils {
    private static final int DEFAULT_ROUND_CORNER_PADDING = 80; //pixel
    public static final float DEFAULT_ROUND_CORNER_RATIO = 1.8f;

    /**
     * Rotate the safe rect if the current activity is LANDSCAPE
     * Safe rect calculated based on PORTRAIT should be applied
     * Note that here we assuming that the safe rect wouldn't change when the screen is upside down
     */
    public static Rect normalizeScreenRect(Rect safeRect, Activity activity) {
        if(activity == null || safeRect == null) {
            return null;
        }
        int orientation = activity.getRequestedOrientation();
        if(orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            return new Rect(safeRect.top, safeRect.right, safeRect.bottom, safeRect.left);
        } else {
            return safeRect;
        }
    }

    /**
     * As with rounded corner screens, a "min safe rect padding" should be applied
     * Here we assume that a device with screen ratio larger that 2:1 (height : width) is a "rounded corner" device
     * The input safe rect here should be based on PORTRAIT
     */
    public static Rect protectRoundCorner(Rect safeRect, Activity activity, ArrayList<String> nonRoundDeviceList) {
        if(safeRect == null || activity == null) {
            return null;
        }
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        if(dm.widthPixels == 0 || dm.heightPixels == 0) {
            return safeRect; // invalid display metrics
        }

        float ratio = 0;
        if(dm.heightPixels > dm.widthPixels) {
            ratio = ((float)dm.heightPixels) / dm.widthPixels;
        } else {
            ratio = ((float)dm.widthPixels) / dm.heightPixels;
        }
        if(ratio < DEFAULT_ROUND_CORNER_RATIO) {
            return safeRect; // Not a rounded corner one
        }
        // Check if current devices is a specified "non-rounded-corner" device
        if(nonRoundDeviceList != null) {
            String curModel = Build.MODEL;
            for(int i = 0, count = nonRoundDeviceList.size(); i < count; i++) {
                String model = nonRoundDeviceList.get(i);
                if(model != null && model.equals(curModel)) {
                    return safeRect; // Do nothing when this is not a rounded corner device
                }
            }
        }
        // During this phase the devices is ensured to be de rounded-corner one
        Rect newRect = new Rect(safeRect);
        newRect.top = newRect.bottom = Math.max(Math.max(safeRect.top, safeRect.bottom), DEFAULT_ROUND_CORNER_PADDING);
        return newRect;
    }

    public static void initWindowLayoutVivoAndOppo(Activity activity, boolean enableNotch) {
        Window window = activity.getWindow();
        if(enableNotch) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        View decor = window.getDecorView();
        if(decor == null){
            return;
        }
        int vis = decor.getSystemUiVisibility();
        if(enableNotch) {
            vis = vis | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            vis = vis | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        } else {
            vis = vis & (~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            vis = vis & (~View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        decor.setSystemUiVisibility(vis);
    }
}