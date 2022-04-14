package org.apache.commons.io.input;

import java.io.*;
import java.util.*;

public class ObservableInputStream extends ProxyInputStream
{
    private final List<Observer> observers;
    
    public ObservableInputStream(final InputStream pProxy) {
        super(pProxy);
        this.observers = new ArrayList<Observer>();
    }
    
    public void add(final Observer pObserver) {
        this.observers.add(pObserver);
    }
    
    public void remove(final Observer pObserver) {
        this.observers.remove(pObserver);
    }
    
    public void removeAllObservers() {
        this.observers.clear();
    }
    
    @Override
    public int read() throws IOException {
        int result = 0;
        IOException ioe = null;
        try {
            result = super.read();
        }
        catch (IOException pException) {
            ioe = pException;
        }
        if (ioe != null) {
            this.noteError(ioe);
        }
        else if (result == -1) {
            this.noteFinished();
        }
        else {
            this.noteDataByte(result);
        }
        return result;
    }
    
    @Override
    public int read(final byte[] pBuffer) throws IOException {
        int result = 0;
        IOException ioe = null;
        try {
            result = super.read(pBuffer);
        }
        catch (IOException pException) {
            ioe = pException;
        }
        if (ioe != null) {
            this.noteError(ioe);
        }
        else if (result == -1) {
            this.noteFinished();
        }
        else if (result > 0) {
            this.noteDataBytes(pBuffer, 0, result);
        }
        return result;
    }
    
    @Override
    public int read(final byte[] pBuffer, final int pOffset, final int pLength) throws IOException {
        int result = 0;
        IOException ioe = null;
        try {
            result = super.read(pBuffer, pOffset, pLength);
        }
        catch (IOException pException) {
            ioe = pException;
        }
        if (ioe != null) {
            this.noteError(ioe);
        }
        else if (result == -1) {
            this.noteFinished();
        }
        else if (result > 0) {
            this.noteDataBytes(pBuffer, pOffset, result);
        }
        return result;
    }
    
    protected void noteDataBytes(final byte[] pBuffer, final int pOffset, final int pLength) throws IOException {
        for (final Observer observer : this.getObservers()) {
            observer.data(pBuffer, pOffset, pLength);
        }
    }
    
    protected void noteFinished() throws IOException {
        for (final Observer observer : this.getObservers()) {
            observer.finished();
        }
    }
    
    protected void noteDataByte(final int pDataByte) throws IOException {
        for (final Observer observer : this.getObservers()) {
            observer.data(pDataByte);
        }
    }
    
    protected void noteError(final IOException pException) throws IOException {
        for (final Observer observer : this.getObservers()) {
            observer.error(pException);
        }
    }
    
    protected void noteClosed() throws IOException {
        for (final Observer observer : this.getObservers()) {
            observer.closed();
        }
    }
    
    protected List<Observer> getObservers() {
        return this.observers;
    }
    
    @Override
    public void close() throws IOException {
        IOException ioe = null;
        try {
            super.close();
        }
        catch (IOException e) {
            ioe = e;
        }
        if (ioe == null) {
            this.noteClosed();
        }
        else {
            this.noteError(ioe);
        }
    }
    
    public void consume() throws IOException {
        final byte[] buffer = new byte[8192];
        int res;
        do {
            res = this.read(buffer);
        } while (res != -1);
    }
    
    public abstract static class Observer
    {
        void data(final int pByte) throws IOException {
        }
        
        void data(final byte[] pBuffer, final int pOffset, final int pLength) throws IOException {
        }
        
        void finished() throws IOException {
        }
        
        void closed() throws IOException {
        }
        
        void error(final IOException pException) throws IOException {
            throw pException;
        }
    }
}
