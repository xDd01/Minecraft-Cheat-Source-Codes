/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.signature;

import org.objectweb.asm.signature.SignatureVisitor;

public class SignatureReader {
    private final String signatureValue;

    public SignatureReader(String signature) {
        this.signatureValue = signature;
    }

    public void accept(SignatureVisitor signatureVistor) {
        int offset;
        String signature = this.signatureValue;
        int length = signature.length();
        if (signature.charAt(0) == '<') {
            char currentChar;
            offset = 2;
            do {
                int classBoundStartOffset = signature.indexOf(58, offset);
                signatureVistor.visitFormalTypeParameter(signature.substring(offset - 1, classBoundStartOffset));
                offset = classBoundStartOffset + 1;
                currentChar = signature.charAt(offset);
                if (currentChar == 'L' || currentChar == '[' || currentChar == 'T') {
                    offset = SignatureReader.parseType(signature, offset, signatureVistor.visitClassBound());
                }
                while ((currentChar = signature.charAt(offset++)) == ':') {
                    offset = SignatureReader.parseType(signature, offset, signatureVistor.visitInterfaceBound());
                }
            } while (currentChar != '>');
        } else {
            offset = 0;
        }
        if (signature.charAt(offset) == '(') {
            ++offset;
            while (signature.charAt(offset) != ')') {
                offset = SignatureReader.parseType(signature, offset, signatureVistor.visitParameterType());
            }
            offset = SignatureReader.parseType(signature, offset + 1, signatureVistor.visitReturnType());
            while (offset < length) {
                offset = SignatureReader.parseType(signature, offset + 1, signatureVistor.visitExceptionType());
            }
        } else {
            offset = SignatureReader.parseType(signature, offset, signatureVistor.visitSuperclass());
            while (offset < length) {
                offset = SignatureReader.parseType(signature, offset, signatureVistor.visitInterface());
            }
        }
    }

    public void acceptType(SignatureVisitor signatureVisitor) {
        SignatureReader.parseType(this.signatureValue, 0, signatureVisitor);
    }

    /*
     * Unable to fully structure code
     */
    private static int parseType(String signature, int startOffset, SignatureVisitor signatureVisitor) {
        offset = startOffset;
        currentChar = signature.charAt(offset++);
        switch (currentChar) {
            case 'B': 
            case 'C': 
            case 'D': 
            case 'F': 
            case 'I': 
            case 'J': 
            case 'S': 
            case 'V': 
            case 'Z': {
                signatureVisitor.visitBaseType(currentChar);
                return offset;
            }
            case '[': {
                return SignatureReader.parseType(signature, offset, signatureVisitor.visitArrayType());
            }
            case 'T': {
                endOffset = signature.indexOf(59, offset);
                signatureVisitor.visitTypeVariable(signature.substring(offset, endOffset));
                return endOffset + 1;
            }
            case 'L': {
                start = offset;
                visited = false;
                inner = false;
                while (true) {
                    if ((currentChar = signature.charAt(offset++)) == '.' || currentChar == ';') {
                        if (!visited) {
                            name = signature.substring(start, offset - 1);
                            if (inner) {
                                signatureVisitor.visitInnerClassType(name);
                            } else {
                                signatureVisitor.visitClassType(name);
                            }
                        }
                        if (currentChar == ';') break;
                        start = offset;
                        visited = false;
                        inner = true;
                        continue;
                    }
                    if (currentChar != '<') continue;
                    name = signature.substring(start, offset - 1);
                    if (inner) {
                        signatureVisitor.visitInnerClassType(name);
                    } else {
                        signatureVisitor.visitClassType(name);
                    }
                    visited = true;
                    block11: while (true) {
                        if ((currentChar = signature.charAt(offset)) == '>') ** break;
                        switch (currentChar) {
                            case '*': {
                                ++offset;
                                signatureVisitor.visitTypeArgument();
                                continue block11;
                            }
                            case '+': 
                            case '-': {
                                offset = SignatureReader.parseType(signature, offset + 1, signatureVisitor.visitTypeArgument(currentChar));
                                continue block11;
                            }
                        }
                        offset = SignatureReader.parseType(signature, offset, signatureVisitor.visitTypeArgument('='));
                    }
                    break;
                }
                signatureVisitor.visitEnd();
                return offset;
            }
        }
        throw new IllegalArgumentException();
    }
}

