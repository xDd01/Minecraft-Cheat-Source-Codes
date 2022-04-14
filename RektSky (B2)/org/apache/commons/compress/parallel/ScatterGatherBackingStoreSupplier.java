package org.apache.commons.compress.parallel;

import java.io.*;

public interface ScatterGatherBackingStoreSupplier
{
    ScatterGatherBackingStore get() throws IOException;
}
