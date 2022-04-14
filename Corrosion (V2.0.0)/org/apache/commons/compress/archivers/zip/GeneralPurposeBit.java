/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.archivers.zip.ZipShort;

public final class GeneralPurposeBit {
    private static final int ENCRYPTION_FLAG = 1;
    private static final int SLIDING_DICTIONARY_SIZE_FLAG = 2;
    private static final int NUMBER_OF_SHANNON_FANO_TREES_FLAG = 4;
    private static final int DATA_DESCRIPTOR_FLAG = 8;
    private static final int STRONG_ENCRYPTION_FLAG = 64;
    public static final int UFT8_NAMES_FLAG = 2048;
    private boolean languageEncodingFlag = false;
    private boolean dataDescriptorFlag = false;
    private boolean encryptionFlag = false;
    private boolean strongEncryptionFlag = false;
    private int slidingDictionarySize;
    private int numberOfShannonFanoTrees;

    public boolean usesUTF8ForNames() {
        return this.languageEncodingFlag;
    }

    public void useUTF8ForNames(boolean b2) {
        this.languageEncodingFlag = b2;
    }

    public boolean usesDataDescriptor() {
        return this.dataDescriptorFlag;
    }

    public void useDataDescriptor(boolean b2) {
        this.dataDescriptorFlag = b2;
    }

    public boolean usesEncryption() {
        return this.encryptionFlag;
    }

    public void useEncryption(boolean b2) {
        this.encryptionFlag = b2;
    }

    public boolean usesStrongEncryption() {
        return this.encryptionFlag && this.strongEncryptionFlag;
    }

    public void useStrongEncryption(boolean b2) {
        this.strongEncryptionFlag = b2;
        if (b2) {
            this.useEncryption(true);
        }
    }

    int getSlidingDictionarySize() {
        return this.slidingDictionarySize;
    }

    int getNumberOfShannonFanoTrees() {
        return this.numberOfShannonFanoTrees;
    }

    public byte[] encode() {
        return ZipShort.getBytes((this.dataDescriptorFlag ? 8 : 0) | (this.languageEncodingFlag ? 2048 : 0) | (this.encryptionFlag ? 1 : 0) | (this.strongEncryptionFlag ? 64 : 0));
    }

    public static GeneralPurposeBit parse(byte[] data, int offset) {
        int generalPurposeFlag = ZipShort.getValue(data, offset);
        GeneralPurposeBit b2 = new GeneralPurposeBit();
        b2.useDataDescriptor((generalPurposeFlag & 8) != 0);
        b2.useUTF8ForNames((generalPurposeFlag & 0x800) != 0);
        b2.useStrongEncryption((generalPurposeFlag & 0x40) != 0);
        b2.useEncryption((generalPurposeFlag & 1) != 0);
        b2.slidingDictionarySize = (generalPurposeFlag & 2) != 0 ? 8192 : 4096;
        b2.numberOfShannonFanoTrees = (generalPurposeFlag & 4) != 0 ? 3 : 2;
        return b2;
    }

    public int hashCode() {
        return 3 * (7 * (13 * (17 * (this.encryptionFlag ? 1 : 0) + (this.strongEncryptionFlag ? 1 : 0)) + (this.languageEncodingFlag ? 1 : 0)) + (this.dataDescriptorFlag ? 1 : 0));
    }

    public boolean equals(Object o2) {
        if (!(o2 instanceof GeneralPurposeBit)) {
            return false;
        }
        GeneralPurposeBit g2 = (GeneralPurposeBit)o2;
        return g2.encryptionFlag == this.encryptionFlag && g2.strongEncryptionFlag == this.strongEncryptionFlag && g2.languageEncodingFlag == this.languageEncodingFlag && g2.dataDescriptorFlag == this.dataDescriptorFlag;
    }
}

