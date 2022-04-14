/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ModuleHashesAttribute
extends Attribute {
    public String algorithm;
    public List<String> modules;
    public List<byte[]> hashes;

    public ModuleHashesAttribute(String algorithm, List<String> modules, List<byte[]> hashes) {
        super("ModuleHashes");
        this.algorithm = algorithm;
        this.modules = modules;
        this.hashes = hashes;
    }

    public ModuleHashesAttribute() {
        this(null, null, null);
    }

    @Override
    protected Attribute read(ClassReader classReader, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
        int currentOffset = offset;
        String hashAlgorithm = classReader.readUTF8(currentOffset, charBuffer);
        int numModules = classReader.readUnsignedShort(currentOffset += 2);
        currentOffset += 2;
        ArrayList<String> moduleList = new ArrayList<String>(numModules);
        ArrayList<byte[]> hashList = new ArrayList<byte[]>(numModules);
        for (int i2 = 0; i2 < numModules; ++i2) {
            String module = classReader.readModule(currentOffset, charBuffer);
            moduleList.add(module);
            int hashLength = classReader.readUnsignedShort(currentOffset += 2);
            currentOffset += 2;
            byte[] hash = new byte[hashLength];
            for (int j2 = 0; j2 < hashLength; ++j2) {
                hash[j2] = (byte)classReader.readByte(currentOffset);
                ++currentOffset;
            }
            hashList.add(hash);
        }
        return new ModuleHashesAttribute(hashAlgorithm, moduleList, hashList);
    }

    @Override
    protected ByteVector write(ClassWriter classWriter, byte[] code, int codeLength, int maxStack, int maxLocals) {
        ByteVector byteVector = new ByteVector();
        byteVector.putShort(classWriter.newUTF8(this.algorithm));
        if (this.modules == null) {
            byteVector.putShort(0);
        } else {
            int numModules = this.modules.size();
            byteVector.putShort(numModules);
            for (int i2 = 0; i2 < numModules; ++i2) {
                String module = this.modules.get(i2);
                byte[] hash = this.hashes.get(i2);
                byteVector.putShort(classWriter.newModule(module)).putShort(hash.length).putByteArray(hash, 0, hash.length);
            }
        }
        return byteVector;
    }
}

