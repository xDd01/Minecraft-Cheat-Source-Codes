package javassist.bytecode;

import java.util.*;
import java.io.*;

class ConstInfoPadding extends ConstInfo
{
    public ConstInfoPadding(final int i) {
        super(i);
    }
    
    @Override
    public int getTag() {
        return 0;
    }
    
    @Override
    public int copy(final ConstPool src, final ConstPool dest, final Map<String, String> map) {
        return dest.addConstInfoPadding();
    }
    
    @Override
    public void write(final DataOutputStream out) throws IOException {
    }
    
    @Override
    public void print(final PrintWriter out) {
        out.println("padding");
    }
}
