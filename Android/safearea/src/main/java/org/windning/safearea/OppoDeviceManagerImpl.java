package org.windning.safearea;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.Nullable;

class OppoDeviceManagerImpl implements IDeviceManager {

    @Override
    public boolean isNotch(Activity activity) {
        try{
            return activity.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        }catch(Exception ignore) {
            return false;
        }
    }

    @Nullable
    @Override
    public Rect getSafeRect(Activity activity) {
        if(activity == null) {
            return null;
        }
        boolean isNotchScreen = activity.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        /* OPPO has a fixed notch size with height 80 pixels */
        int top = 0;
        if(isNotchScreen) {
            top = 80;
        }
        return new Rect(0, top, 0, top);
    }

    @Override
    public void initWindowLayout(Activity activity, boolean enableNotch) {
        SafeAreaUtils.initWindowLayoutVivoAndOppo(activity, enableNotch);
    }
}