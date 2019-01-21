package org.windning.safearea;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

/**
 * Device mgr impl on android P
 * On Android P we use window insets to judge the safe area
 */
class PDeviceManagerImpl implements IDeviceManager {

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
            WindowInsets insets = activity.getWindow().getDecorView().getRootWindowInsets();
            Rect safeRect = new Rect(insets.getStableInsetLeft(), insets.getStableInsetTop(),
                    insets.getStableInsetRight(), insets.getStableInsetBottom());
            /* Pick the larger value for top and bottom */
            safeRect.top = safeRect.bottom = Math.max(safeRect.top, safeRect.bottom);
            return safeRect;
        }catch(NullPointerException ignore) {
            Log.e("PDeviceManagerImpl","Display cutout not available");
            return null;
        }
    }
}