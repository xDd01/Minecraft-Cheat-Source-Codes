/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.account.impl;

import cafe.corrosion.util.account.AltAccountType;
import cafe.corrosion.util.json.JsonChain;
import com.google.gson.JsonObject;

public class AltAccount {
    private final String name;
    private final String email;
    private final String password;
    private final AltAccountType accountType;

    public AltAccount(JsonObject object) {
        this.name = object.get("name").getAsString();
        this.email = object.get("email").getAsString();
        this.password = object.get("password").getAsString();
        this.accountType = AltAccountType.valueOf(object.get("type").getAsString().toUpperCase());
    }

    public JsonObject serialize() {
        return new JsonChain().addProperty("name", this.name).addProperty("email", this.email).addProperty("password", this.password).addProperty("type", this.accountType.getName()).getJsonObject();
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public AltAccountType getAccountType() {
        return this.accountType;
    }

    public AltAccount(String name, String email, String password, AltAccountType accountType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }
}

