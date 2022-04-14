/*
 * Decompiled with CFR 0.152.
 */
package org.yaml.snakeyaml.env;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class EnvScalarConstructor
extends Constructor {
    public static final Tag ENV_TAG = new Tag("!ENV");
    public static final Pattern ENV_FORMAT = Pattern.compile("^\\$\\{\\s*((?<name>\\w+)((?<separator>:?(-|\\?))(?<value>\\w+)?)?)\\s*\\}$");

    public EnvScalarConstructor() {
        this.yamlConstructors.put(ENV_TAG, new ConstructEnv());
    }

    public String apply(String name, String separator, String value, String environment) {
        if (environment != null && !environment.isEmpty()) {
            return environment;
        }
        if (separator == null) return "";
        if (separator.equals("?") && environment == null) {
            throw new MissingEnvironmentVariableException("Missing mandatory variable " + name + ": " + value);
        }
        if (separator.equals(":?")) {
            if (environment == null) {
                throw new MissingEnvironmentVariableException("Missing mandatory variable " + name + ": " + value);
            }
            if (environment.isEmpty()) {
                throw new MissingEnvironmentVariableException("Empty mandatory variable " + name + ": " + value);
            }
        }
        if (separator.startsWith(":")) {
            if (environment == null) return value;
            if (!environment.isEmpty()) return "";
            return value;
        }
        if (environment != null) return "";
        return value;
    }

    public String getEnv(String key) {
        return System.getenv(key);
    }

    private class ConstructEnv
    extends AbstractConstruct {
        private ConstructEnv() {
        }

        @Override
        public Object construct(Node node) {
            String string;
            String val = EnvScalarConstructor.this.constructScalar((ScalarNode)node);
            Matcher matcher = ENV_FORMAT.matcher(val);
            matcher.matches();
            String name = matcher.group("name");
            String value = matcher.group("value");
            String separator = matcher.group("separator");
            if (value != null) {
                string = value;
                return EnvScalarConstructor.this.apply(name, separator, string, EnvScalarConstructor.this.getEnv(name));
            }
            string = "";
            return EnvScalarConstructor.this.apply(name, separator, string, EnvScalarConstructor.this.getEnv(name));
        }
    }
}
