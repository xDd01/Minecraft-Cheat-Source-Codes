/*
 * Decompiled with CFR 0.152.
 */
package optfine;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import optfine.IFileDownloadListener;
import optfine.PlayerConfiguration;
import optfine.PlayerConfigurationParser;
import optfine.PlayerConfigurations;

public class PlayerConfigurationReceiver
implements IFileDownloadListener {
    private String player = null;

    public PlayerConfigurationReceiver(String p_i48_1_) {
        this.player = p_i48_1_;
    }

    @Override
    public void fileDownloadFinished(String p_fileDownloadFinished_1_, byte[] p_fileDownloadFinished_2_, Throwable p_fileDownloadFinished_3_) {
        if (p_fileDownloadFinished_2_ == null) return;
        try {
            String s = new String(p_fileDownloadFinished_2_, "ASCII");
            JsonParser jsonparser = new JsonParser();
            JsonElement jsonelement = jsonparser.parse(s);
            PlayerConfigurationParser playerconfigurationparser = new PlayerConfigurationParser(this.player);
            PlayerConfiguration playerconfiguration = playerconfigurationparser.parsePlayerConfiguration(jsonelement);
            if (playerconfiguration == null) return;
            playerconfiguration.setInitialized(true);
            PlayerConfigurations.setPlayerConfiguration(this.player, playerconfiguration);
            return;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

