package org.apache.commons.compress.archivers.zip;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.io.*;
import org.apache.commons.compress.parallel.*;

public class ParallelScatterZipCreator
{
    private final List<ScatterZipOutputStream> streams;
    private final ExecutorService es;
    private final ScatterGatherBackingStoreSupplier backingStoreSupplier;
    private final List<Future<Object>> futures;
    private final long startedAt;
    private long compressionDoneAt;
    private long scatterDoneAt;
    private final ThreadLocal<ScatterZipOutputStream> tlScatterStreams;
    
    private ScatterZipOutputStream createDeferred(final ScatterGatherBackingStoreSupplier scatterGatherBackingStoreSupplier) throws IOException {
        final ScatterGatherBackingStore bs = scatterGatherBackingStoreSupplier.get();
        final StreamCompressor sc = StreamCompressor.create(-1, bs);
        return new ScatterZipOutputStream(bs, sc);
    }
    
    public ParallelScatterZipCreator() {
        this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }
    
    public ParallelScatterZipCreator(final ExecutorService executorService) {
        this(executorService, new DefaultBackingStoreSupplier());
    }
    
    public ParallelScatterZipCreator(final ExecutorService executorService, final ScatterGatherBackingStoreSupplier backingStoreSupplier) {
        this.streams = Collections.synchronizedList(new ArrayList<ScatterZipOutputStream>());
        this.futures = new ArrayList<Future<Object>>();
        this.startedAt = System.currentTimeMillis();
        this.compressionDoneAt = 0L;
        this.tlScatterStreams = new ThreadLocal<ScatterZipOutputStream>() {
            @Override
            protected ScatterZipOutputStream initialValue() {
                try {
                    final ScatterZipOutputStream scatterStream = ParallelScatterZipCreator.this.createDeferred(ParallelScatterZipCreator.this.backingStoreSupplier);
                    ParallelScatterZipCreator.this.streams.add(scatterStream);
                    return scatterStream;
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        this.backingStoreSupplier = backingStoreSupplier;
        this.es = executorService;
    }
    
    public void addArchiveEntry(final ZipArchiveEntry zipArchiveEntry, final InputStreamSupplier source) {
        this.submit(this.createCallable(zipArchiveEntry, source));
    }
    
    public void addArchiveEntry(final ZipArchiveEntryRequestSupplier zipArchiveEntryRequestSupplier) {
        this.submit(this.createCallable(zipArchiveEntryRequestSupplier));
    }
    
    public final void submit(final Callable<Object> callable) {
        this.futures.add(this.es.submit(callable));
    }
    
    public final Callable<Object> createCallable(final ZipArchiveEntry zipArchiveEntry, final InputStreamSupplier source) {
        final int method = zipArchiveEntry.getMethod();
        if (method == -1) {
            throw new IllegalArgumentException("Method must be set on zipArchiveEntry: " + zipArchiveEntry);
        }
        final ZipArchiveEntryRequest zipArchiveEntryRequest = ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, source);
        return new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                ParallelScatterZipCreator.this.tlScatterStreams.get().addArchiveEntry(zipArchiveEntryRequest);
                return null;
            }
        };
    }
    
    public final Callable<Object> createCallable(final ZipArchiveEntryRequestSupplier zipArchiveEntryRequestSupplier) {
        return new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                ParallelScatterZipCreator.this.tlScatterStreams.get().addArchiveEntry(zipArchiveEntryRequestSupplier.get());
                return null;
            }
        };
    }
    
    public void writeTo(final ZipArchiveOutputStream targetStream) throws IOException, InterruptedException, ExecutionException {
        try {
            for (final Future<?> future : this.futures) {
                future.get();
            }
        }
        finally {
            this.es.shutdown();
        }
        this.es.awaitTermination(60000L, TimeUnit.SECONDS);
        this.compressionDoneAt = System.currentTimeMillis();
        synchronized (this.streams) {
            for (final ScatterZipOutputStream scatterStream : this.streams) {
                scatterStream.writeTo(targetStream);
                scatterStream.close();
            }
        }
        this.scatterDoneAt = System.currentTimeMillis();
    }
    
    public ScatterStatistics getStatisticsMessage() {
        return new ScatterStatistics(this.compressionDoneAt - this.startedAt, this.scatterDoneAt - this.compressionDoneAt);
    }
    
    private static class DefaultBackingStoreSupplier implements ScatterGatherBackingStoreSupplier
    {
        final AtomicInteger storeNum;
        
        private DefaultBackingStoreSupplier() {
            this.storeNum = new AtomicInteger(0);
        }
        
        @Override
        public ScatterGatherBackingStore get() throws IOException {
            final File tempFile = File.createTempFile("parallelscatter", "n" + this.storeNum.incrementAndGet());
            return new FileBasedScatterGatherBackingStore(tempFile);
        }
    }
}
