package org.windning.safearea;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class HwDeviceManagerImpl implements IDeviceManager {
    private static final int HW_FLAG_NOTCH_SUPPORT = 0x00010000;

    private Class m_hwNotchSizeUtil;
    private Method m_getNotchSize;

    @Nullable
    @Override
    public Rect getSafeRect(Activity activity) {
        if(activity == null) {
            return null;
        }
        try{
            if(m_hwNotchSizeUtil == null) {
                m_hwNotchSizeUtil = activity.getClassLoader().loadClass("com.huawei.android.util.HwNotchSizeUtil");
            }
            if(m_getNotchSize != null) {
                m_getNotchSize = m_hwNotchSizeUtil.getMethod("getNotchSize");
            }
            int[] ret = (int[]) m_getNotchSize.invoke(null);
            return new Rect(0, ret[1], 0, 0);
        }catch(Exception ignore) {
            return new Rect(0,0,0,0); // Huawei util not valid, ingore this
        }
    }

    @Override
    public void initWindowLayout(Activity activity, boolean enableNotch) {
        if(activity == null) {
            return ;
        }
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        try{
            Field field = lp.getClass().getDeclaredField("hwFlags");
            field.setAccessible(true);
            if(enableNotch) {
                field.set(lp, HW_FLAG_NOTCH_SUPPORT);
            }
        }catch(Exception ignore) {}
    }
}
