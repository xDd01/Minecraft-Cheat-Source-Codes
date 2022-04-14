/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WinNT;

public class Win32Exception
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private WinNT.HRESULT _hr;

    public WinNT.HRESULT getHR() {
        return this._hr;
    }

    public Win32Exception(WinNT.HRESULT hr2) {
        super(Kernel32Util.formatMessageFromHR(hr2));
        this._hr = hr2;
    }

    public Win32Exception(int code) {
        this(W32Errors.HRESULT_FROM_WIN32(code));
    }
}

