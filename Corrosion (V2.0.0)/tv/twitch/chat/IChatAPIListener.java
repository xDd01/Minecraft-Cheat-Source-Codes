/*
 * Decompiled with CFR 0.152.
 */
package tv.twitch.chat;

import tv.twitch.ErrorCode;

public interface IChatAPIListener {
    public void chatInitializationCallback(ErrorCode var1);

    public void chatShutdownCallback(ErrorCode var1);

    public void chatEmoticonDataDownloadCallback(ErrorCode var1);
}

