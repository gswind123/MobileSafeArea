package org.windning.safearea;

import android.app.Activity;
import android.content.Context;
import android.content.QuickViewConstants;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Common utils collection during this project
 */
class SafeAreaUtils {
    private static final int DEFAULT_ROUND_CORNER_PADDING = 80; //pixel
    private static final float DEFAULT_ROUND_CORNER_RATIO = 1.8f;

    public static boolean CheckIfLandscape(Context context) {
        try{
            WindowManager mgr = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
            int orientation = mgr.getDefaultDisplay().getRotation();
            switch(orientation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    return false; // portrait
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                default:
                    return true;
            }
        }catch(NullPointerException ignore) {
            return true; // use landscape for default
        }
    }

    /**
     * Rotate the safe rect if the current activity is LANDSCAPE
     * Safe rect calculated based on PORTRAIT should be applied
     * Note that here we assuming that the safe rect wouldn't change when the screen is upside down
     */
    public static Rect normalizeScreenRect(Rect safeRect, Activity activity) {
        if(activity == null || safeRect == null) {
            return null;
        }
        boolean isLandscape = SafeAreaUtils.CheckIfLandscape(activity);
        if(isLandscape) {
            return new Rect(safeRect.top, safeRect.right, safeRect.bottom, safeRect.left);
        } else {
            return safeRect;
        }
    }

    public static Rect getPortraitScreenSize(Activity activity) {
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        Rect screenRect = new Rect(0, 0, dm.widthPixels, dm.heightPixels);
        boolean isLandscape = SafeAreaUtils.CheckIfLandscape(activity);
        if (isLandscape) {
            screenRect.right = dm.heightPixels;
            screenRect.bottom = dm.widthPixels;
        }
        return screenRect;
    }

    /**
     * A "vital" notch is the kind that can't be ignored (that is, a non-vital notch could be ignored)
     * A vital notch currently is the devices with a large cutoff area like IPHONE-X
     * Note that the input rects should be normalized to PORTRAIT mode
     */
    public static boolean isVitalNotch(Rect notch, Rect screen) {
        if(notch.width() == 0 || notch.height() == 0) {
            return false;
        }
        /* A symmetrical notch is not vital when it's small and like a square */
        float smallHeight = screen.height() * 0.05f;
        float smallWidth = screen.width() * 0.2f;
        if(notch.height() < smallHeight && notch.width() < smallWidth) {
            return false;
        }
        return true;
    }

    /**
     * Get the first element from an array
     * This is designed to provide an interface for non-java environment
     */
    public static /* Reflected */ Object getFirstElement(Object array) {
        try{
            return Array.get(array, 0);
        }catch(Exception ignore) {
            return null;
        }
    }

    private static boolean isPixelSimilar(int a, int b) {
        int delta = a - b;
        int threshold = 5;
        return delta >= -threshold && delta <= threshold;
    }

    @RequiresApi(api=Build.VERSION_CODES.O)
    public static void initWindowLayoutOppoAndVivo(Activity activity, boolean enableNotch) {
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