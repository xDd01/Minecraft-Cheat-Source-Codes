/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.auth;

import java.security.Principal;

public interface Credentials {
    public Principal getUserPrincipal();

    public String getPassword();
}

