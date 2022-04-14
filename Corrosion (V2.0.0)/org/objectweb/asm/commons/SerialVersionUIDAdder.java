/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SerialVersionUIDAdder
extends ClassVisitor {
    private static final String CLINIT = "<clinit>";
    private boolean computeSvuid;
    private boolean hasSvuid;
    private int access;
    private String name;
    private String[] interfaces;
    private Collection<Item> svuidFields;
    private boolean hasStaticInitializer;
    private Collection<Item> svuidConstructors;
    private Collection<Item> svuidMethods;

    public SerialVersionUIDAdder(ClassVisitor classVisitor) {
        this(589824, classVisitor);
        if (this.getClass() != SerialVersionUIDAdder.class) {
            throw new IllegalStateException();
        }
    }

    protected SerialVersionUIDAdder(int api2, ClassVisitor classVisitor) {
        super(api2, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        boolean bl2 = this.computeSvuid = (access & 0x4000) == 0;
        if (this.computeSvuid) {
            this.name = name;
            this.access = access;
            this.interfaces = (String[])interfaces.clone();
            this.svuidFields = new ArrayList<Item>();
            this.svuidConstructors = new ArrayList<Item>();
            this.svuidMethods = new ArrayList<Item>();
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (this.computeSvuid) {
            if (CLINIT.equals(name)) {
                this.hasStaticInitializer = true;
            }
            int mods = access & 0xD3F;
            if ((access & 2) == 0) {
                if ("<init>".equals(name)) {
                    this.svuidConstructors.add(new Item(name, mods, descriptor));
                } else if (!CLINIT.equals(name)) {
                    this.svuidMethods.add(new Item(name, mods, descriptor));
                }
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (this.computeSvuid) {
            if ("serialVersionUID".equals(name)) {
                this.computeSvuid = false;
                this.hasSvuid = true;
            }
            if ((access & 2) == 0 || (access & 0x88) == 0) {
                int mods = access & 0xDF;
                this.svuidFields.add(new Item(name, mods, desc));
            }
        }
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitInnerClass(String innerClassName, String outerName, String innerName, int innerClassAccess) {
        if (this.name != null && this.name.equals(innerClassName)) {
            this.access = innerClassAccess;
        }
        super.visitInnerClass(innerClassName, outerName, innerName, innerClassAccess);
    }

    @Override
    public void visitEnd() {
        if (this.computeSvuid && !this.hasSvuid) {
            try {
                this.addSVUID(this.computeSVUID());
            }
            catch (IOException e2) {
                throw new IllegalStateException("Error while computing SVUID for " + this.name, e2);
            }
        }
        super.visitEnd();
    }

    public boolean hasSVUID() {
        return this.hasSvuid;
    }

    protected void addSVUID(long svuid) {
        FieldVisitor fieldVisitor = super.visitField(24, "serialVersionUID", "J", null, svuid);
        if (fieldVisitor != null) {
            fieldVisitor.visitEnd();
        }
    }

    protected long computeSVUID() throws IOException {
        long svuid = 0L;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeUTF(this.name.replace('/', '.'));
                int mods = this.access;
                if ((mods & 0x200) != 0) {
                    mods = this.svuidMethods.isEmpty() ? mods & 0xFFFFFBFF : mods | 0x400;
                }
                dataOutputStream.writeInt(mods & 0x611);
                Arrays.sort(this.interfaces);
                for (String interfaceName : this.interfaces) {
                    dataOutputStream.writeUTF(interfaceName.replace('/', '.'));
                }
                SerialVersionUIDAdder.writeItems(this.svuidFields, dataOutputStream, false);
                if (this.hasStaticInitializer) {
                    dataOutputStream.writeUTF(CLINIT);
                    dataOutputStream.writeInt(8);
                    dataOutputStream.writeUTF("()V");
                }
                SerialVersionUIDAdder.writeItems(this.svuidConstructors, dataOutputStream, true);
                SerialVersionUIDAdder.writeItems(this.svuidMethods, dataOutputStream, true);
                dataOutputStream.flush();
                byte[] hashBytes = this.computeSHAdigest(byteArrayOutputStream.toByteArray());
                for (int i2 = Math.min(hashBytes.length, 8) - 1; i2 >= 0; --i2) {
                    svuid = svuid << 8 | (long)(hashBytes[i2] & 0xFF);
                }
            }
            catch (Throwable throwable) {
                try {
                    dataOutputStream.close();
                }
                catch (Throwable throwable2) {
                }
                throw throwable;
            }
            dataOutputStream.close();
        }
        catch (Throwable throwable) {
            try {
                byteArrayOutputStream.close();
            }
            catch (Throwable throwable3) {
            }
            throw throwable;
        }
        byteArrayOutputStream.close();
        return svuid;
    }

    protected byte[] computeSHAdigest(byte[] value) {
        try {
            return MessageDigest.getInstance("SHA").digest(value);
        }
        catch (NoSuchAlgorithmException e2) {
            throw new UnsupportedOperationException(e2);
        }
    }

    private static void writeItems(Collection<Item> itemCollection, DataOutput dataOutputStream, boolean dotted) throws IOException {
        Object[] items = itemCollection.toArray(new Item[0]);
        Arrays.sort(items);
        for (Object item : items) {
            dataOutputStream.writeUTF(((Item)item).name);
            dataOutputStream.writeInt(((Item)item).access);
            dataOutputStream.writeUTF(dotted ? ((Item)item).descriptor.replace('/', '.') : ((Item)item).descriptor);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class Item
    implements Comparable<Item> {
        final String name;
        final int access;
        final String descriptor;

        Item(String name, int access, String descriptor) {
            this.name = name;
            this.access = access;
            this.descriptor = descriptor;
        }

        @Override
        public int compareTo(Item item) {
            int result = this.name.compareTo(item.name);
            if (result == 0) {
                result = this.descriptor.compareTo(item.descriptor);
            }
            return result;
        }

        public boolean equals(Object other) {
            if (other instanceof Item) {
                return this.compareTo((Item)other) == 0;
            }
            return false;
        }

        public int hashCode() {
            return this.name.hashCode() ^ this.descriptor.hashCode();
        }
    }
}

