package org.windning.safearea;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.lang.reflect.Method;

class VivoDeviceManagerImpl extends BaseDeviceManager {
    private static final int VIVO_HAS_NOTCH_DISPLAY = 0x00000020;

    private Class m_fitFeature;
    private Method m_getFeatureSupport;

    @Override
    public boolean isNotch(Activity activity) {
        return false; // vivo doesn't support notch full screen on non android P devices
    }

    @Nullable
    @Override
    public Rect getSafeRect(Activity activity) {
        return new Rect(0, 0, 0, 0); // vivo doesn't support notch full screen on non android P devices
    }

    @Override
    public void initWindowLayout(Activity activity, boolean enableNotch) {
        SafeAreaUtils.initWindowLayoutOppoAndVivo(activity, enableNotch);
    }

    private boolean isNotchScreen(Context context) {
        try{
            if(m_fitFeature == null) {
                m_fitFeature = context.getClassLoader().loadClass("android.util.FtFeature");
            }
            if(m_getFeatureSupport == null) {
                m_getFeatureSupport = m_fitFeature.getMethod("isFeatureSupport", int.class);
            }
            m_getFeatureSupport.setAccessible(true);
            return (Boolean)m_getFeatureSupport.invoke(m_fitFeature, VIVO_HAS_NOTCH_DISPLAY);
        }catch(Exception ignore) {
            return false;
        }
    }

    private Rect DEPRECATE_getSafeRect(Activity activity) {
        if(activity == null) {
            return null;
        }
        /* VIVO has a fixed notch size with height 27DP */
        int top = 0;
        if(isNotchScreen((activity))) {
            DisplayMetrics dm = activity.getResources().getDisplayMetrics();
            if(dm != null) {
                top = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27f, dm));
            }
        }
        return new Rect(0, top, 0, top /* Apply the same bottom with top */);
    }

}