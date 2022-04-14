package org.lwjgl;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;

abstract class J2SESysImplementation extends DefaultSysImplementation
{
    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }
    
    @Override
    public void alert(final String title, final String message) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            LWJGLUtil.log("Caught exception while setting LAF: " + e);
        }
        JOptionPane.showMessageDialog(null, message, title, 2);
    }
    
    @Override
    public String getClipboard() {
        try {
            final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            final Transferable transferable = clipboard.getContents(null);
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception e) {
            LWJGLUtil.log("Exception while getting clipboard: " + e);
        }
        return null;
    }
}
