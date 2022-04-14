/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Interpreter<V extends Value> {
    protected final int api;

    protected Interpreter(int api2) {
        this.api = api2;
    }

    public abstract V newValue(Type var1);

    public V newParameterValue(boolean isInstanceMethod, int local, Type type) {
        return this.newValue(type);
    }

    public V newReturnTypeValue(Type type) {
        return this.newValue(type);
    }

    public V newEmptyValue(int local) {
        return this.newValue(null);
    }

    public V newExceptionValue(TryCatchBlockNode tryCatchBlockNode, Frame<V> handlerFrame, Type exceptionType) {
        return this.newValue(exceptionType);
    }

    public abstract V newOperation(AbstractInsnNode var1) throws AnalyzerException;

    public abstract V copyOperation(AbstractInsnNode var1, V var2) throws AnalyzerException;

    public abstract V unaryOperation(AbstractInsnNode var1, V var2) throws AnalyzerException;

    public abstract V binaryOperation(AbstractInsnNode var1, V var2, V var3) throws AnalyzerException;

    public abstract V ternaryOperation(AbstractInsnNode var1, V var2, V var3, V var4) throws AnalyzerException;

    public abstract V naryOperation(AbstractInsnNode var1, List<? extends V> var2) throws AnalyzerException;

    public abstract void returnOperation(AbstractInsnNode var1, V var2, V var3) throws AnalyzerException;

    public abstract V merge(V var1, V var2);
}

