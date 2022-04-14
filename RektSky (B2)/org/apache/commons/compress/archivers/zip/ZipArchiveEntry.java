package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.archivers.*;
import java.util.zip.*;
import java.io.*;
import java.util.*;

public class ZipArchiveEntry extends ZipEntry implements ArchiveEntry, EntryStreamOffsets
{
    public static final int PLATFORM_UNIX = 3;
    public static final int PLATFORM_FAT = 0;
    public static final int CRC_UNKNOWN = -1;
    private static final int SHORT_MASK = 65535;
    private static final int SHORT_SHIFT = 16;
    private static final byte[] EMPTY;
    private int method;
    private long size;
    private int internalAttributes;
    private int versionRequired;
    private int versionMadeBy;
    private int platform;
    private int rawFlag;
    private long externalAttributes;
    private int alignment;
    private ZipExtraField[] extraFields;
    private UnparseableExtraFieldData unparseableExtra;
    private String name;
    private byte[] rawName;
    private GeneralPurposeBit gpb;
    private static final ZipExtraField[] noExtraFields;
    private long localHeaderOffset;
    private long dataOffset;
    private boolean isStreamContiguous;
    private NameSource nameSource;
    private CommentSource commentSource;
    
    public ZipArchiveEntry(final String name) {
        super(name);
        this.method = -1;
        this.size = -1L;
        this.internalAttributes = 0;
        this.platform = 0;
        this.externalAttributes = 0L;
        this.alignment = 0;
        this.unparseableExtra = null;
        this.name = null;
        this.rawName = null;
        this.gpb = new GeneralPurposeBit();
        this.localHeaderOffset = -1L;
        this.dataOffset = -1L;
        this.isStreamContiguous = false;
        this.nameSource = NameSource.NAME;
        this.commentSource = CommentSource.COMMENT;
        this.setName(name);
    }
    
    public ZipArchiveEntry(final ZipEntry entry) throws ZipException {
        super(entry);
        this.method = -1;
        this.size = -1L;
        this.internalAttributes = 0;
        this.platform = 0;
        this.externalAttributes = 0L;
        this.alignment = 0;
        this.unparseableExtra = null;
        this.name = null;
        this.rawName = null;
        this.gpb = new GeneralPurposeBit();
        this.localHeaderOffset = -1L;
        this.dataOffset = -1L;
        this.isStreamContiguous = false;
        this.nameSource = NameSource.NAME;
        this.commentSource = CommentSource.COMMENT;
        this.setName(entry.getName());
        final byte[] extra = entry.getExtra();
        if (extra != null) {
            this.setExtraFields(ExtraFieldUtils.parse(extra, true, ExtraFieldUtils.UnparseableExtraField.READ));
        }
        else {
            this.setExtra();
        }
        this.setMethod(entry.getMethod());
        this.size = entry.getSize();
    }
    
    public ZipArchiveEntry(final ZipArchiveEntry entry) throws ZipException {
        this((ZipEntry)entry);
        this.setInternalAttributes(entry.getInternalAttributes());
        this.setExternalAttributes(entry.getExternalAttributes());
        this.setExtraFields(this.getAllExtraFieldsNoCopy());
        this.setPlatform(entry.getPlatform());
        final GeneralPurposeBit other = entry.getGeneralPurposeBit();
        this.setGeneralPurposeBit((other == null) ? null : ((GeneralPurposeBit)other.clone()));
    }
    
    protected ZipArchiveEntry() {
        this("");
    }
    
    public ZipArchiveEntry(final File inputFile, final String entryName) {
        this((inputFile.isDirectory() && !entryName.endsWith("/")) ? (entryName + "/") : entryName);
        if (inputFile.isFile()) {
            this.setSize(inputFile.length());
        }
        this.setTime(inputFile.lastModified());
    }
    
    @Override
    public Object clone() {
        final ZipArchiveEntry e = (ZipArchiveEntry)super.clone();
        e.setInternalAttributes(this.getInternalAttributes());
        e.setExternalAttributes(this.getExternalAttributes());
        e.setExtraFields(this.getAllExtraFieldsNoCopy());
        return e;
    }
    
    @Override
    public int getMethod() {
        return this.method;
    }
    
    @Override
    public void setMethod(final int method) {
        if (method < 0) {
            throw new IllegalArgumentException("ZIP compression method can not be negative: " + method);
        }
        this.method = method;
    }
    
    public int getInternalAttributes() {
        return this.internalAttributes;
    }
    
    public void setInternalAttributes(final int value) {
        this.internalAttributes = value;
    }
    
    public long getExternalAttributes() {
        return this.externalAttributes;
    }
    
    public void setExternalAttributes(final long value) {
        this.externalAttributes = value;
    }
    
    public void setUnixMode(final int mode) {
        this.setExternalAttributes(mode << 16 | (((mode & 0x80) == 0x0) ? 1 : 0) | (this.isDirectory() ? 16 : 0));
        this.platform = 3;
    }
    
    public int getUnixMode() {
        return (this.platform != 3) ? 0 : ((int)(this.getExternalAttributes() >> 16 & 0xFFFFL));
    }
    
    public boolean isUnixSymlink() {
        return (this.getUnixMode() & 0xF000) == 0xA000;
    }
    
    public int getPlatform() {
        return this.platform;
    }
    
    protected void setPlatform(final int platform) {
        this.platform = platform;
    }
    
    protected int getAlignment() {
        return this.alignment;
    }
    
    public void setAlignment(final int alignment) {
        if ((alignment & alignment - 1) != 0x0 || alignment > 65535) {
            throw new IllegalArgumentException("Invalid value for alignment, must be power of two and no bigger than 65535 but is " + alignment);
        }
        this.alignment = alignment;
    }
    
    public void setExtraFields(final ZipExtraField[] fields) {
        final List<ZipExtraField> newFields = new ArrayList<ZipExtraField>();
        for (final ZipExtraField field : fields) {
            if (field instanceof UnparseableExtraFieldData) {
                this.unparseableExtra = (UnparseableExtraFieldData)field;
            }
            else {
                newFields.add(field);
            }
        }
        this.extraFields = newFields.toArray(new ZipExtraField[newFields.size()]);
        this.setExtra();
    }
    
    public ZipExtraField[] getExtraFields() {
        return this.getParseableExtraFields();
    }
    
    public ZipExtraField[] getExtraFields(final boolean includeUnparseable) {
        return includeUnparseable ? this.getAllExtraFields() : this.getParseableExtraFields();
    }
    
    private ZipExtraField[] getParseableExtraFieldsNoCopy() {
        if (this.extraFields == null) {
            return ZipArchiveEntry.noExtraFields;
        }
        return this.extraFields;
    }
    
    private ZipExtraField[] getParseableExtraFields() {
        final ZipExtraField[] parseableExtraFields = this.getParseableExtraFieldsNoCopy();
        return (parseableExtraFields == this.extraFields) ? this.copyOf(parseableExtraFields) : parseableExtraFields;
    }
    
    private ZipExtraField[] getAllExtraFieldsNoCopy() {
        if (this.extraFields == null) {
            return this.getUnparseableOnly();
        }
        return (this.unparseableExtra != null) ? this.getMergedFields() : this.extraFields;
    }
    
    private ZipExtraField[] copyOf(final ZipExtraField[] src) {
        return this.copyOf(src, src.length);
    }
    
    private ZipExtraField[] copyOf(final ZipExtraField[] src, final int length) {
        final ZipExtraField[] cpy = new ZipExtraField[length];
        System.arraycopy(src, 0, cpy, 0, Math.min(src.length, length));
        return cpy;
    }
    
    private ZipExtraField[] getMergedFields() {
        final ZipExtraField[] zipExtraFields = this.copyOf(this.extraFields, this.extraFields.length + 1);
        zipExtraFields[this.extraFields.length] = this.unparseableExtra;
        return zipExtraFields;
    }
    
    private ZipExtraField[] getUnparseableOnly() {
        return (this.unparseableExtra == null) ? ZipArchiveEntry.noExtraFields : new ZipExtraField[] { this.unparseableExtra };
    }
    
    private ZipExtraField[] getAllExtraFields() {
        final ZipExtraField[] allExtraFieldsNoCopy = this.getAllExtraFieldsNoCopy();
        return (allExtraFieldsNoCopy == this.extraFields) ? this.copyOf(allExtraFieldsNoCopy) : allExtraFieldsNoCopy;
    }
    
    public void addExtraField(final ZipExtraField ze) {
        if (ze instanceof UnparseableExtraFieldData) {
            this.unparseableExtra = (UnparseableExtraFieldData)ze;
        }
        else if (this.extraFields == null) {
            this.extraFields = new ZipExtraField[] { ze };
        }
        else {
            if (this.getExtraField(ze.getHeaderId()) != null) {
                this.removeExtraField(ze.getHeaderId());
            }
            final ZipExtraField[] zipExtraFields = this.copyOf(this.extraFields, this.extraFields.length + 1);
            zipExtraFields[zipExtraFields.length - 1] = ze;
            this.extraFields = zipExtraFields;
        }
        this.setExtra();
    }
    
    public void addAsFirstExtraField(final ZipExtraField ze) {
        if (ze instanceof UnparseableExtraFieldData) {
            this.unparseableExtra = (UnparseableExtraFieldData)ze;
        }
        else {
            if (this.getExtraField(ze.getHeaderId()) != null) {
                this.removeExtraField(ze.getHeaderId());
            }
            final ZipExtraField[] copy = this.extraFields;
            final int newLen = (this.extraFields != null) ? (this.extraFields.length + 1) : 1;
            (this.extraFields = new ZipExtraField[newLen])[0] = ze;
            if (copy != null) {
                System.arraycopy(copy, 0, this.extraFields, 1, this.extraFields.length - 1);
            }
        }
        this.setExtra();
    }
    
    public void removeExtraField(final ZipShort type) {
        if (this.extraFields == null) {
            throw new NoSuchElementException();
        }
        final List<ZipExtraField> newResult = new ArrayList<ZipExtraField>();
        for (final ZipExtraField extraField : this.extraFields) {
            if (!type.equals(extraField.getHeaderId())) {
                newResult.add(extraField);
            }
        }
        if (this.extraFields.length == newResult.size()) {
            throw new NoSuchElementException();
        }
        this.extraFields = newResult.toArray(new ZipExtraField[newResult.size()]);
        this.setExtra();
    }
    
    public void removeUnparseableExtraFieldData() {
        if (this.unparseableExtra == null) {
            throw new NoSuchElementException();
        }
        this.unparseableExtra = null;
        this.setExtra();
    }
    
    public ZipExtraField getExtraField(final ZipShort type) {
        if (this.extraFields != null) {
            for (final ZipExtraField extraField : this.extraFields) {
                if (type.equals(extraField.getHeaderId())) {
                    return extraField;
                }
            }
        }
        return null;
    }
    
    public UnparseableExtraFieldData getUnparseableExtraFieldData() {
        return this.unparseableExtra;
    }
    
    @Override
    public void setExtra(final byte[] extra) throws RuntimeException {
        try {
            final ZipExtraField[] local = ExtraFieldUtils.parse(extra, true, ExtraFieldUtils.UnparseableExtraField.READ);
            this.mergeExtraFields(local, true);
        }
        catch (ZipException e) {
            throw new RuntimeException("Error parsing extra fields for entry: " + this.getName() + " - " + e.getMessage(), e);
        }
    }
    
    protected void setExtra() {
        super.setExtra(ExtraFieldUtils.mergeLocalFileDataData(this.getAllExtraFieldsNoCopy()));
    }
    
    public void setCentralDirectoryExtra(final byte[] b) {
        try {
            final ZipExtraField[] central = ExtraFieldUtils.parse(b, false, ExtraFieldUtils.UnparseableExtraField.READ);
            this.mergeExtraFields(central, false);
        }
        catch (ZipException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public byte[] getLocalFileDataExtra() {
        final byte[] extra = this.getExtra();
        return (extra != null) ? extra : ZipArchiveEntry.EMPTY;
    }
    
    public byte[] getCentralDirectoryExtra() {
        return ExtraFieldUtils.mergeCentralDirectoryData(this.getAllExtraFieldsNoCopy());
    }
    
    @Override
    public String getName() {
        return (this.name == null) ? super.getName() : this.name;
    }
    
    @Override
    public boolean isDirectory() {
        return this.getName().endsWith("/");
    }
    
    protected void setName(String name) {
        if (name != null && this.getPlatform() == 0 && !name.contains("/")) {
            name = name.replace('\\', '/');
        }
        this.name = name;
    }
    
    @Override
    public long getSize() {
        return this.size;
    }
    
    @Override
    public void setSize(final long size) {
        if (size < 0L) {
            throw new IllegalArgumentException("invalid entry size");
        }
        this.size = size;
    }
    
    protected void setName(final String name, final byte[] rawName) {
        this.setName(name);
        this.rawName = rawName;
    }
    
    public byte[] getRawName() {
        if (this.rawName != null) {
            final byte[] b = new byte[this.rawName.length];
            System.arraycopy(this.rawName, 0, b, 0, this.rawName.length);
            return b;
        }
        return null;
    }
    
    protected long getLocalHeaderOffset() {
        return this.localHeaderOffset;
    }
    
    protected void setLocalHeaderOffset(final long localHeaderOffset) {
        this.localHeaderOffset = localHeaderOffset;
    }
    
    @Override
    public long getDataOffset() {
        return this.dataOffset;
    }
    
    protected void setDataOffset(final long dataOffset) {
        this.dataOffset = dataOffset;
    }
    
    @Override
    public boolean isStreamContiguous() {
        return this.isStreamContiguous;
    }
    
    protected void setStreamContiguous(final boolean isStreamContiguous) {
        this.isStreamContiguous = isStreamContiguous;
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    public GeneralPurposeBit getGeneralPurposeBit() {
        return this.gpb;
    }
    
    public void setGeneralPurposeBit(final GeneralPurposeBit b) {
        this.gpb = b;
    }
    
    private void mergeExtraFields(final ZipExtraField[] f, final boolean local) throws ZipException {
        if (this.extraFields == null) {
            this.setExtraFields(f);
        }
        else {
            for (final ZipExtraField element : f) {
                ZipExtraField existing;
                if (element instanceof UnparseableExtraFieldData) {
                    existing = this.unparseableExtra;
                }
                else {
                    existing = this.getExtraField(element.getHeaderId());
                }
                if (existing == null) {
                    this.addExtraField(element);
                }
                else if (local) {
                    final byte[] b = element.getLocalFileDataData();
                    existing.parseFromLocalFileData(b, 0, b.length);
                }
                else {
                    final byte[] b = element.getCentralDirectoryData();
                    existing.parseFromCentralDirectoryData(b, 0, b.length);
                }
            }
            this.setExtra();
        }
    }
    
    @Override
    public Date getLastModifiedDate() {
        return new Date(this.getTime());
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final ZipArchiveEntry other = (ZipArchiveEntry)obj;
        final String myName = this.getName();
        final String otherName = other.getName();
        if (myName == null) {
            if (otherName != null) {
                return false;
            }
        }
        else if (!myName.equals(otherName)) {
            return false;
        }
        String myComment = this.getComment();
        String otherComment = other.getComment();
        if (myComment == null) {
            myComment = "";
        }
        if (otherComment == null) {
            otherComment = "";
        }
        return this.getTime() == other.getTime() && myComment.equals(otherComment) && this.getInternalAttributes() == other.getInternalAttributes() && this.getPlatform() == other.getPlatform() && this.getExternalAttributes() == other.getExternalAttributes() && this.getMethod() == other.getMethod() && this.getSize() == other.getSize() && this.getCrc() == other.getCrc() && this.getCompressedSize() == other.getCompressedSize() && Arrays.equals(this.getCentralDirectoryExtra(), other.getCentralDirectoryExtra()) && Arrays.equals(this.getLocalFileDataExtra(), other.getLocalFileDataExtra()) && this.localHeaderOffset == other.localHeaderOffset && this.dataOffset == other.dataOffset && this.gpb.equals(other.gpb);
    }
    
    public void setVersionMadeBy(final int versionMadeBy) {
        this.versionMadeBy = versionMadeBy;
    }
    
    public void setVersionRequired(final int versionRequired) {
        this.versionRequired = versionRequired;
    }
    
    public int getVersionRequired() {
        return this.versionRequired;
    }
    
    public int getVersionMadeBy() {
        return this.versionMadeBy;
    }
    
    public int getRawFlag() {
        return this.rawFlag;
    }
    
    public void setRawFlag(final int rawFlag) {
        this.rawFlag = rawFlag;
    }
    
    public NameSource getNameSource() {
        return this.nameSource;
    }
    
    public void setNameSource(final NameSource nameSource) {
        this.nameSource = nameSource;
    }
    
    public CommentSource getCommentSource() {
        return this.commentSource;
    }
    
    public void setCommentSource(final CommentSource commentSource) {
        this.commentSource = commentSource;
    }
    
    static {
        EMPTY = new byte[0];
        noExtraFields = new ZipExtraField[0];
    }
    
    public enum NameSource
    {
        NAME, 
        NAME_WITH_EFS_FLAG, 
        UNICODE_EXTRA_FIELD;
    }
    
    public enum CommentSource
    {
        COMMENT, 
        UNICODE_EXTRA_FIELD;
    }
}
