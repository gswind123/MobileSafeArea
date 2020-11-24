package org.windning.safearea;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.Surface;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.util.List;

/**
 * Device mgr impl on android P
 * On Android P we use window insets to judge the safe area
 */
class PDeviceManagerImpl extends BaseDeviceManager {

    @Override
    public boolean isNotch(Activity activity) {
        Rect safeRect = this.getSafeRect(activity);
        return safeRect != null && (safeRect.left != 0 || safeRect.top != 0 ||
                safeRect.right != 0 || safeRect.bottom != 0);
    }

    @Override
    public Rect getSafeRect(Activity activity) {
        if(activity == null) {
            return null;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return getWindowInsets(activity);
        } else {
            return null;
        }
    }

    @Override
    public void initWindowLayout(Activity activity, boolean enableNotch) {
        if(activity == null) {
            return;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            enableCutoutShortEdge(activity, enableNotch);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void enableCutoutShortEdge(Activity act, boolean enableNotch) {
        Window window = act.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if(enableNotch) {
            attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        } else {
            attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        }
        window.setAttributes(attributes);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private Rect getWindowInsets(@NonNull Activity activity) {
        try{
            boolean isLandscape = SafeAreaUtils.CheckIfLandscape(activity);
            /* Parse screen size */
            Rect screenRect = SafeAreaUtils.getPortraitScreenSize(activity);
            /* Parse notch rects */
            WindowInsets insets = activity.getWindow().getDecorView().getRootWindowInsets();
            DisplayCutout cutout = insets.getDisplayCutout();
            List<Rect> boundingRects = cutout.getBoundingRects();
            Rect safeRect = new Rect(0, 0, 0, 0);
            /* Here we use the standard of PORTRAIT orientation */
            for(Rect rect : boundingRects) {
                Rect normalizedRect = new Rect();
                if(isLandscape) {
                    normalizedRect.right = rect.height();
                    normalizedRect.bottom = rect.width();
                } else {
                    normalizedRect.right = rect.width();
                    normalizedRect.bottom = rect.height();
                }
                if(!SafeAreaUtils.isVitalNotch(normalizedRect, screenRect)) {
                    continue; // skip non-vital notch rects
                }
                safeRect.top = normalizedRect.height();
            }
            /* Pick the larger value for top and bottom */
            safeRect.top = safeRect.bottom = Math.max(safeRect.top, safeRect.bottom);
            return safeRect;
        }catch(NullPointerException ignore) {
            Log.e("PDeviceManagerImpl","Display cutout not available");
            return null;
        }
    }
}