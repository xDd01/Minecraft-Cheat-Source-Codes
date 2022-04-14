package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.*;

public class MultilineRecursiveToStringStyle extends RecursiveToStringStyle
{
    private static final long serialVersionUID = 1L;
    private static final int INDENT = 2;
    private int spaces;
    
    public MultilineRecursiveToStringStyle() {
        this.spaces = 2;
        this.resetIndent();
    }
    
    private void resetIndent() {
        this.setArrayStart("{" + System.lineSeparator() + (Object)this.spacer(this.spaces));
        this.setArraySeparator("," + System.lineSeparator() + (Object)this.spacer(this.spaces));
        this.setArrayEnd(System.lineSeparator() + (Object)this.spacer(this.spaces - 2) + "}");
        this.setContentStart("[" + System.lineSeparator() + (Object)this.spacer(this.spaces));
        this.setFieldSeparator("," + System.lineSeparator() + (Object)this.spacer(this.spaces));
        this.setContentEnd(System.lineSeparator() + (Object)this.spacer(this.spaces - 2) + "]");
    }
    
    private StringBuilder spacer(final int spaces) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaces; ++i) {
            sb.append(" ");
        }
        return sb;
    }
    
    @Override
    public void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
        if (!ClassUtils.isPrimitiveWrapper(value.getClass()) && !String.class.equals(value.getClass()) && this.accept(value.getClass())) {
            this.spaces += 2;
            this.resetIndent();
            buffer.append(ReflectionToStringBuilder.toString(value, this));
            this.spaces -= 2;
            this.resetIndent();
        }
        else {
            super.appendDetail(buffer, fieldName, value);
        }
    }
    
    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object[] array) {
        this.spaces += 2;
        this.resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        this.resetIndent();
    }
    
    @Override
    protected void reflectionAppendArrayDetail(final StringBuffer buffer, final String fieldName, final Object array) {
        this.spaces += 2;
        this.resetIndent();
        super.reflectionAppendArrayDetail(buffer, fieldName, array);
        this.spaces -= 2;
        this.resetIndent();
    }
    
    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final long[] array) {
        this.spaces += 2;
        this.resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        this.resetIndent();
    }
    
    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final int[] array) {
        this.spaces += 2;
        this.resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        this.resetIndent();
    }
    
    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final short[] array) {
        this.spaces += 2;
        this.resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        this.resetIndent();
    }
    
    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final byte[] array) {
        this.spaces += 2;
        this.resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        this.resetIndent();
    }
    
    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final char[] array) {
        this.spaces += 2;
        this.resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        this.resetIndent();
    }
    
    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final double[] array) {
        this.spaces += 2;
        this.resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        this.resetIndent();
    }
    
    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final float[] array) {
        this.spaces += 2;
        this.resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        this.resetIndent();
    }
    
    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final boolean[] array) {
        this.spaces += 2;
        this.resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        this.resetIndent();
    }
}
