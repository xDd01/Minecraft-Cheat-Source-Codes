/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.NtDll;
import com.sun.jna.platform.win32.Wdm;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;

public abstract class NtDllUtil {
    public static String getKeyName(WinReg.HKEY hkey) {
        IntByReference resultLength = new IntByReference();
        int rc2 = NtDll.INSTANCE.ZwQueryKey(hkey, 0, null, 0, resultLength);
        if (rc2 != -1073741789 || resultLength.getValue() <= 0) {
            throw new Win32Exception(rc2);
        }
        Wdm.KEY_BASIC_INFORMATION keyInformation = new Wdm.KEY_BASIC_INFORMATION(resultLength.getValue());
        rc2 = NtDll.INSTANCE.ZwQueryKey(hkey, 0, keyInformation, resultLength.getValue(), resultLength);
        if (rc2 != 0) {
            throw new Win32Exception(rc2);
        }
        return keyInformation.getName();
    }
}

