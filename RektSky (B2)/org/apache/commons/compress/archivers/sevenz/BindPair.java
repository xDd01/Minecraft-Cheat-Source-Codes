package org.apache.commons.compress.archivers.sevenz;

class BindPair
{
    long inIndex;
    long outIndex;
    
    @Override
    public String toString() {
        return "BindPair binding input " + this.inIndex + " to output " + this.outIndex;
    }
}
