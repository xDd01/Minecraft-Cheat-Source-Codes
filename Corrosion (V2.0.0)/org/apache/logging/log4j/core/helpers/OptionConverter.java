/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.helpers;

import java.util.Locale;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.helpers.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class OptionConverter {
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final String DELIM_START = "${";
    private static final char DELIM_STOP = '}';
    private static final int DELIM_START_LEN = 2;
    private static final int DELIM_STOP_LEN = 1;
    private static final int ONE_K = 1024;

    private OptionConverter() {
    }

    public static String[] concatenateArrays(String[] l2, String[] r2) {
        int len = l2.length + r2.length;
        String[] a2 = new String[len];
        System.arraycopy(l2, 0, a2, 0, l2.length);
        System.arraycopy(r2, 0, a2, l2.length, r2.length);
        return a2;
    }

    public static String convertSpecialChars(String s2) {
        int len = s2.length();
        StringBuilder sbuf = new StringBuilder(len);
        int i2 = 0;
        while (i2 < len) {
            int c2;
            if ((c2 = s2.charAt(i2++)) == 92) {
                if ((c2 = s2.charAt(i2++)) == 110) {
                    c2 = 10;
                } else if (c2 == 114) {
                    c2 = 13;
                } else if (c2 == 116) {
                    c2 = 9;
                } else if (c2 == 102) {
                    c2 = 12;
                } else if (c2 == 8) {
                    c2 = 8;
                } else if (c2 == 34) {
                    c2 = 34;
                } else if (c2 == 39) {
                    c2 = 39;
                } else if (c2 == 92) {
                    c2 = 92;
                }
            }
            sbuf.append((char)c2);
        }
        return sbuf.toString();
    }

    public static Object instantiateByKey(Properties props, String key, Class<?> superClass, Object defaultValue) {
        String className = OptionConverter.findAndSubst(key, props);
        if (className == null) {
            LOGGER.error("Could not find value for key " + key);
            return defaultValue;
        }
        return OptionConverter.instantiateByClassName(className.trim(), superClass, defaultValue);
    }

    public static boolean toBoolean(String value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        String trimmedVal = value.trim();
        if ("true".equalsIgnoreCase(trimmedVal)) {
            return true;
        }
        if ("false".equalsIgnoreCase(trimmedVal)) {
            return false;
        }
        return defaultValue;
    }

    public static int toInt(String value, int defaultValue) {
        if (value != null) {
            String s2 = value.trim();
            try {
                return Integer.parseInt(s2);
            }
            catch (NumberFormatException e2) {
                LOGGER.error("[" + s2 + "] is not in proper int form.");
                e2.printStackTrace();
            }
        }
        return defaultValue;
    }

    public static long toFileSize(String value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        String str = value.trim().toUpperCase(Locale.ENGLISH);
        long multiplier = 1L;
        int index = str.indexOf("KB");
        if (index != -1) {
            multiplier = 1024L;
            str = str.substring(0, index);
        } else {
            index = str.indexOf("MB");
            if (index != -1) {
                multiplier = 0x100000L;
                str = str.substring(0, index);
            } else {
                index = str.indexOf("GB");
                if (index != -1) {
                    multiplier = 0x40000000L;
                    str = str.substring(0, index);
                }
            }
        }
        if (str != null) {
            try {
                return Long.parseLong(str) * multiplier;
            }
            catch (NumberFormatException e2) {
                LOGGER.error("[" + str + "] is not in proper int form.");
                LOGGER.error("[" + value + "] not in expected format.", (Throwable)e2);
            }
        }
        return defaultValue;
    }

    public static String findAndSubst(String key, Properties props) {
        String value = props.getProperty(key);
        if (value == null) {
            return null;
        }
        try {
            return OptionConverter.substVars(value, props);
        }
        catch (IllegalArgumentException e2) {
            LOGGER.error("Bad option value [" + value + "].", (Throwable)e2);
            return value;
        }
    }

    public static Object instantiateByClassName(String className, Class<?> superClass, Object defaultValue) {
        if (className != null) {
            try {
                Class<?> classObj = Loader.loadClass(className);
                if (!superClass.isAssignableFrom(classObj)) {
                    LOGGER.error("A \"" + className + "\" object is not assignable to a \"" + superClass.getName() + "\" variable.");
                    LOGGER.error("The class \"" + superClass.getName() + "\" was loaded by ");
                    LOGGER.error("[" + superClass.getClassLoader() + "] whereas object of type ");
                    LOGGER.error("\"" + classObj.getName() + "\" was loaded by [" + classObj.getClassLoader() + "].");
                    return defaultValue;
                }
                return classObj.newInstance();
            }
            catch (ClassNotFoundException e2) {
                LOGGER.error("Could not instantiate class [" + className + "].", (Throwable)e2);
            }
            catch (IllegalAccessException e3) {
                LOGGER.error("Could not instantiate class [" + className + "].", (Throwable)e3);
            }
            catch (InstantiationException e4) {
                LOGGER.error("Could not instantiate class [" + className + "].", (Throwable)e4);
            }
            catch (RuntimeException e5) {
                LOGGER.error("Could not instantiate class [" + className + "].", (Throwable)e5);
            }
        }
        return defaultValue;
    }

    public static String substVars(String val, Properties props) throws IllegalArgumentException {
        StringBuilder sbuf = new StringBuilder();
        int i2 = 0;
        while (true) {
            int j2;
            if ((j2 = val.indexOf(DELIM_START, i2)) == -1) {
                if (i2 == 0) {
                    return val;
                }
                sbuf.append(val.substring(i2, val.length()));
                return sbuf.toString();
            }
            sbuf.append(val.substring(i2, j2));
            int k2 = val.indexOf(125, j2);
            if (k2 == -1) {
                throw new IllegalArgumentException('\"' + val + "\" has no closing brace. Opening brace at position " + j2 + '.');
            }
            String key = val.substring(j2 += 2, k2);
            String replacement = PropertiesUtil.getProperties().getStringProperty(key, null);
            if (replacement == null && props != null) {
                replacement = props.getProperty(key);
            }
            if (replacement != null) {
                String recursiveReplacement = OptionConverter.substVars(replacement, props);
                sbuf.append(recursiveReplacement);
            }
            i2 = k2 + 1;
        }
    }
}

