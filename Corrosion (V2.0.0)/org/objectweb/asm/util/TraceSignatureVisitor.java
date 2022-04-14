/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.signature.SignatureVisitor;

public final class TraceSignatureVisitor
extends SignatureVisitor {
    private static final String COMMA_SEPARATOR = ", ";
    private static final String EXTENDS_SEPARATOR = " extends ";
    private static final String IMPLEMENTS_SEPARATOR = " implements ";
    private static final Map<Character, String> BASE_TYPES;
    private final boolean isInterface;
    private final StringBuilder declaration;
    private StringBuilder returnType;
    private StringBuilder exceptions;
    private boolean formalTypeParameterVisited;
    private boolean interfaceBoundVisited;
    private boolean parameterTypeVisited;
    private boolean interfaceVisited;
    private int argumentStack;
    private int arrayStack;
    private String separator = "";

    public TraceSignatureVisitor(int accessFlags) {
        super(589824);
        this.isInterface = (accessFlags & 0x200) != 0;
        this.declaration = new StringBuilder();
    }

    private TraceSignatureVisitor(StringBuilder stringBuilder) {
        super(589824);
        this.isInterface = false;
        this.declaration = stringBuilder;
    }

    public void visitFormalTypeParameter(String name) {
        this.declaration.append(this.formalTypeParameterVisited ? COMMA_SEPARATOR : "<").append(name);
        this.formalTypeParameterVisited = true;
        this.interfaceBoundVisited = false;
    }

    public SignatureVisitor visitClassBound() {
        this.separator = EXTENDS_SEPARATOR;
        this.startType();
        return this;
    }

    public SignatureVisitor visitInterfaceBound() {
        this.separator = this.interfaceBoundVisited ? COMMA_SEPARATOR : EXTENDS_SEPARATOR;
        this.interfaceBoundVisited = true;
        this.startType();
        return this;
    }

    public SignatureVisitor visitSuperclass() {
        this.endFormals();
        this.separator = EXTENDS_SEPARATOR;
        this.startType();
        return this;
    }

    public SignatureVisitor visitInterface() {
        if (this.interfaceVisited) {
            this.separator = COMMA_SEPARATOR;
        } else {
            this.separator = this.isInterface ? EXTENDS_SEPARATOR : IMPLEMENTS_SEPARATOR;
            this.interfaceVisited = true;
        }
        this.startType();
        return this;
    }

    public SignatureVisitor visitParameterType() {
        this.endFormals();
        if (this.parameterTypeVisited) {
            this.declaration.append(COMMA_SEPARATOR);
        } else {
            this.declaration.append('(');
            this.parameterTypeVisited = true;
        }
        this.startType();
        return this;
    }

    public SignatureVisitor visitReturnType() {
        this.endFormals();
        if (this.parameterTypeVisited) {
            this.parameterTypeVisited = false;
        } else {
            this.declaration.append('(');
        }
        this.declaration.append(')');
        this.returnType = new StringBuilder();
        return new TraceSignatureVisitor(this.returnType);
    }

    public SignatureVisitor visitExceptionType() {
        if (this.exceptions == null) {
            this.exceptions = new StringBuilder();
        } else {
            this.exceptions.append(COMMA_SEPARATOR);
        }
        return new TraceSignatureVisitor(this.exceptions);
    }

    public void visitBaseType(char descriptor) {
        String baseType = BASE_TYPES.get(Character.valueOf(descriptor));
        if (baseType == null) {
            throw new IllegalArgumentException();
        }
        this.declaration.append(baseType);
        this.endType();
    }

    public void visitTypeVariable(String name) {
        this.declaration.append(this.separator).append(name);
        this.separator = "";
        this.endType();
    }

    public SignatureVisitor visitArrayType() {
        this.startType();
        this.arrayStack |= 1;
        return this;
    }

    public void visitClassType(String name) {
        if ("java/lang/Object".equals(name)) {
            boolean needObjectClass;
            boolean bl2 = needObjectClass = this.argumentStack % 2 != 0 || this.parameterTypeVisited;
            if (needObjectClass) {
                this.declaration.append(this.separator).append(name.replace('/', '.'));
            }
        } else {
            this.declaration.append(this.separator).append(name.replace('/', '.'));
        }
        this.separator = "";
        this.argumentStack *= 2;
    }

    public void visitInnerClassType(String name) {
        if (this.argumentStack % 2 != 0) {
            this.declaration.append('>');
        }
        this.argumentStack /= 2;
        this.declaration.append('.');
        this.declaration.append(this.separator).append(name.replace('/', '.'));
        this.separator = "";
        this.argumentStack *= 2;
    }

    public void visitTypeArgument() {
        if (this.argumentStack % 2 == 0) {
            ++this.argumentStack;
            this.declaration.append('<');
        } else {
            this.declaration.append(COMMA_SEPARATOR);
        }
        this.declaration.append('?');
    }

    public SignatureVisitor visitTypeArgument(char tag) {
        if (this.argumentStack % 2 == 0) {
            ++this.argumentStack;
            this.declaration.append('<');
        } else {
            this.declaration.append(COMMA_SEPARATOR);
        }
        if (tag == '+') {
            this.declaration.append("? extends ");
        } else if (tag == '-') {
            this.declaration.append("? super ");
        }
        this.startType();
        return this;
    }

    public void visitEnd() {
        if (this.argumentStack % 2 != 0) {
            this.declaration.append('>');
        }
        this.argumentStack /= 2;
        this.endType();
    }

    public String getDeclaration() {
        return this.declaration.toString();
    }

    public String getReturnType() {
        return this.returnType == null ? null : this.returnType.toString();
    }

    public String getExceptions() {
        return this.exceptions == null ? null : this.exceptions.toString();
    }

    private void endFormals() {
        if (this.formalTypeParameterVisited) {
            this.declaration.append('>');
            this.formalTypeParameterVisited = false;
        }
    }

    private void startType() {
        this.arrayStack *= 2;
    }

    private void endType() {
        if (this.arrayStack % 2 == 0) {
            this.arrayStack /= 2;
        } else {
            while (this.arrayStack % 2 != 0) {
                this.arrayStack /= 2;
                this.declaration.append("[]");
            }
        }
    }

    static {
        HashMap<Character, String> baseTypes = new HashMap<Character, String>();
        baseTypes.put(Character.valueOf('Z'), "boolean");
        baseTypes.put(Character.valueOf('B'), "byte");
        baseTypes.put(Character.valueOf('C'), "char");
        baseTypes.put(Character.valueOf('S'), "short");
        baseTypes.put(Character.valueOf('I'), "int");
        baseTypes.put(Character.valueOf('J'), "long");
        baseTypes.put(Character.valueOf('F'), "float");
        baseTypes.put(Character.valueOf('D'), "double");
        baseTypes.put(Character.valueOf('V'), "void");
        BASE_TYPES = Collections.unmodifiableMap(baseTypes);
    }
}

