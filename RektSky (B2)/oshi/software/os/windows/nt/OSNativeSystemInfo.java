package oshi.software.os.windows.nt;

import com.sun.jna.ptr.*;
import com.sun.jna.platform.win32.*;

public class OSNativeSystemInfo
{
    private WinBase.SYSTEM_INFO _si;
    
    public OSNativeSystemInfo() {
        this._si = null;
        final WinBase.SYSTEM_INFO si = new WinBase.SYSTEM_INFO();
        Kernel32.INSTANCE.GetSystemInfo(si);
        try {
            final IntByReference isWow64 = new IntByReference();
            final WinNT.HANDLE hProcess = Kernel32.INSTANCE.GetCurrentProcess();
            if (Kernel32.INSTANCE.IsWow64Process(hProcess, isWow64) && isWow64.getValue() > 0) {
                Kernel32.INSTANCE.GetNativeSystemInfo(si);
            }
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {}
        this._si = si;
    }
    
    public OSNativeSystemInfo(final WinBase.SYSTEM_INFO si) {
        this._si = null;
        this._si = si;
    }
    
    public int getNumberOfProcessors() {
        return this._si.dwNumberOfProcessors.intValue();
    }
}
