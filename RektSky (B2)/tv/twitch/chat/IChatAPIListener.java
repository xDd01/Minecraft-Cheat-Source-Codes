package tv.twitch.chat;

import tv.twitch.*;

public interface IChatAPIListener
{
    void chatInitializationCallback(final ErrorCode p0);
    
    void chatShutdownCallback(final ErrorCode p0);
    
    void chatEmoticonDataDownloadCallback(final ErrorCode p0);
}
