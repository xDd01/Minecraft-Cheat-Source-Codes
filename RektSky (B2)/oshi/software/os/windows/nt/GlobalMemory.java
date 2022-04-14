package oshi.software.os.windows.nt;

import oshi.hardware.*;
import com.sun.jna.platform.win32.*;

public class GlobalMemory implements Memory
{
    WinBase.MEMORYSTATUSEX _memory;
    
    public GlobalMemory() {
        this._memory = new WinBase.MEMORYSTATUSEX();
        if (!Kernel32.INSTANCE.GlobalMemoryStatusEx(this._memory)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }
    
    public long getAvailable() {
        return this._memory.ullAvailPhys.longValue();
    }
    
    public long getTotal() {
        return this._memory.ullTotalPhys.longValue();
    }
}
