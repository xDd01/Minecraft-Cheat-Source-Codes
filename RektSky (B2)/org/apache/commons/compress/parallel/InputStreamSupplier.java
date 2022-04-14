package org.apache.commons.compress.parallel;

import java.io.*;

public interface InputStreamSupplier
{
    InputStream get();
}
