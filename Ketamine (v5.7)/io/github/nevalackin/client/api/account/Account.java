package io.github.nevalackin.client.api.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.UserType;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import io.github.nevalackin.client.api.config.Serializable;
import io.github.nevalackin.client.api.notification.NotificationType;
import io.github.nevalackin.client.impl.core.KetamineClient;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public final class Account {

    private String username;

    private final String email;
    private final String password;

    private AccountType type;

    private long unbanTime;
    private boolean working;

    Account(final String username, final String email,
                   final String password, final AccountType type,
                   final long unbanTime, final boolean working) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.type = type;
        this.working = working;
        this.unbanTime = unbanTime;
    }

    public void createLoginThread() {
        final Thread altLoginThread = new Thread(() -> {
            // TODO :: Notifications

            if (this.getType() != AccountType.OFFLINE) {
                final UserAuthentication authentication = new YggdrasilAuthenticationService(
                    Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);

                authentication.setUsername(this.getEmail());
                authentication.setPassword(this.getPassword());

                KetamineClient.getInstance().getNotificationManager().add(NotificationType.INFO,
                                                                          "Logging In",
                                                                          String.format("Attempting to login to %s.", this.getEmail()),
                                                                          1000L);

                try {
                    authentication.logIn();
                } catch (final AuthenticationException e) {
                    KetamineClient.getInstance().getNotificationManager().add(NotificationType.ERROR,
                                                                              "Invalid Credentials",
                                                                              String.format("Invalid credentials for account %s.", this.getUsername()),
                                                                              1500L);

                    if (this.getUsername().length() == 0) {
                        this.setUsername("invalid_account");
                    }

                    this.setWorking(false);
                    return;
                }


                final String username = authentication.getSelectedProfile().getName();
                this.setUsername(username);
                this.setWorking(true);

                KetamineClient.getInstance().getNotificationManager().add(NotificationType.SUCCESS,
                                                                          "Logged In",
                                                                          String.format("Successfully logged into %s.", username),
                                                                          1500L);

                Minecraft.getMinecraft().setSession(new Session(username,
                                                                UUIDTypeAdapter.fromUUID(authentication.getSelectedProfile().getId()),
                                                                authentication.getAuthenticatedToken(),
                                                                authentication.getUserType().getName()));
            } else {
                this.setUsername(this.getEmail());

                final GameProfile profile = new GameProfile(UUID.randomUUID(), this.getUsername());

                Minecraft.getMinecraft().setSession(new Session(profile.getName(),
                                                                UUIDTypeAdapter.fromUUID(profile.getId()),
                                                                "", UserType.MOJANG.getName()));
            }
        });

        altLoginThread.start();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public AccountType getType() {
        return type;
    }

    public long getUnbanDate() {
        return unbanTime;
    }

    public void setUnbanDate(final long unbanDate) {
        this.unbanTime = unbanDate;
    }

    public boolean isBanned() {
        return this.unbanTime == -1337 || System.currentTimeMillis() < this.unbanTime;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public JsonObject save() {
        final JsonObject object = new JsonObject();

        object.addProperty("email", this.email);
        object.addProperty("pass", this.password);

        if (this.username != null)
            object.addProperty("user", this.username);

        object.addProperty("working", this.working);
        object.addProperty("unban_time", this.unbanTime);

        return object;
    }

    public static Account from(JsonObject object) {
        final AccountBuilder builder = new AccountBuilder();

        if (object.has("email"))
            builder.email(object.get("email").getAsString());

        if (object.has("pass"))
            builder.password(object.get("pass").getAsString());

        if (object.has("user"))
            builder.username(object.get("user").getAsString());

        if (object.has("working"))
            builder.working(object.get("working").getAsBoolean());

        if (object.has("unban_time"))
            builder.unbanTime(object.get("unban_time").getAsLong());

        return builder.build();
    }

    public static AccountBuilder builder() {
        return new AccountBuilder();
    }

    public static class AccountBuilder {
        private String username = "", email, password = "";
        private AccountType type = AccountType.MOJANG;
        private long unbanTime;
        private boolean working;

        private AccountBuilder() {
        }

        public AccountBuilder username(final String username) {
            this.username = username;

            return this;
        }

        public AccountBuilder email(final String email) {
            this.email = email;

            return this;
        }

        public AccountBuilder password(final String password) {
            this.password = password;

            return this;
        }

        public AccountBuilder unbanTime(final long unbanTime) {
            this.unbanTime = unbanTime;

            return this;
        }

        public AccountBuilder working(final boolean working) {
            this.working = working;

            return this;
        }

        public Account build() {
            if (this.password.length() == 0) {
                this.type = AccountType.OFFLINE;
            }

            return new Account(username, email, password, type, unbanTime, working);
        }
    }
}
