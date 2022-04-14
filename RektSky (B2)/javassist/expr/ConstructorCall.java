package javassist.expr;

import javassist.bytecode.*;
import javassist.*;

public class ConstructorCall extends MethodCall
{
    protected ConstructorCall(final int pos, final CodeIterator i, final CtClass decl, final MethodInfo m) {
        super(pos, i, decl, m);
    }
    
    @Override
    public String getMethodName() {
        return this.isSuper() ? "super" : "this";
    }
    
    @Override
    public CtMethod getMethod() throws NotFoundException {
        throw new NotFoundException("this is a constructor call.  Call getConstructor().");
    }
    
    public CtConstructor getConstructor() throws NotFoundException {
        return this.getCtClass().getConstructor(this.getSignature());
    }
    
    @Override
    public boolean isSuper() {
        return super.isSuper();
    }
}
