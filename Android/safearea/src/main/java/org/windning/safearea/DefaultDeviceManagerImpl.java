package org.windning.safearea;

import android.app.Activity;
import android.graphics.Rect;

/**
 * This is the default device manager and would do nothing but return (0, 0, 0, 0) as
 * the safe area
 */
class DefaultDeviceManagerImpl implements IDeviceManager {
    @Override
    public Rect getSafeRect(Activity activity) {
        return new Rect(0, 0, 0, 0);
    }

    @Override
    public void initWindowLayout(Activity activity, boolean enableNotch) {
        //Do nothing
    }

    @Override
    public boolean isNotch(Activity act) {
        return false;
    }
}
