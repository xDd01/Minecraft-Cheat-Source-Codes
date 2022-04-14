package org.apache.http.impl.client;

import org.apache.http.client.*;
import java.io.*;
import org.apache.http.annotation.*;
import org.apache.http.cookie.*;
import java.util.concurrent.locks.*;
import java.util.*;

@Contract(threading = ThreadingBehavior.SAFE)
public class BasicCookieStore implements CookieStore, Serializable
{
    private static final long serialVersionUID = -7581093305228232025L;
    private final TreeSet<Cookie> cookies;
    private final ReadWriteLock lock;
    
    public BasicCookieStore() {
        this.cookies = new TreeSet<Cookie>(new CookieIdentityComparator());
        this.lock = new ReentrantReadWriteLock();
    }
    
    @Override
    public void addCookie(final Cookie cookie) {
        if (cookie != null) {
            this.lock.writeLock().lock();
            try {
                this.cookies.remove(cookie);
                if (!cookie.isExpired(new Date())) {
                    this.cookies.add(cookie);
                }
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
    }
    
    public void addCookies(final Cookie[] cookies) {
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                this.addCookie(cookie);
            }
        }
    }
    
    @Override
    public List<Cookie> getCookies() {
        this.lock.readLock().lock();
        try {
            return new ArrayList<Cookie>(this.cookies);
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    @Override
    public boolean clearExpired(final Date date) {
        if (date == null) {
            return false;
        }
        this.lock.writeLock().lock();
        try {
            boolean removed = false;
            final Iterator<Cookie> it = this.cookies.iterator();
            while (it.hasNext()) {
                if (it.next().isExpired(date)) {
                    it.remove();
                    removed = true;
                }
            }
            return removed;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    @Override
    public void clear() {
        this.lock.writeLock().lock();
        try {
            this.cookies.clear();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    @Override
    public String toString() {
        this.lock.readLock().lock();
        try {
            return this.cookies.toString();
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
}
