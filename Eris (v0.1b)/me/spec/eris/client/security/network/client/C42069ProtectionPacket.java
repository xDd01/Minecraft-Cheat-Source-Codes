package me.spec.eris.client.security.network.client;

import java.io.Serializable;

public class C42069ProtectionPacket implements Serializable {

    public int UID;
    public boolean ADMIN;
    public String AUTH_CODE = "";
    public String PASSWORD = "";
    public String HWID = "";
    public String NAME = "";
    public String ACTION = "";

}
