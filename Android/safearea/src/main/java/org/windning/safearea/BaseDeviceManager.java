package org.windning.safearea;

public abstract class BaseDeviceManager implements IDeviceManager {
    private RomUtil m_romUtil;

    public void setRomUtil(RomUtil romUtil) {
        m_romUtil = romUtil;
    }

    protected RomUtil getRomUtil() {
        return m_romUtil;
    }
}
