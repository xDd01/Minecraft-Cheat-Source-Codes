package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.parallel.*;
import java.io.*;

public class ZipArchiveEntryRequest
{
    private final ZipArchiveEntry zipArchiveEntry;
    private final InputStreamSupplier payloadSupplier;
    private final int method;
    
    private ZipArchiveEntryRequest(final ZipArchiveEntry zipArchiveEntry, final InputStreamSupplier payloadSupplier) {
        this.zipArchiveEntry = zipArchiveEntry;
        this.payloadSupplier = payloadSupplier;
        this.method = zipArchiveEntry.getMethod();
    }
    
    public static ZipArchiveEntryRequest createZipArchiveEntryRequest(final ZipArchiveEntry zipArchiveEntry, final InputStreamSupplier payloadSupplier) {
        return new ZipArchiveEntryRequest(zipArchiveEntry, payloadSupplier);
    }
    
    public InputStream getPayloadStream() {
        return this.payloadSupplier.get();
    }
    
    public int getMethod() {
        return this.method;
    }
    
    ZipArchiveEntry getZipArchiveEntry() {
        return this.zipArchiveEntry;
    }
}
