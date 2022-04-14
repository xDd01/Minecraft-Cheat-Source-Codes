package org.apache.commons.compress.archivers.sevenz;

class StreamMap
{
    int[] folderFirstPackStreamIndex;
    long[] packStreamOffsets;
    int[] folderFirstFileIndex;
    int[] fileFolderIndex;
    
    @Override
    public String toString() {
        return "StreamMap with indices of " + this.folderFirstPackStreamIndex.length + " folders, offsets of " + this.packStreamOffsets.length + " packed streams, first files of " + this.folderFirstFileIndex.length + " folders and folder indices for " + this.fileFolderIndex.length + " files";
    }
}
