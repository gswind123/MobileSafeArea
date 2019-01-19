package org.windning.safearea;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Method;

class RomUtil {
    private Class m_clsSystemProperty;
    private Method m_getSystemProperty;

    public RomUtil(@NonNull Context context) {
        ClassLoader clsLoader = context.getClassLoader();
        try{
            m_clsSystemProperty = clsLoader.loadClass("android.os.SystemProperty");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            m_getSystemProperty = m_clsSystemProperty.getMethod("get", paramTypes);
        } catch(ClassNotFoundException ignore) {
            Log.e("RomUtil", "SystemProperty not found");
        } catch(NoSuchMethodException ignore) {
            Log.e("RomUtil", "SystemProperty#get method not found");
        }
    }

    private boolean checkValidation() {
        return m_clsSystemProperty != null && m_getSystemProperty != null;
    }

    public boolean isHuaweiRom() {
        String manuf = Build.MANUFACTURER;
        return manuf != null && manuf.contains("HUAWEI");
    }

    public boolean isXiaomi() {
        String prop = getSystemProperty("ro.miui.ui.version.name");
        return prop != null && prop.length() > 0;
    }

    public boolean isOppo() {
        String brand = getSystemProperty("ro.product.brand");
        return brand != null && brand.toLowerCase().contains("oppo");
    }

    public boolean isVivo() {
        String name = getSystemProperty("ro.vivo.os.name");
        return name != null && name.toLowerCase().contains("funtouch");
    }

    private String getSystemProperty(String name) {
        if(!checkValidation()) {
           return null;
        }
        try{
            return (String)m_getSystemProperty.invoke(m_clsSystemProperty, name);
        }catch(Exception e) {
            return null;
        }
    }
}
