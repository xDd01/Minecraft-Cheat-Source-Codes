package org.apache.http;

public interface ExceptionLogger
{
    public static final ExceptionLogger NO_OP = new ExceptionLogger() {
        @Override
        public void log(final Exception ex) {
        }
    };
    public static final ExceptionLogger STD_ERR = new ExceptionLogger() {
        @Override
        public void log(final Exception ex) {
            ex.printStackTrace();
        }
    };
    
    void log(final Exception p0);
}
