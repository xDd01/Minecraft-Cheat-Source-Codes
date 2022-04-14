package me.spec.eris.api.config;

import java.io.File;

import me.spec.eris.Eris;

/**
 * Author: Ice
 * Created: 20:19, 11-Jun-20
 * Project: Client
 */
public class ClientConfig {

    private String configName;
    private File configFile;

    public ClientConfig(String configName) {
        this.configName = configName;
        this.configFile = new File(Eris.INSTANCE.fileManager.configDir, this.getConfigName() + ".eriscnf");
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }
}