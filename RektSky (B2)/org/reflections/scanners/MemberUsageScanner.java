package org.reflections.scanners;

import org.reflections.*;
import javassist.expr.*;
import javassist.bytecode.*;
import java.util.*;
import org.reflections.util.*;
import javassist.*;

public class MemberUsageScanner extends AbstractScanner
{
    private ClassPool classPool;
    
    @Override
    public void scan(final Object cls, final Store store) {
        try {
            final CtClass ctClass = this.getClassPool().get(this.getMetadataAdapter().getClassName(cls));
            for (final CtBehavior member : ctClass.getDeclaredConstructors()) {
                this.scanMember(member, store);
            }
            for (final CtBehavior member : ctClass.getDeclaredMethods()) {
                this.scanMember(member, store);
            }
            ctClass.detach();
        }
        catch (Exception e) {
            throw new ReflectionsException("Could not scan method usage for " + this.getMetadataAdapter().getClassName(cls), e);
        }
    }
    
    void scanMember(final CtBehavior member, final Store store) throws CannotCompileException {
        final String key = member.getDeclaringClass().getName() + "." + member.getMethodInfo().getName() + "(" + this.parameterNames(member.getMethodInfo()) + ")";
        member.instrument(new ExprEditor() {
            @Override
            public void edit(final NewExpr e) throws CannotCompileException {
                try {
                    MemberUsageScanner.this.put(store, e.getConstructor().getDeclaringClass().getName() + ".<init>(" + MemberUsageScanner.this.parameterNames(e.getConstructor().getMethodInfo()) + ")", e.getLineNumber(), key);
                }
                catch (NotFoundException e2) {
                    throw new ReflectionsException("Could not find new instance usage in " + key, e2);
                }
            }
            
            @Override
            public void edit(final MethodCall m) throws CannotCompileException {
                try {
                    MemberUsageScanner.this.put(store, m.getMethod().getDeclaringClass().getName() + "." + m.getMethodName() + "(" + MemberUsageScanner.this.parameterNames(m.getMethod().getMethodInfo()) + ")", m.getLineNumber(), key);
                }
                catch (NotFoundException e) {
                    throw new ReflectionsException("Could not find member " + m.getClassName() + " in " + key, e);
                }
            }
            
            @Override
            public void edit(final ConstructorCall c) throws CannotCompileException {
                try {
                    MemberUsageScanner.this.put(store, c.getConstructor().getDeclaringClass().getName() + ".<init>(" + MemberUsageScanner.this.parameterNames(c.getConstructor().getMethodInfo()) + ")", c.getLineNumber(), key);
                }
                catch (NotFoundException e) {
                    throw new ReflectionsException("Could not find member " + c.getClassName() + " in " + key, e);
                }
            }
            
            @Override
            public void edit(final FieldAccess f) throws CannotCompileException {
                try {
                    MemberUsageScanner.this.put(store, f.getField().getDeclaringClass().getName() + "." + f.getFieldName(), f.getLineNumber(), key);
                }
                catch (NotFoundException e) {
                    throw new ReflectionsException("Could not find member " + f.getFieldName() + " in " + key, e);
                }
            }
        });
    }
    
    private void put(final Store store, final String key, final int lineNumber, final String value) {
        if (this.acceptResult(key)) {
            this.put(store, key, value + " #" + lineNumber);
        }
    }
    
    String parameterNames(final MethodInfo info) {
        return Utils.join(this.getMetadataAdapter().getParameterNames(info), ", ");
    }
    
    private ClassPool getClassPool() {
        if (this.classPool == null) {
            synchronized (this) {
                this.classPool = new ClassPool();
                ClassLoader[] classLoaders = this.getConfiguration().getClassLoaders();
                if (classLoaders == null) {
                    classLoaders = ClasspathHelper.classLoaders(new ClassLoader[0]);
                }
                for (final ClassLoader classLoader : classLoaders) {
                    this.classPool.appendClassPath(new LoaderClassPath(classLoader));
                }
            }
        }
        return this.classPool;
    }
}
