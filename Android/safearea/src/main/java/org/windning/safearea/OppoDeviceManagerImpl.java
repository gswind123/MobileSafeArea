package org.windning.safearea;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.Nullable;

class OppoDeviceManagerImpl extends BaseDeviceManager {

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
        /* Use OPPO API to check the detail notch size*/
        if(isNotch(activity)) {
            /* Parse portrait screen size */
            Rect screenRect = SafeAreaUtils.getPortraitScreenSize(activity);
            /* Parse device notch portrait rect. By default it's a rect with height 80 pixels */
            Rect notch = new Rect(0, 80, screenRect.width(), 0);
            try {
                /* If the property is achievable, it should be like "444,0:636,76", which means : left,top:right,bottom */
                String property = getRomUtil().getSystemProperty("ro.oppo.screen.heteromorphism");
                int left = 0, top = 0, right = 0, bottom = 0;
                String[] points = property.split(":");
                if(points.length != 2) {
                    throw new IllegalArgumentException();
                }
                String[] leftTops = points[0].split(",");
                String[] rightBottoms = points[1].split(",");
                if(leftTops.length != 2 || rightBottoms.length != 2) {
                    throw new IllegalArgumentException();
                }
                left = Integer.parseInt(leftTops[0]);
                top = Integer.parseInt(leftTops[1]);
                right = Integer.parseInt(rightBottoms[0]);
                bottom = Integer.parseInt(rightBottoms[1]);
                notch = new Rect(left, top, right, bottom);
                if(!SafeAreaUtils.isVitalNotch(notch, screenRect)) {
                    notch = new Rect(0, 0, 0,0);
                }
            } catch (Exception ignore) { }
            return new Rect(0, notch.height(), 0, notch.height());
        } else {
            return new Rect(0, 0, 0, 0);
        }
    }

    @Override
    public void initWindowLayout(Activity activity, boolean enableNotch) {
        SafeAreaUtils.initWindowLayoutOppoAndVivo(activity, enableNotch);
    }
}