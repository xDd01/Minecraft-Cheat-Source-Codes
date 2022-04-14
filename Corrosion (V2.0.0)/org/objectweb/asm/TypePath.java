/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.ByteVector;

public final class TypePath {
    public static final int ARRAY_ELEMENT = 0;
    public static final int INNER_TYPE = 1;
    public static final int WILDCARD_BOUND = 2;
    public static final int TYPE_ARGUMENT = 3;
    private final byte[] typePathContainer;
    private final int typePathOffset;

    TypePath(byte[] typePathContainer, int typePathOffset) {
        this.typePathContainer = typePathContainer;
        this.typePathOffset = typePathOffset;
    }

    public int getLength() {
        return this.typePathContainer[this.typePathOffset];
    }

    public int getStep(int index) {
        return this.typePathContainer[this.typePathOffset + 2 * index + 1];
    }

    public int getStepArgument(int index) {
        return this.typePathContainer[this.typePathOffset + 2 * index + 2];
    }

    public static TypePath fromString(String typePath) {
        if (typePath == null || typePath.length() == 0) {
            return null;
        }
        int typePathLength = typePath.length();
        ByteVector output = new ByteVector(typePathLength);
        output.putByte(0);
        int typePathIndex = 0;
        while (typePathIndex < typePathLength) {
            char c2;
            if ((c2 = typePath.charAt(typePathIndex++)) == '[') {
                output.put11(0, 0);
                continue;
            }
            if (c2 == '.') {
                output.put11(1, 0);
                continue;
            }
            if (c2 == '*') {
                output.put11(2, 0);
                continue;
            }
            if (c2 >= '0' && c2 <= '9') {
                int typeArg = c2 - 48;
                while (typePathIndex < typePathLength) {
                    if ((c2 = typePath.charAt(typePathIndex++)) >= '0' && c2 <= '9') {
                        typeArg = typeArg * 10 + c2 - 48;
                        continue;
                    }
                    if (c2 == ';') break;
                    throw new IllegalArgumentException();
                }
                output.put11(3, typeArg);
                continue;
            }
            throw new IllegalArgumentException();
        }
        output.data[0] = (byte)(output.length / 2);
        return new TypePath(output.data, 0);
    }

    public String toString() {
        int length = this.getLength();
        StringBuilder result = new StringBuilder(length * 2);
        block6: for (int i2 = 0; i2 < length; ++i2) {
            switch (this.getStep(i2)) {
                case 0: {
                    result.append('[');
                    continue block6;
                }
                case 1: {
                    result.append('.');
                    continue block6;
                }
                case 2: {
                    result.append('*');
                    continue block6;
                }
                case 3: {
                    result.append(this.getStepArgument(i2)).append(';');
                    continue block6;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        return result.toString();
    }

    static void put(TypePath typePath, ByteVector output) {
        if (typePath == null) {
            output.putByte(0);
        } else {
            int length = typePath.typePathContainer[typePath.typePathOffset] * 2 + 1;
            output.putByteArray(typePath.typePathContainer, typePath.typePathOffset, length);
        }
    }
}

