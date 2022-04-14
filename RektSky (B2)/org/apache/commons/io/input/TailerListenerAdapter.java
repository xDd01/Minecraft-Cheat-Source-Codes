package org.apache.commons.io.input;

public class TailerListenerAdapter implements TailerListener
{
    @Override
    public void init(final Tailer tailer) {
    }
    
    @Override
    public void fileNotFound() {
    }
    
    @Override
    public void fileRotated() {
    }
    
    @Override
    public void handle(final String line) {
    }
    
    @Override
    public void handle(final Exception ex) {
    }
    
    public void endOfFileReached() {
    }
}
