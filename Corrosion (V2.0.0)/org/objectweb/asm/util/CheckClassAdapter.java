/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.objectweb.asm.util.CheckAnnotationAdapter;
import org.objectweb.asm.util.CheckFieldAdapter;
import org.objectweb.asm.util.CheckMethodAdapter;
import org.objectweb.asm.util.CheckModuleAdapter;
import org.objectweb.asm.util.CheckRecordComponentAdapter;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CheckClassAdapter
extends ClassVisitor {
    private static final String USAGE = "Verifies the given class.\nUsage: CheckClassAdapter <fully qualified class name or class file name>";
    private static final String ERROR_AT = ": error at index ";
    private boolean checkDataFlow;
    private int version;
    private boolean visitCalled;
    private boolean visitModuleCalled;
    private boolean visitSourceCalled;
    private boolean visitOuterClassCalled;
    private boolean visitNestHostCalled;
    private String nestMemberPackageName;
    private boolean visitEndCalled;
    private Map<Label, Integer> labelInsnIndices = new HashMap<Label, Integer>();

    public CheckClassAdapter(ClassVisitor classVisitor) {
        this(classVisitor, true);
    }

    public CheckClassAdapter(ClassVisitor classVisitor, boolean checkDataFlow) {
        this(589824, classVisitor, checkDataFlow);
        if (this.getClass() != CheckClassAdapter.class) {
            throw new IllegalStateException();
        }
    }

    protected CheckClassAdapter(int api2, ClassVisitor classVisitor, boolean checkDataFlow) {
        super(api2, classVisitor);
        this.checkDataFlow = checkDataFlow;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (this.visitCalled) {
            throw new IllegalStateException("visit must be called only once");
        }
        this.visitCalled = true;
        this.checkState();
        CheckClassAdapter.checkAccess(access, 259633);
        if (name == null) {
            throw new IllegalArgumentException("Illegal class name (null)");
        }
        if (!name.endsWith("package-info") && !name.endsWith("module-info")) {
            CheckMethodAdapter.checkInternalName(version, name, "class name");
        }
        if ("java/lang/Object".equals(name)) {
            if (superName != null) {
                throw new IllegalArgumentException("The super class name of the Object class must be 'null'");
            }
        } else if (name.endsWith("module-info")) {
            if (superName != null) {
                throw new IllegalArgumentException("The super class name of a module-info class must be 'null'");
            }
        } else {
            CheckMethodAdapter.checkInternalName(version, superName, "super class name");
        }
        if (signature != null) {
            CheckClassAdapter.checkClassSignature(signature);
        }
        if ((access & 0x200) != 0 && !"java/lang/Object".equals(superName)) {
            throw new IllegalArgumentException("The super class name of interfaces must be 'java/lang/Object'");
        }
        if (interfaces != null) {
            for (int i2 = 0; i2 < interfaces.length; ++i2) {
                CheckMethodAdapter.checkInternalName(version, interfaces[i2], "interface name at index " + i2);
            }
        }
        this.version = version;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitSource(String file, String debug) {
        this.checkState();
        if (this.visitSourceCalled) {
            throw new IllegalStateException("visitSource can be called only once.");
        }
        this.visitSourceCalled = true;
        super.visitSource(file, debug);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        this.checkState();
        if (this.visitModuleCalled) {
            throw new IllegalStateException("visitModule can be called only once.");
        }
        this.visitModuleCalled = true;
        CheckClassAdapter.checkFullyQualifiedName(this.version, name, "module name");
        CheckClassAdapter.checkAccess(access, 36896);
        CheckModuleAdapter checkModuleAdapter = new CheckModuleAdapter(this.api, super.visitModule(name, access, version), (access & 0x20) != 0);
        checkModuleAdapter.classVersion = this.version;
        return checkModuleAdapter;
    }

    @Override
    public void visitNestHost(String nestHost) {
        this.checkState();
        CheckMethodAdapter.checkInternalName(this.version, nestHost, "nestHost");
        if (this.visitNestHostCalled) {
            throw new IllegalStateException("visitNestHost can be called only once.");
        }
        if (this.nestMemberPackageName != null) {
            throw new IllegalStateException("visitNestHost and visitNestMember are mutually exclusive.");
        }
        this.visitNestHostCalled = true;
        super.visitNestHost(nestHost);
    }

    @Override
    public void visitNestMember(String nestMember) {
        this.checkState();
        CheckMethodAdapter.checkInternalName(this.version, nestMember, "nestMember");
        if (this.visitNestHostCalled) {
            throw new IllegalStateException("visitMemberOfNest and visitNestHost are mutually exclusive.");
        }
        String packageName = CheckClassAdapter.packageName(nestMember);
        if (this.nestMemberPackageName == null) {
            this.nestMemberPackageName = packageName;
        } else if (!this.nestMemberPackageName.equals(packageName)) {
            throw new IllegalStateException("nest member " + nestMember + " should be in the package " + this.nestMemberPackageName);
        }
        super.visitNestMember(nestMember);
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
        this.checkState();
        CheckMethodAdapter.checkInternalName(this.version, permittedSubclass, "permittedSubclass");
        super.visitPermittedSubclass(permittedSubclass);
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        this.checkState();
        if (this.visitOuterClassCalled) {
            throw new IllegalStateException("visitOuterClass can be called only once.");
        }
        this.visitOuterClassCalled = true;
        if (owner == null) {
            throw new IllegalArgumentException("Illegal outer class owner");
        }
        if (descriptor != null) {
            CheckMethodAdapter.checkMethodDescriptor(this.version, descriptor);
        }
        super.visitOuterClass(owner, name, descriptor);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        this.checkState();
        CheckMethodAdapter.checkInternalName(this.version, name, "class name");
        if (outerName != null) {
            CheckMethodAdapter.checkInternalName(this.version, outerName, "outer class name");
        }
        if (innerName != null) {
            int startIndex;
            for (startIndex = 0; startIndex < innerName.length() && Character.isDigit(innerName.charAt(startIndex)); ++startIndex) {
            }
            if (startIndex == 0 || startIndex < innerName.length()) {
                CheckMethodAdapter.checkIdentifier(this.version, innerName, startIndex, -1, "inner class name");
            }
        }
        CheckClassAdapter.checkAccess(access, 30239);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        this.checkState();
        CheckMethodAdapter.checkUnqualifiedName(this.version, name, "record component name");
        CheckMethodAdapter.checkDescriptor(this.version, descriptor, false);
        if (signature != null) {
            CheckClassAdapter.checkFieldSignature(signature);
        }
        return new CheckRecordComponentAdapter(this.api, super.visitRecordComponent(name, descriptor, signature));
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        this.checkState();
        CheckClassAdapter.checkAccess(access, 184543);
        CheckMethodAdapter.checkUnqualifiedName(this.version, name, "field name");
        CheckMethodAdapter.checkDescriptor(this.version, descriptor, false);
        if (signature != null) {
            CheckClassAdapter.checkFieldSignature(signature);
        }
        if (value != null) {
            CheckMethodAdapter.checkConstant(value);
        }
        return new CheckFieldAdapter(this.api, super.visitField(access, name, descriptor, signature, value));
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        this.checkState();
        CheckClassAdapter.checkMethodAccess(this.version, access, 171519);
        if (!"<init>".equals(name) && !"<clinit>".equals(name)) {
            CheckMethodAdapter.checkMethodIdentifier(this.version, name, "method name");
        }
        CheckMethodAdapter.checkMethodDescriptor(this.version, descriptor);
        if (signature != null) {
            CheckClassAdapter.checkMethodSignature(signature);
        }
        if (exceptions != null) {
            for (int i2 = 0; i2 < exceptions.length; ++i2) {
                CheckMethodAdapter.checkInternalName(this.version, exceptions[i2], "exception name at index " + i2);
            }
        }
        CheckMethodAdapter checkMethodAdapter = this.checkDataFlow ? new CheckMethodAdapter(this.api, access, name, descriptor, super.visitMethod(access, name, descriptor, signature, exceptions), this.labelInsnIndices) : new CheckMethodAdapter(this.api, super.visitMethod(access, name, descriptor, signature, exceptions), this.labelInsnIndices);
        checkMethodAdapter.version = this.version;
        return checkMethodAdapter;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        this.checkState();
        CheckMethodAdapter.checkDescriptor(this.version, descriptor, false);
        return new CheckAnnotationAdapter(super.visitAnnotation(descriptor, visible));
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        this.checkState();
        int sort = new TypeReference(typeRef).getSort();
        if (sort != 0 && sort != 17 && sort != 16) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(sort));
        }
        CheckClassAdapter.checkTypeRef(typeRef);
        CheckMethodAdapter.checkDescriptor(this.version, descriptor, false);
        return new CheckAnnotationAdapter(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        this.checkState();
        if (attribute == null) {
            throw new IllegalArgumentException("Invalid attribute (must not be null)");
        }
        super.visitAttribute(attribute);
    }

    @Override
    public void visitEnd() {
        this.checkState();
        this.visitEndCalled = true;
        super.visitEnd();
    }

    private void checkState() {
        if (!this.visitCalled) {
            throw new IllegalStateException("Cannot visit member before visit has been called.");
        }
        if (this.visitEndCalled) {
            throw new IllegalStateException("Cannot visit member after visitEnd has been called.");
        }
    }

    static void checkAccess(int access, int possibleAccess) {
        if ((access & ~possibleAccess) != 0) {
            throw new IllegalArgumentException("Invalid access flags: " + access);
        }
        int publicProtectedPrivate = 7;
        if (Integer.bitCount(access & publicProtectedPrivate) > 1) {
            throw new IllegalArgumentException("public, protected and private are mutually exclusive: " + access);
        }
        if (Integer.bitCount(access & 0x410) > 1) {
            throw new IllegalArgumentException("final and abstract are mutually exclusive: " + access);
        }
    }

    private static void checkMethodAccess(int version, int access, int possibleAccess) {
        CheckClassAdapter.checkAccess(access, possibleAccess);
        if ((version & 0xFFFF) < 61 && Integer.bitCount(access & 0xC00) > 1) {
            throw new IllegalArgumentException("strictfp and abstract are mutually exclusive: " + access);
        }
    }

    static void checkFullyQualifiedName(int version, String name, String source) {
        try {
            int dotIndex;
            int startIndex = 0;
            while ((dotIndex = name.indexOf(46, startIndex + 1)) != -1) {
                CheckMethodAdapter.checkIdentifier(version, name, startIndex, dotIndex, null);
                startIndex = dotIndex + 1;
            }
            CheckMethodAdapter.checkIdentifier(version, name, startIndex, name.length(), null);
        }
        catch (IllegalArgumentException e2) {
            throw new IllegalArgumentException("Invalid " + source + " (must be a fully qualified name): " + name, e2);
        }
    }

    public static void checkClassSignature(String signature) {
        int pos = 0;
        if (CheckClassAdapter.getChar(signature, 0) == '<') {
            pos = CheckClassAdapter.checkTypeParameters(signature, pos);
        }
        pos = CheckClassAdapter.checkClassTypeSignature(signature, pos);
        while (CheckClassAdapter.getChar(signature, pos) == 'L') {
            pos = CheckClassAdapter.checkClassTypeSignature(signature, pos);
        }
        if (pos != signature.length()) {
            throw new IllegalArgumentException(signature + ERROR_AT + pos);
        }
    }

    public static void checkMethodSignature(String signature) {
        int pos = 0;
        if (CheckClassAdapter.getChar(signature, 0) == '<') {
            pos = CheckClassAdapter.checkTypeParameters(signature, pos);
        }
        pos = CheckClassAdapter.checkChar('(', signature, pos);
        while ("ZCBSIFJDL[T".indexOf(CheckClassAdapter.getChar(signature, pos)) != -1) {
            pos = CheckClassAdapter.checkJavaTypeSignature(signature, pos);
        }
        pos = CheckClassAdapter.getChar(signature, pos = CheckClassAdapter.checkChar(')', signature, pos)) == 'V' ? ++pos : CheckClassAdapter.checkJavaTypeSignature(signature, pos);
        while (CheckClassAdapter.getChar(signature, pos) == '^') {
            if (CheckClassAdapter.getChar(signature, ++pos) == 'L') {
                pos = CheckClassAdapter.checkClassTypeSignature(signature, pos);
                continue;
            }
            pos = CheckClassAdapter.checkTypeVariableSignature(signature, pos);
        }
        if (pos != signature.length()) {
            throw new IllegalArgumentException(signature + ERROR_AT + pos);
        }
    }

    public static void checkFieldSignature(String signature) {
        int pos = CheckClassAdapter.checkReferenceTypeSignature(signature, 0);
        if (pos != signature.length()) {
            throw new IllegalArgumentException(signature + ERROR_AT + pos);
        }
    }

    private static int checkTypeParameters(String signature, int startPos) {
        int pos = startPos;
        pos = CheckClassAdapter.checkChar('<', signature, pos);
        pos = CheckClassAdapter.checkTypeParameter(signature, pos);
        while (CheckClassAdapter.getChar(signature, pos) != '>') {
            pos = CheckClassAdapter.checkTypeParameter(signature, pos);
        }
        return pos + 1;
    }

    private static int checkTypeParameter(String signature, int startPos) {
        int pos = startPos;
        pos = CheckClassAdapter.checkSignatureIdentifier(signature, pos);
        if ("L[T".indexOf(CheckClassAdapter.getChar(signature, pos = CheckClassAdapter.checkChar(':', signature, pos))) != -1) {
            pos = CheckClassAdapter.checkReferenceTypeSignature(signature, pos);
        }
        while (CheckClassAdapter.getChar(signature, pos) == ':') {
            pos = CheckClassAdapter.checkReferenceTypeSignature(signature, pos + 1);
        }
        return pos;
    }

    private static int checkReferenceTypeSignature(String signature, int pos) {
        switch (CheckClassAdapter.getChar(signature, pos)) {
            case 'L': {
                return CheckClassAdapter.checkClassTypeSignature(signature, pos);
            }
            case '[': {
                return CheckClassAdapter.checkJavaTypeSignature(signature, pos + 1);
            }
        }
        return CheckClassAdapter.checkTypeVariableSignature(signature, pos);
    }

    private static int checkClassTypeSignature(String signature, int startPos) {
        int pos = startPos;
        pos = CheckClassAdapter.checkChar('L', signature, pos);
        pos = CheckClassAdapter.checkSignatureIdentifier(signature, pos);
        while (CheckClassAdapter.getChar(signature, pos) == '/') {
            pos = CheckClassAdapter.checkSignatureIdentifier(signature, pos + 1);
        }
        if (CheckClassAdapter.getChar(signature, pos) == '<') {
            pos = CheckClassAdapter.checkTypeArguments(signature, pos);
        }
        while (CheckClassAdapter.getChar(signature, pos) == '.') {
            if (CheckClassAdapter.getChar(signature, pos = CheckClassAdapter.checkSignatureIdentifier(signature, pos + 1)) != '<') continue;
            pos = CheckClassAdapter.checkTypeArguments(signature, pos);
        }
        return CheckClassAdapter.checkChar(';', signature, pos);
    }

    private static int checkTypeArguments(String signature, int startPos) {
        int pos = startPos;
        pos = CheckClassAdapter.checkChar('<', signature, pos);
        pos = CheckClassAdapter.checkTypeArgument(signature, pos);
        while (CheckClassAdapter.getChar(signature, pos) != '>') {
            pos = CheckClassAdapter.checkTypeArgument(signature, pos);
        }
        return pos + 1;
    }

    private static int checkTypeArgument(String signature, int startPos) {
        int pos = startPos;
        char c2 = CheckClassAdapter.getChar(signature, pos);
        if (c2 == '*') {
            return pos + 1;
        }
        if (c2 == '+' || c2 == '-') {
            ++pos;
        }
        return CheckClassAdapter.checkReferenceTypeSignature(signature, pos);
    }

    private static int checkTypeVariableSignature(String signature, int startPos) {
        int pos = startPos;
        pos = CheckClassAdapter.checkChar('T', signature, pos);
        pos = CheckClassAdapter.checkSignatureIdentifier(signature, pos);
        return CheckClassAdapter.checkChar(';', signature, pos);
    }

    private static int checkJavaTypeSignature(String signature, int startPos) {
        int pos = startPos;
        switch (CheckClassAdapter.getChar(signature, pos)) {
            case 'B': 
            case 'C': 
            case 'D': 
            case 'F': 
            case 'I': 
            case 'J': 
            case 'S': 
            case 'Z': {
                return pos + 1;
            }
        }
        return CheckClassAdapter.checkReferenceTypeSignature(signature, pos);
    }

    private static int checkSignatureIdentifier(String signature, int startPos) {
        int pos = startPos;
        while (pos < signature.length() && ".;[/<>:".indexOf(signature.codePointAt(pos)) == -1) {
            pos = signature.offsetByCodePoints(pos, 1);
        }
        if (pos == startPos) {
            throw new IllegalArgumentException(signature + ": identifier expected at index " + startPos);
        }
        return pos;
    }

    private static int checkChar(char c2, String signature, int pos) {
        if (CheckClassAdapter.getChar(signature, pos) == c2) {
            return pos + 1;
        }
        throw new IllegalArgumentException(signature + ": '" + c2 + "' expected at index " + pos);
    }

    private static char getChar(String string, int pos) {
        return pos < string.length() ? string.charAt(pos) : (char)'\u0000';
    }

    static void checkTypeRef(int typeRef) {
        int mask = 0;
        switch (typeRef >>> 24) {
            case 0: 
            case 1: 
            case 22: {
                mask = -65536;
                break;
            }
            case 19: 
            case 20: 
            case 21: 
            case 64: 
            case 65: 
            case 67: 
            case 68: 
            case 69: 
            case 70: {
                mask = -16777216;
                break;
            }
            case 16: 
            case 17: 
            case 18: 
            case 23: 
            case 66: {
                mask = -256;
                break;
            }
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: {
                mask = -16776961;
                break;
            }
        }
        if (mask == 0 || (typeRef & ~mask) != 0) {
            throw new IllegalArgumentException("Invalid type reference 0x" + Integer.toHexString(typeRef));
        }
    }

    private static String packageName(String name) {
        int index = name.lastIndexOf(47);
        if (index == -1) {
            return "";
        }
        return name.substring(0, index);
    }

    public static void main(String[] args) throws IOException {
        CheckClassAdapter.main(args, new PrintWriter(System.err, true));
    }

    static void main(String[] args, PrintWriter logger) throws IOException {
        ClassReader classReader;
        block6: {
            block5: {
                if (args.length != 1) {
                    logger.println(USAGE);
                    return;
                }
                if (!args[0].endsWith(".class")) break block5;
                FileInputStream inputStream = new FileInputStream(args[0]);
                try {
                    classReader = new ClassReader(inputStream);
                }
                catch (Throwable throwable) {
                    try {
                        ((InputStream)inputStream).close();
                    }
                    catch (Throwable throwable2) {
                    }
                    throw throwable;
                }
                ((InputStream)inputStream).close();
                break block6;
            }
            classReader = new ClassReader(args[0]);
        }
        CheckClassAdapter.verify(classReader, false, logger);
    }

    public static void verify(ClassReader classReader, boolean printResults, PrintWriter printWriter) {
        CheckClassAdapter.verify(classReader, null, printResults, printWriter);
    }

    public static void verify(ClassReader classReader, ClassLoader loader, boolean printResults, PrintWriter printWriter) {
        ClassNode classNode = new ClassNode();
        classReader.accept(new CheckClassAdapter(0x10A0000, classNode, false){}, 2);
        Type syperType = classNode.superName == null ? null : Type.getObjectType(classNode.superName);
        List<MethodNode> methods = classNode.methods;
        ArrayList<Type> interfaces = new ArrayList<Type>();
        for (String interfaceName : classNode.interfaces) {
            interfaces.add(Type.getObjectType(interfaceName));
        }
        for (MethodNode method : methods) {
            SimpleVerifier verifier = new SimpleVerifier(Type.getObjectType(classNode.name), syperType, interfaces, (classNode.access & 0x200) != 0);
            Analyzer<BasicValue> analyzer = new Analyzer<BasicValue>(verifier);
            if (loader != null) {
                verifier.setClassLoader(loader);
            }
            try {
                analyzer.analyze(classNode.name, method);
            }
            catch (AnalyzerException e2) {
                e2.printStackTrace(printWriter);
            }
            if (!printResults) continue;
            CheckClassAdapter.printAnalyzerResult(method, analyzer, printWriter);
        }
        printWriter.flush();
    }

    static void printAnalyzerResult(MethodNode method, Analyzer<BasicValue> analyzer, PrintWriter printWriter) {
        Textifier textifier = new Textifier();
        TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(textifier);
        printWriter.println(method.name + method.desc);
        for (int i2 = 0; i2 < method.instructions.size(); ++i2) {
            method.instructions.get(i2).accept(traceMethodVisitor);
            StringBuilder stringBuilder = new StringBuilder();
            Frame<BasicValue> frame = analyzer.getFrames()[i2];
            if (frame == null) {
                stringBuilder.append('?');
            } else {
                int j2;
                for (j2 = 0; j2 < frame.getLocals(); ++j2) {
                    stringBuilder.append(CheckClassAdapter.getUnqualifiedName(frame.getLocal(j2).toString())).append(' ');
                }
                stringBuilder.append(" : ");
                for (j2 = 0; j2 < frame.getStackSize(); ++j2) {
                    stringBuilder.append(CheckClassAdapter.getUnqualifiedName(frame.getStack(j2).toString())).append(' ');
                }
            }
            while (stringBuilder.length() < method.maxStack + method.maxLocals + 1) {
                stringBuilder.append(' ');
            }
            printWriter.print(Integer.toString(i2 + 100000).substring(1));
            printWriter.print(" " + stringBuilder + " : " + textifier.text.get(textifier.text.size() - 1));
        }
        for (TryCatchBlockNode tryCatchBlock : method.tryCatchBlocks) {
            tryCatchBlock.accept(traceMethodVisitor);
            printWriter.print(" " + textifier.text.get(textifier.text.size() - 1));
        }
        printWriter.println();
    }

    private static String getUnqualifiedName(String name) {
        int lastBracketIndex;
        int lastSlashIndex = name.lastIndexOf(47);
        if (lastSlashIndex == -1) {
            return name;
        }
        int endIndex = name.length();
        if (name.charAt(endIndex - 1) == ';') {
            --endIndex;
        }
        if ((lastBracketIndex = name.lastIndexOf(91)) == -1) {
            return name.substring(lastSlashIndex + 1, endIndex);
        }
        return name.substring(0, lastBracketIndex + 1) + name.substring(lastSlashIndex + 1, endIndex);
    }
}

