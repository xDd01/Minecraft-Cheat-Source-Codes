package oshi;

import oshi.software.os.*;
import oshi.hardware.*;
import com.sun.jna.*;
import oshi.software.os.windows.*;
import oshi.software.os.linux.*;
import oshi.software.os.mac.*;

public class SystemInfo
{
    private OperatingSystem _os;
    private HardwareAbstractionLayer _hardware;
    private PlatformEnum currentPlatformEnum;
    
    public SystemInfo() {
        this._os = null;
        this._hardware = null;
        if (Platform.isWindows()) {
            this.currentPlatformEnum = PlatformEnum.WINDOWS;
        }
        else if (Platform.isLinux()) {
            this.currentPlatformEnum = PlatformEnum.LINUX;
        }
        else if (Platform.isMac()) {
            this.currentPlatformEnum = PlatformEnum.MACOSX;
        }
        else {
            this.currentPlatformEnum = PlatformEnum.UNKNOWN;
        }
    }
    
    public OperatingSystem getOperatingSystem() {
        if (this._os == null) {
            switch (this.currentPlatformEnum) {
                case WINDOWS: {
                    this._os = new WindowsOperatingSystem();
                    break;
                }
                case LINUX: {
                    this._os = new LinuxOperatingSystem();
                    break;
                }
                case MACOSX: {
                    this._os = new MacOperatingSystem();
                    break;
                }
                default: {
                    throw new RuntimeException("Operating system not supported: " + Platform.getOSType());
                }
            }
        }
        return this._os;
    }
    
    public HardwareAbstractionLayer getHardware() {
        if (this._hardware == null) {
            switch (this.currentPlatformEnum) {
                case WINDOWS: {
                    this._hardware = new WindowsHardwareAbstractionLayer();
                    break;
                }
                case LINUX: {
                    this._hardware = new LinuxHardwareAbstractionLayer();
                    break;
                }
                case MACOSX: {
                    this._hardware = new MacHardwareAbstractionLayer();
                    break;
                }
                default: {
                    throw new RuntimeException("Operating system not supported: " + Platform.getOSType());
                }
            }
        }
        return this._hardware;
    }
}
