/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WinNT;

public abstract class Ole32Util {
    public static Guid.GUID getGUIDFromString(String guidString) {
        Guid.GUID.ByReference lpiid = new Guid.GUID.ByReference();
        WinNT.HRESULT hr2 = Ole32.INSTANCE.IIDFromString(guidString, lpiid);
        if (!hr2.equals(W32Errors.S_OK)) {
            throw new RuntimeException(hr2.toString());
        }
        return lpiid;
    }

    public static String getStringFromGUID(Guid.GUID guid) {
        int max;
        char[] lpsz;
        Guid.GUID.ByReference pguid = new Guid.GUID.ByReference(guid.getPointer());
        int len = Ole32.INSTANCE.StringFromGUID2(pguid, lpsz = new char[max = 39], max);
        if (len == 0) {
            throw new RuntimeException("StringFromGUID2");
        }
        lpsz[len - 1] = '\u0000';
        return Native.toString(lpsz);
    }

    public static Guid.GUID generateGUID() {
        Guid.GUID.ByReference pguid = new Guid.GUID.ByReference();
        WinNT.HRESULT hr2 = Ole32.INSTANCE.CoCreateGuid(pguid);
        if (!hr2.equals(W32Errors.S_OK)) {
            throw new RuntimeException(hr2.toString());
        }
        return pguid;
    }
}

