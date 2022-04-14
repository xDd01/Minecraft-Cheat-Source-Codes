package org.newdawn.slick.muffin;

import java.util.*;
import java.io.*;
import org.newdawn.slick.util.*;

public class FileMuffin implements Muffin
{
    @Override
    public void saveFile(final HashMap scoreMap, final String fileName) throws IOException {
        final String userHome = System.getProperty("user.home");
        File file = new File(userHome);
        file = new File(file, ".java");
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(file, fileName);
        final FileOutputStream fos = new FileOutputStream(file);
        final ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(scoreMap);
        oos.close();
    }
    
    @Override
    public HashMap loadFile(final String fileName) throws IOException {
        HashMap hashMap = new HashMap();
        final String userHome = System.getProperty("user.home");
        File file = new File(userHome);
        file = new File(file, ".java");
        file = new File(file, fileName);
        if (file.exists()) {
            try {
                final FileInputStream fis = new FileInputStream(file);
                final ObjectInputStream ois = new ObjectInputStream(fis);
                hashMap = (HashMap)ois.readObject();
                ois.close();
            }
            catch (EOFException e2) {}
            catch (ClassNotFoundException e) {
                Log.error(e);
                throw new IOException("Failed to pull state from store - class not found");
            }
        }
        return hashMap;
    }
}
