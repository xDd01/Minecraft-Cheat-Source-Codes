/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu;

import cafe.corrosion.menu.GuiAltManager;
import cafe.corrosion.util.session.SessionResult;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public class LoginThread
extends Thread {
    private final String password;
    private final String username;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final GuiAltManager altManager;
    private String status;

    public LoginThread(String username, String password, GuiAltManager altManager) {
        super("Alt Login Thread");
        this.username = username;
        this.password = password;
        this.altManager = altManager;
        this.status = (Object)((Object)EnumChatFormatting.GRAY) + "Waiting";
    }

    private SessionResult createSession(String username, String password) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            Session session;
            auth.logIn();
            this.mc.session = session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
            this.altManager.onAccountLoad(auth.getSelectedProfile().getName(), username, password);
            return new SessionResult((Object)((Object)EnumChatFormatting.GREEN) + "Successfully logged into " + session.getUsername() + "!", session);
        }
        catch (Exception exception) {
            return new SessionResult((Object)((Object)EnumChatFormatting.RED) + "Invalid credentials!", null);
        }
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public void run() {
        if (this.password.equals("")) {
            this.mc.session = new Session(this.username.replace("&", "\u00a7"), "", "", "mojang");
            this.status = (Object)((Object)EnumChatFormatting.GREEN) + "Set username to " + this.username;
            return;
        }
        this.status = (Object)((Object)EnumChatFormatting.AQUA) + "Authenticating...";
        SessionResult auth = this.createSession(this.username, this.password);
        this.status = auth.getResponseMessage();
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

