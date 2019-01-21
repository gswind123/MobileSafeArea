package org.windning.safearea;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.Nullable;

import java.lang.reflect.Method;

class MiDeviceManagerImpl implements IDeviceManager {
    private static final int FLAG_NOTCH_IMMERSIVE = 0x00000100;
    private static final int FLAG_NOTCH_PORTRAIT = 0x00000200;
    private static final int FLAG_NOTCH_LANDSCAPE = 0x00000400;

    @Override
    public boolean isNotch(Activity activity) {
        try{
            RomUtil romUtil = SafeAreaController.getRomUtil();
            return "1".equals(romUtil.getSystemProperty("ro.miui.notch"));
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
        int top = 0;
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resId > 0) {
            top = activity.getResources().getDimensionPixelSize(resId);
        }
        return new Rect(0, top, 0, top/* bottom is the same with top */);
    }

    @Override
    public void initWindowLayout(Activity activity, boolean enableNotch) {
        int flag = FLAG_NOTCH_IMMERSIVE | FLAG_NOTCH_PORTRAIT | FLAG_NOTCH_LANDSCAPE;
        try{
            Method method = null;
            if(enableNotch) {
                method = activity.getWindow().getClass().getMethod("addExtraFlags", int.class);
            } else {
                method = activity.getWindow().getClass().getMethod("clearExtraFlags", int.class);
            }
            if(method != null) {
                method.setAccessible(true);
                method.invoke(activity.getWindow(), flag);
            }
        } catch(Exception ignore){}
    }
}