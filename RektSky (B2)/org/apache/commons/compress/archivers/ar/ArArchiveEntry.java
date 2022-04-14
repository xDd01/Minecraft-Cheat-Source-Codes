package org.apache.commons.compress.archivers.ar;

import org.apache.commons.compress.archivers.*;
import java.io.*;
import java.util.*;

public class ArArchiveEntry implements ArchiveEntry
{
    public static final String HEADER = "!<arch>\n";
    public static final String TRAILER = "`\n";
    private final String name;
    private final int userId;
    private final int groupId;
    private final int mode;
    private static final int DEFAULT_MODE = 33188;
    private final long lastModified;
    private final long length;
    
    public ArArchiveEntry(final String name, final long length) {
        this(name, length, 0, 0, 33188, System.currentTimeMillis() / 1000L);
    }
    
    public ArArchiveEntry(final String name, final long length, final int userId, final int groupId, final int mode, final long lastModified) {
        this.name = name;
        this.length = length;
        this.userId = userId;
        this.groupId = groupId;
        this.mode = mode;
        this.lastModified = lastModified;
    }
    
    public ArArchiveEntry(final File inputFile, final String entryName) {
        this(entryName, inputFile.isFile() ? inputFile.length() : 0L, 0, 0, 33188, inputFile.lastModified() / 1000L);
    }
    
    @Override
    public long getSize() {
        return this.getLength();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public int getUserId() {
        return this.userId;
    }
    
    public int getGroupId() {
        return this.groupId;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public long getLastModified() {
        return this.lastModified;
    }
    
    @Override
    public Date getLastModifiedDate() {
        return new Date(1000L * this.getLastModified());
    }
    
    public long getLength() {
        return this.length;
    }
    
    @Override
    public boolean isDirectory() {
        return false;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final ArArchiveEntry other = (ArArchiveEntry)obj;
        if (this.name == null) {
            return other.name == null;
        }
        return this.name.equals(other.name);
    }
}
