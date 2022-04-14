package org.apache.commons.io.filefilter;

import java.io.*;

public class HiddenFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = 8930842316112759062L;
    public static final IOFileFilter HIDDEN;
    public static final IOFileFilter VISIBLE;
    
    protected HiddenFileFilter() {
    }
    
    @Override
    public boolean accept(final File file) {
        return file.isHidden();
    }
    
    static {
        HIDDEN = new HiddenFileFilter();
        VISIBLE = new NotFileFilter(HiddenFileFilter.HIDDEN);
    }
}
