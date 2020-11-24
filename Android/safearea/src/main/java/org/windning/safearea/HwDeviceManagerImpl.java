package org.windning.safearea;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import java.lang.reflect.*;

class HwDeviceManagerImpl extends BaseDeviceManager {
    private static final int HW_FLAG_NOTCH_SUPPORT = 0x00010000;

    private Class m_hwNotchSizeUtil;
    private Method m_getNotchSize;
    private Method m_hasNotchInScreen;

    @Override
    public boolean isNotch(Activity activity) {
        if(activity == null) {
            return false;
        }
        try{
            if(m_hwNotchSizeUtil == null) {
                m_hwNotchSizeUtil = activity.getClassLoader().loadClass("com.huawei.android.util.HwNotchSizeUtil");
            }
            if(m_hasNotchInScreen == null) {
                m_hasNotchInScreen = m_hwNotchSizeUtil.getMethod("hasNotchInScreen");
            }
            return (Boolean) m_hasNotchInScreen.invoke(null);
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
        if(!isNotch(activity)) {
            return new Rect(0, 0, 0, 0);
        }
        try{
            if(m_hwNotchSizeUtil == null) {
                m_hwNotchSizeUtil = activity.getClassLoader().loadClass("com.huawei.android.util.HwNotchSizeUtil");
            }
            if(m_getNotchSize == null) {
                m_getNotchSize = m_hwNotchSizeUtil.getMethod("getNotchSize");
            }
            int[] ret = (int[]) m_getNotchSize.invoke(null);
            Rect notch = new Rect(0, 0, ret[0], ret[1]);
            Rect screenRect = SafeAreaUtils.getPortraitScreenSize(activity);
            if(!SafeAreaUtils.isVitalNotch(notch, screenRect)) {
                notch = new Rect(0, 0, 0, 0);
            }
            return new Rect(0, notch.height(), 0, notch.height());
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
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(lp);
            Method method = layoutParamsExCls.getMethod(enableNotch ? "addHwFlags" : "clearHwFlags", int.class);
            method.invoke(layoutParamsExObj, HW_FLAG_NOTCH_SUPPORT);
        } catch(ClassNotFoundException ignore) {
            // not notch device, ignore this
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
