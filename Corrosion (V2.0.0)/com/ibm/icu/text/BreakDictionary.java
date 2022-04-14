/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.util.CompactByteArray;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

class BreakDictionary {
    private char[] reverseColumnMap = null;
    private CompactByteArray columnMap = null;
    private int numCols;
    private short[] table = null;
    private short[] rowIndex = null;
    private int[] rowIndexFlags = null;
    private short[] rowIndexFlagsIndex = null;
    private byte[] rowIndexShifts = null;

    static void writeToFile(String inFile, String outFile) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        BreakDictionary dictionary = new BreakDictionary(new FileInputStream(inFile));
        PrintWriter out = null;
        if (outFile != null) {
            out = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(outFile), "UnicodeLittle"));
        }
        dictionary.printWordList("", 0, out);
        if (out != null) {
            out.close();
        }
    }

    void printWordList(String partialWord, int state, PrintWriter out) throws IOException {
        if (state == 65535) {
            System.out.println(partialWord);
            if (out != null) {
                out.println(partialWord);
            }
        } else {
            for (int i2 = 0; i2 < this.numCols; ++i2) {
                int newState = this.at(state, i2) & 0xFFFF;
                if (newState == 0) continue;
                char newChar = this.reverseColumnMap[i2];
                String newPartialWord = partialWord;
                if (newChar != '\u0000') {
                    newPartialWord = newPartialWord + newChar;
                }
                this.printWordList(newPartialWord, newState, out);
            }
        }
    }

    BreakDictionary(InputStream dictionaryStream) throws IOException {
        this.readDictionaryFile(new DataInputStream(dictionaryStream));
    }

    void readDictionaryFile(DataInputStream in2) throws IOException {
        int i2;
        in2.readInt();
        int l2 = in2.readInt();
        char[] temp = new char[l2];
        for (int i3 = 0; i3 < temp.length; ++i3) {
            temp[i3] = (char)in2.readShort();
        }
        l2 = in2.readInt();
        byte[] temp2 = new byte[l2];
        for (i2 = 0; i2 < temp2.length; ++i2) {
            temp2[i2] = in2.readByte();
        }
        this.columnMap = new CompactByteArray(temp, temp2);
        this.numCols = in2.readInt();
        in2.readInt();
        l2 = in2.readInt();
        this.rowIndex = new short[l2];
        for (i2 = 0; i2 < this.rowIndex.length; ++i2) {
            this.rowIndex[i2] = in2.readShort();
        }
        l2 = in2.readInt();
        this.rowIndexFlagsIndex = new short[l2];
        for (i2 = 0; i2 < this.rowIndexFlagsIndex.length; ++i2) {
            this.rowIndexFlagsIndex[i2] = in2.readShort();
        }
        l2 = in2.readInt();
        this.rowIndexFlags = new int[l2];
        for (i2 = 0; i2 < this.rowIndexFlags.length; ++i2) {
            this.rowIndexFlags[i2] = in2.readInt();
        }
        l2 = in2.readInt();
        this.rowIndexShifts = new byte[l2];
        for (i2 = 0; i2 < this.rowIndexShifts.length; ++i2) {
            this.rowIndexShifts[i2] = in2.readByte();
        }
        l2 = in2.readInt();
        this.table = new short[l2];
        for (i2 = 0; i2 < this.table.length; ++i2) {
            this.table[i2] = in2.readShort();
        }
        this.reverseColumnMap = new char[this.numCols];
        for (char c2 = '\u0000'; c2 < '\uffff'; c2 = (char)(c2 + '\u0001')) {
            byte col = this.columnMap.elementAt(c2);
            if (col == 0) continue;
            this.reverseColumnMap[col] = c2;
        }
        in2.close();
    }

    final short at(int row, char ch) {
        byte col = this.columnMap.elementAt(ch);
        return this.at(row, col);
    }

    final short at(int row, int col) {
        if (this.cellIsPopulated(row, col)) {
            return this.internalAt(this.rowIndex[row], col + this.rowIndexShifts[row]);
        }
        return 0;
    }

    private final boolean cellIsPopulated(int row, int col) {
        if (this.rowIndexFlagsIndex[row] < 0) {
            return col == -this.rowIndexFlagsIndex[row];
        }
        int flags = this.rowIndexFlags[this.rowIndexFlagsIndex[row] + (col >> 5)];
        return (flags & 1 << (col & 0x1F)) != 0;
    }

    private final short internalAt(int row, int col) {
        return this.table[row * this.numCols + col];
    }
}

