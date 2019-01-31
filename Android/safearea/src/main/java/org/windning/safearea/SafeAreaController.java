package org.windning.safearea;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Use @getSafeArea to achieve insets for the safe area
 * Note that this method needs an activity to be alive
 */
public class SafeAreaController {
    private static SafeAreaController s_inst;

    private IDeviceManager m_deviceMngr;
    private RomUtil m_romUtil;
    private ArrayList<String> m_nonRoundedDevices = new ArrayList<>();

    private SafeAreaController(Activity activity) {
        initDeviceManager(activity);
//        initSpecialDevices(activity); // Currently we don't need this
    }

    protected static RomUtil getRomUtil() {
        if(s_inst != null) {
            return s_inst.m_romUtil;
        } else {
            return null;
        }
    }

    private static SafeAreaController getInstance(Activity activity) {
        if(s_inst == null) {
            s_inst = new SafeAreaController(activity);
        }
        return s_inst;
    }

    public static void initWindowLayout(Activity act, boolean enableNotch) {
        SafeAreaController inst = getInstance(act);
        inst.m_deviceMngr.initWindowLayout(act, enableNotch);
    }

    /**
     * Return value would be a json :
     * {
     *      left: 20, //pixels
     *      right: 20,
     *      top : 0,
     *      bottom : 0
     * }
     * Each val means how much pixels insets should be applied to fit safe area
     * EMPTY ("") would be returned if safe area hasn't been available
     */
    public static String getSafeArea(Activity act) {
        SafeAreaController inst = getInstance(act);
        /**
         *  Practically if a device has notch screen, it should be a rounded corner one
         *  And we treat all rounded-corner devices with a fixed corner size
         */
        Rect safeRect = new Rect(0, 0, 0,0);
        if(inst.m_romUtil.isOldDeviceOrSimulator()) {
            // As with old devices or simulator, don't protect the rounded corners
        } else {
            safeRect = SafeAreaUtils.protectRoundCorner(inst.m_deviceMngr.isNotch(act));
        }
        if(safeRect == null) {
            return "";
        }
        safeRect = SafeAreaUtils.normalizeScreenRect(safeRect, act);
        JSONObject jsonRect = new JSONObject();
        try{
            jsonRect.put("left", safeRect.left);
            jsonRect.put("right", safeRect.right);
            jsonRect.put("top", safeRect.top);
            jsonRect.put("bottom", safeRect.bottom);
        }catch(JSONException ignore) {}
        return jsonRect.toString();
    }

    private void initDeviceManager(Activity activity) {
        if(m_romUtil == null) {
            m_romUtil = new RomUtil(activity);
        }
        /**
         * Select a suitable device manager for different platforms:
         * 1. As with android P, use methods provided by android SDK
         * 2. As with android before O, no notch screens would be encountered and use default
         * 3. As with other cases, use different impl depends on manufacturers
         */
        int sdkInt = Build.VERSION.SDK_INT;
        if(sdkInt >= Build.VERSION_CODES.P) {
            m_deviceMngr = new PDeviceManagerImpl();
        } else if (sdkInt < Build.VERSION_CODES.O) {
            m_deviceMngr = new DefaultDeviceManagerImpl();
        } else { // Select a manufacturer
            if(m_romUtil.isHuaweiRom()) {
                m_deviceMngr = new HwDeviceManagerImpl();
            } else if(m_romUtil.isXiaomi()) {
                m_deviceMngr = new MiDeviceManagerImpl();
            } else if(m_romUtil.isOppo()) {
                m_deviceMngr = new OppoDeviceManagerImpl();
            } else if(m_romUtil.isVivo()) {
                m_deviceMngr = new VivoDeviceManagerImpl();
            }
        }
        /* By default use the default device mgr */
        if(m_deviceMngr == null) {
            m_deviceMngr = new DefaultDeviceManagerImpl();
        }
    }

    private void legacy_InitSpecialDevices(Activity activity) {
        m_nonRoundedDevices.clear();
        InputStream input = activity.getResources().openRawResource(R.raw.non_round_corner_device_list);
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while((line = reader.readLine()) != null) {
                if(line.length() > 0) {
                    m_nonRoundedDevices.add(line.trim());
                }
            }
        }catch(Exception e) {
            m_nonRoundedDevices.clear();
            Log.e("SafeAreaController", "non round corner list unavailable");
        } finally {
            try{
                input.close();
            }catch(IOException ignore){}
        }
    }
}