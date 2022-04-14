/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.duration.impl;

import com.ibm.icu.impl.duration.impl.RecordWriter;
import com.ibm.icu.lang.UCharacter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class XMLRecordWriter
implements RecordWriter {
    private Writer w;
    private List<String> nameStack;
    static final String NULL_NAME = "Null";
    private static final String INDENT = "    ";

    public XMLRecordWriter(Writer w2) {
        this.w = w2;
        this.nameStack = new ArrayList<String>();
    }

    public boolean open(String title) {
        this.newline();
        this.writeString("<" + title + ">");
        this.nameStack.add(title);
        return true;
    }

    public boolean close() {
        int ix2 = this.nameStack.size() - 1;
        if (ix2 >= 0) {
            String name = this.nameStack.remove(ix2);
            this.newline();
            this.writeString("</" + name + ">");
            return true;
        }
        return false;
    }

    public void flush() {
        try {
            this.w.flush();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void bool(String name, boolean value) {
        this.internalString(name, String.valueOf(value));
    }

    public void boolArray(String name, boolean[] values) {
        if (values != null) {
            String[] stringValues = new String[values.length];
            for (int i2 = 0; i2 < values.length; ++i2) {
                stringValues[i2] = String.valueOf(values[i2]);
            }
            this.stringArray(name, stringValues);
        }
    }

    private static String ctos(char value) {
        if (value == '<') {
            return "&lt;";
        }
        if (value == '&') {
            return "&amp;";
        }
        return String.valueOf(value);
    }

    public void character(String name, char value) {
        if (value != '\uffff') {
            this.internalString(name, XMLRecordWriter.ctos(value));
        }
    }

    public void characterArray(String name, char[] values) {
        if (values != null) {
            String[] stringValues = new String[values.length];
            for (int i2 = 0; i2 < values.length; ++i2) {
                char value = values[i2];
                stringValues[i2] = value == '\uffff' ? NULL_NAME : XMLRecordWriter.ctos(value);
            }
            this.internalStringArray(name, stringValues);
        }
    }

    public void namedIndex(String name, String[] names, int value) {
        if (value >= 0) {
            this.internalString(name, names[value]);
        }
    }

    public void namedIndexArray(String name, String[] names, byte[] values) {
        if (values != null) {
            String[] stringValues = new String[values.length];
            for (int i2 = 0; i2 < values.length; ++i2) {
                byte value = values[i2];
                stringValues[i2] = value < 0 ? NULL_NAME : names[value];
            }
            this.internalStringArray(name, stringValues);
        }
    }

    public static String normalize(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb2 = null;
        boolean inWhitespace = false;
        char c2 = '\u0000';
        boolean special = false;
        for (int i2 = 0; i2 < str.length(); ++i2) {
            c2 = str.charAt(i2);
            if (UCharacter.isWhitespace(c2)) {
                if (sb2 == null && (inWhitespace || c2 != ' ')) {
                    sb2 = new StringBuilder(str.substring(0, i2));
                }
                if (inWhitespace) continue;
                inWhitespace = true;
                special = false;
                c2 = ' ';
            } else {
                inWhitespace = false;
                boolean bl2 = special = c2 == '<' || c2 == '&';
                if (special && sb2 == null) {
                    sb2 = new StringBuilder(str.substring(0, i2));
                }
            }
            if (sb2 == null) continue;
            if (special) {
                sb2.append(c2 == '<' ? "&lt;" : "&amp;");
                continue;
            }
            sb2.append(c2);
        }
        if (sb2 != null) {
            return sb2.toString();
        }
        return str;
    }

    private void internalString(String name, String normalizedValue) {
        if (normalizedValue != null) {
            this.newline();
            this.writeString("<" + name + ">" + normalizedValue + "</" + name + ">");
        }
    }

    private void internalStringArray(String name, String[] normalizedValues) {
        if (normalizedValues != null) {
            this.push(name + "List");
            for (int i2 = 0; i2 < normalizedValues.length; ++i2) {
                String value = normalizedValues[i2];
                if (value == null) {
                    value = NULL_NAME;
                }
                this.string(name, value);
            }
            this.pop();
        }
    }

    public void string(String name, String value) {
        this.internalString(name, XMLRecordWriter.normalize(value));
    }

    public void stringArray(String name, String[] values) {
        if (values != null) {
            this.push(name + "List");
            for (int i2 = 0; i2 < values.length; ++i2) {
                String value = XMLRecordWriter.normalize(values[i2]);
                if (value == null) {
                    value = NULL_NAME;
                }
                this.internalString(name, value);
            }
            this.pop();
        }
    }

    public void stringTable(String name, String[][] values) {
        if (values != null) {
            this.push(name + "Table");
            for (int i2 = 0; i2 < values.length; ++i2) {
                String[] rowValues = values[i2];
                if (rowValues == null) {
                    this.internalString(name + "List", NULL_NAME);
                    continue;
                }
                this.stringArray(name, rowValues);
            }
            this.pop();
        }
    }

    private void push(String name) {
        this.newline();
        this.writeString("<" + name + ">");
        this.nameStack.add(name);
    }

    private void pop() {
        int ix2 = this.nameStack.size() - 1;
        String name = this.nameStack.remove(ix2);
        this.newline();
        this.writeString("</" + name + ">");
    }

    private void newline() {
        this.writeString("\n");
        for (int i2 = 0; i2 < this.nameStack.size(); ++i2) {
            this.writeString(INDENT);
        }
    }

    private void writeString(String str) {
        if (this.w != null) {
            try {
                this.w.write(str);
            }
            catch (IOException e2) {
                System.err.println(e2.getMessage());
                this.w = null;
            }
        }
    }
}

