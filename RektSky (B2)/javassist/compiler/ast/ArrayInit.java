package javassist.compiler.ast;

import javassist.compiler.*;

public class ArrayInit extends ASTList
{
    private static final long serialVersionUID = 1L;
    
    public ArrayInit(final ASTree firstElement) {
        super(firstElement);
    }
    
    @Override
    public void accept(final Visitor v) throws CompileError {
        v.atArrayInit(this);
    }
    
    public String getTag() {
        return "array";
    }
}
