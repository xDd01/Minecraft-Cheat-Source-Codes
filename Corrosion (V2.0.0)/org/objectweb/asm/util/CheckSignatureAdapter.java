/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import java.util.EnumSet;
import org.objectweb.asm.signature.SignatureVisitor;

public class CheckSignatureAdapter
extends SignatureVisitor {
    public static final int CLASS_SIGNATURE = 0;
    public static final int METHOD_SIGNATURE = 1;
    public static final int TYPE_SIGNATURE = 2;
    private static final EnumSet<State> VISIT_FORMAL_TYPE_PARAMETER_STATES = EnumSet.of(State.EMPTY, State.FORMAL, State.BOUND);
    private static final EnumSet<State> VISIT_CLASS_BOUND_STATES = EnumSet.of(State.FORMAL);
    private static final EnumSet<State> VISIT_INTERFACE_BOUND_STATES = EnumSet.of(State.FORMAL, State.BOUND);
    private static final EnumSet<State> VISIT_SUPER_CLASS_STATES = EnumSet.of(State.EMPTY, State.FORMAL, State.BOUND);
    private static final EnumSet<State> VISIT_INTERFACE_STATES = EnumSet.of(State.SUPER);
    private static final EnumSet<State> VISIT_PARAMETER_TYPE_STATES = EnumSet.of(State.EMPTY, State.FORMAL, State.BOUND, State.PARAM);
    private static final EnumSet<State> VISIT_RETURN_TYPE_STATES = EnumSet.of(State.EMPTY, State.FORMAL, State.BOUND, State.PARAM);
    private static final EnumSet<State> VISIT_EXCEPTION_TYPE_STATES = EnumSet.of(State.RETURN);
    private static final String INVALID = "Invalid ";
    private final int type;
    private State state;
    private boolean canBeVoid;
    private final SignatureVisitor signatureVisitor;

    public CheckSignatureAdapter(int type, SignatureVisitor signatureVisitor) {
        this(589824, type, signatureVisitor);
    }

    protected CheckSignatureAdapter(int api2, int type, SignatureVisitor signatureVisitor) {
        super(api2);
        this.type = type;
        this.state = State.EMPTY;
        this.signatureVisitor = signatureVisitor;
    }

    public void visitFormalTypeParameter(String name) {
        if (this.type == 2 || !VISIT_FORMAL_TYPE_PARAMETER_STATES.contains((Object)this.state)) {
            throw new IllegalStateException();
        }
        this.checkIdentifier(name, "formal type parameter");
        this.state = State.FORMAL;
        if (this.signatureVisitor != null) {
            this.signatureVisitor.visitFormalTypeParameter(name);
        }
    }

    public SignatureVisitor visitClassBound() {
        if (this.type == 2 || !VISIT_CLASS_BOUND_STATES.contains((Object)this.state)) {
            throw new IllegalStateException();
        }
        this.state = State.BOUND;
        return new CheckSignatureAdapter(2, this.signatureVisitor == null ? null : this.signatureVisitor.visitClassBound());
    }

    public SignatureVisitor visitInterfaceBound() {
        if (this.type == 2 || !VISIT_INTERFACE_BOUND_STATES.contains((Object)this.state)) {
            throw new IllegalStateException();
        }
        return new CheckSignatureAdapter(2, this.signatureVisitor == null ? null : this.signatureVisitor.visitInterfaceBound());
    }

    public SignatureVisitor visitSuperclass() {
        if (this.type != 0 || !VISIT_SUPER_CLASS_STATES.contains((Object)this.state)) {
            throw new IllegalStateException();
        }
        this.state = State.SUPER;
        return new CheckSignatureAdapter(2, this.signatureVisitor == null ? null : this.signatureVisitor.visitSuperclass());
    }

    public SignatureVisitor visitInterface() {
        if (this.type != 0 || !VISIT_INTERFACE_STATES.contains((Object)this.state)) {
            throw new IllegalStateException();
        }
        return new CheckSignatureAdapter(2, this.signatureVisitor == null ? null : this.signatureVisitor.visitInterface());
    }

    public SignatureVisitor visitParameterType() {
        if (this.type != 1 || !VISIT_PARAMETER_TYPE_STATES.contains((Object)this.state)) {
            throw new IllegalStateException();
        }
        this.state = State.PARAM;
        return new CheckSignatureAdapter(2, this.signatureVisitor == null ? null : this.signatureVisitor.visitParameterType());
    }

    public SignatureVisitor visitReturnType() {
        if (this.type != 1 || !VISIT_RETURN_TYPE_STATES.contains((Object)this.state)) {
            throw new IllegalStateException();
        }
        this.state = State.RETURN;
        CheckSignatureAdapter checkSignatureAdapter = new CheckSignatureAdapter(2, this.signatureVisitor == null ? null : this.signatureVisitor.visitReturnType());
        checkSignatureAdapter.canBeVoid = true;
        return checkSignatureAdapter;
    }

    public SignatureVisitor visitExceptionType() {
        if (this.type != 1 || !VISIT_EXCEPTION_TYPE_STATES.contains((Object)this.state)) {
            throw new IllegalStateException();
        }
        return new CheckSignatureAdapter(2, this.signatureVisitor == null ? null : this.signatureVisitor.visitExceptionType());
    }

    public void visitBaseType(char descriptor) {
        if (this.type != 2 || this.state != State.EMPTY) {
            throw new IllegalStateException();
        }
        if (descriptor == 'V') {
            if (!this.canBeVoid) {
                throw new IllegalArgumentException("Base type descriptor can't be V");
            }
        } else if ("ZCBSIFJD".indexOf(descriptor) == -1) {
            throw new IllegalArgumentException("Base type descriptor must be one of ZCBSIFJD");
        }
        this.state = State.SIMPLE_TYPE;
        if (this.signatureVisitor != null) {
            this.signatureVisitor.visitBaseType(descriptor);
        }
    }

    public void visitTypeVariable(String name) {
        if (this.type != 2 || this.state != State.EMPTY) {
            throw new IllegalStateException();
        }
        this.checkIdentifier(name, "type variable");
        this.state = State.SIMPLE_TYPE;
        if (this.signatureVisitor != null) {
            this.signatureVisitor.visitTypeVariable(name);
        }
    }

    public SignatureVisitor visitArrayType() {
        if (this.type != 2 || this.state != State.EMPTY) {
            throw new IllegalStateException();
        }
        this.state = State.SIMPLE_TYPE;
        return new CheckSignatureAdapter(2, this.signatureVisitor == null ? null : this.signatureVisitor.visitArrayType());
    }

    public void visitClassType(String name) {
        if (this.type != 2 || this.state != State.EMPTY) {
            throw new IllegalStateException();
        }
        this.checkClassName(name, "class name");
        this.state = State.CLASS_TYPE;
        if (this.signatureVisitor != null) {
            this.signatureVisitor.visitClassType(name);
        }
    }

    public void visitInnerClassType(String name) {
        if (this.state != State.CLASS_TYPE) {
            throw new IllegalStateException();
        }
        this.checkIdentifier(name, "inner class name");
        if (this.signatureVisitor != null) {
            this.signatureVisitor.visitInnerClassType(name);
        }
    }

    public void visitTypeArgument() {
        if (this.state != State.CLASS_TYPE) {
            throw new IllegalStateException();
        }
        if (this.signatureVisitor != null) {
            this.signatureVisitor.visitTypeArgument();
        }
    }

    public SignatureVisitor visitTypeArgument(char wildcard) {
        if (this.state != State.CLASS_TYPE) {
            throw new IllegalStateException();
        }
        if ("+-=".indexOf(wildcard) == -1) {
            throw new IllegalArgumentException("Wildcard must be one of +-=");
        }
        return new CheckSignatureAdapter(2, this.signatureVisitor == null ? null : this.signatureVisitor.visitTypeArgument(wildcard));
    }

    public void visitEnd() {
        if (this.state != State.CLASS_TYPE) {
            throw new IllegalStateException();
        }
        this.state = State.END;
        if (this.signatureVisitor != null) {
            this.signatureVisitor.visitEnd();
        }
    }

    private void checkClassName(String name, String message) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException(INVALID + message + " (must not be null or empty)");
        }
        for (int i2 = 0; i2 < name.length(); ++i2) {
            if (".;[<>:".indexOf(name.charAt(i2)) == -1) continue;
            throw new IllegalArgumentException(INVALID + message + " (must not contain . ; [ < > or :): " + name);
        }
    }

    private void checkIdentifier(String name, String message) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException(INVALID + message + " (must not be null or empty)");
        }
        for (int i2 = 0; i2 < name.length(); ++i2) {
            if (".;[/<>:".indexOf(name.charAt(i2)) == -1) continue;
            throw new IllegalArgumentException(INVALID + message + " (must not contain . ; [ / < > or :): " + name);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum State {
        EMPTY,
        FORMAL,
        BOUND,
        SUPER,
        PARAM,
        RETURN,
        SIMPLE_TYPE,
        CLASS_TYPE,
        END;

    }
}

