package org.windning.safearea;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.Nullable;

/**
 * Provide different impl of this to support different device types
 */
public interface IDeviceManager {
    boolean isNotch(Activity act);

    /**
     * Return null if safe rect couldn't be achieved
     * Note that the safe rect should be calculated based on PORTRAIT orientation
     */
    @Nullable Rect getSafeRect(Activity activity);

    /**
     * This should enable/disable the notch part to display content
     */
    void initWindowLayout(Activity activity, boolean enableNotch);
}
