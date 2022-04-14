package org.apache.commons.compress.compressors.lz77support;

public final class Parameters
{
    public static final int TRUE_MIN_BACK_REFERENCE_LENGTH = 3;
    private final int windowSize;
    private final int minBackReferenceLength;
    private final int maxBackReferenceLength;
    private final int maxOffset;
    private final int maxLiteralLength;
    private final int niceBackReferenceLength;
    private final int maxCandidates;
    private final int lazyThreshold;
    private final boolean lazyMatching;
    
    public static Builder builder(final int windowSize) {
        return new Builder(windowSize);
    }
    
    private Parameters(final int windowSize, final int minBackReferenceLength, final int maxBackReferenceLength, final int maxOffset, final int maxLiteralLength, final int niceBackReferenceLength, final int maxCandidates, final boolean lazyMatching, final int lazyThreshold) {
        this.windowSize = windowSize;
        this.minBackReferenceLength = minBackReferenceLength;
        this.maxBackReferenceLength = maxBackReferenceLength;
        this.maxOffset = maxOffset;
        this.maxLiteralLength = maxLiteralLength;
        this.niceBackReferenceLength = niceBackReferenceLength;
        this.maxCandidates = maxCandidates;
        this.lazyMatching = lazyMatching;
        this.lazyThreshold = lazyThreshold;
    }
    
    public int getWindowSize() {
        return this.windowSize;
    }
    
    public int getMinBackReferenceLength() {
        return this.minBackReferenceLength;
    }
    
    public int getMaxBackReferenceLength() {
        return this.maxBackReferenceLength;
    }
    
    public int getMaxOffset() {
        return this.maxOffset;
    }
    
    public int getMaxLiteralLength() {
        return this.maxLiteralLength;
    }
    
    public int getNiceBackReferenceLength() {
        return this.niceBackReferenceLength;
    }
    
    public int getMaxCandidates() {
        return this.maxCandidates;
    }
    
    public boolean getLazyMatching() {
        return this.lazyMatching;
    }
    
    public int getLazyMatchingThreshold() {
        return this.lazyThreshold;
    }
    
    private static final boolean isPowerOfTwo(final int x) {
        return (x & x - 1) == 0x0;
    }
    
    public static class Builder
    {
        private final int windowSize;
        private int minBackReferenceLength;
        private int maxBackReferenceLength;
        private int maxOffset;
        private int maxLiteralLength;
        private Integer niceBackReferenceLength;
        private Integer maxCandidates;
        private Integer lazyThreshold;
        private Boolean lazyMatches;
        
        private Builder(final int windowSize) {
            if (windowSize < 2 || !isPowerOfTwo(windowSize)) {
                throw new IllegalArgumentException("windowSize must be a power of two");
            }
            this.windowSize = windowSize;
            this.minBackReferenceLength = 3;
            this.maxBackReferenceLength = windowSize - 1;
            this.maxOffset = windowSize - 1;
            this.maxLiteralLength = windowSize;
        }
        
        public Builder withMinBackReferenceLength(final int minBackReferenceLength) {
            this.minBackReferenceLength = Math.max(3, minBackReferenceLength);
            if (this.windowSize < this.minBackReferenceLength) {
                throw new IllegalArgumentException("minBackReferenceLength can't be bigger than windowSize");
            }
            if (this.maxBackReferenceLength < this.minBackReferenceLength) {
                this.maxBackReferenceLength = this.minBackReferenceLength;
            }
            return this;
        }
        
        public Builder withMaxBackReferenceLength(final int maxBackReferenceLength) {
            this.maxBackReferenceLength = ((maxBackReferenceLength < this.minBackReferenceLength) ? this.minBackReferenceLength : Math.min(maxBackReferenceLength, this.windowSize - 1));
            return this;
        }
        
        public Builder withMaxOffset(final int maxOffset) {
            this.maxOffset = ((maxOffset < 1) ? (this.windowSize - 1) : Math.min(maxOffset, this.windowSize - 1));
            return this;
        }
        
        public Builder withMaxLiteralLength(final int maxLiteralLength) {
            this.maxLiteralLength = ((maxLiteralLength < 1) ? this.windowSize : Math.min(maxLiteralLength, this.windowSize));
            return this;
        }
        
        public Builder withNiceBackReferenceLength(final int niceLen) {
            this.niceBackReferenceLength = niceLen;
            return this;
        }
        
        public Builder withMaxNumberOfCandidates(final int maxCandidates) {
            this.maxCandidates = maxCandidates;
            return this;
        }
        
        public Builder withLazyMatching(final boolean lazy) {
            this.lazyMatches = lazy;
            return this;
        }
        
        public Builder withLazyThreshold(final int threshold) {
            this.lazyThreshold = threshold;
            return this;
        }
        
        public Builder tunedForSpeed() {
            this.niceBackReferenceLength = Math.max(this.minBackReferenceLength, this.maxBackReferenceLength / 8);
            this.maxCandidates = Math.max(32, this.windowSize / 1024);
            this.lazyMatches = false;
            this.lazyThreshold = this.minBackReferenceLength;
            return this;
        }
        
        public Builder tunedForCompressionRatio() {
            final Integer value = this.maxBackReferenceLength;
            this.lazyThreshold = value;
            this.niceBackReferenceLength = value;
            this.maxCandidates = Math.max(32, this.windowSize / 16);
            this.lazyMatches = true;
            return this;
        }
        
        public Parameters build() {
            final int niceLen = (this.niceBackReferenceLength != null) ? this.niceBackReferenceLength : Math.max(this.minBackReferenceLength, this.maxBackReferenceLength / 2);
            final int candidates = (this.maxCandidates != null) ? this.maxCandidates : Math.max(256, this.windowSize / 128);
            final boolean lazy = this.lazyMatches == null || this.lazyMatches;
            final int threshold = lazy ? ((this.lazyThreshold != null) ? this.lazyThreshold : niceLen) : this.minBackReferenceLength;
            return new Parameters(this.windowSize, this.minBackReferenceLength, this.maxBackReferenceLength, this.maxOffset, this.maxLiteralLength, niceLen, candidates, lazy, threshold, null);
        }
    }
}
