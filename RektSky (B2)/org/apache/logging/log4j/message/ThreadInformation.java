package org.apache.logging.log4j.message;

interface ThreadInformation
{
    void printThreadInfo(final StringBuilder p0);
    
    void printStack(final StringBuilder p0, final StackTraceElement[] p1);
}
