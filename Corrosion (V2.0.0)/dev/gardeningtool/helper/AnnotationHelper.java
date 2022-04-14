/*
 * Decompiled with CFR 0.152.
 */
package dev.gardeningtool.helper;

import dev.gardeningtool.helper.processor.impl.ClassProcessor;
import dev.gardeningtool.helper.processor.impl.MethodProcessor;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class AnnotationHelper {
    public static void main(String[] args) throws Exception {
        Path path = Paths.get("input.jar", new String[0]);
        if (!path.toFile().exists()) {
            System.out.println("Unable to find jar file input.jar. Exiting...");
            System.exit(-1);
        }
        ArrayList<String> lines = new ArrayList<String>();
        ClassProcessor classProcessor = new ClassProcessor();
        MethodProcessor methodProcessor = new MethodProcessor();
        ZipFile zipFile = new ZipFile(path.toFile());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            InputStream inputStream = zipFile.getInputStream(entry);
            if (!name.endsWith(".class")) continue;
            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(inputStream);
            reader.accept(node, 0);
            if (classProcessor.process(node)) continue;
            lines.add(node.name);
            node.methods.stream().filter(methodNode -> !methodProcessor.process((MethodNode)methodNode)).forEach(methodNode -> {
                if (lines.contains(node.name)) {
                    return;
                }
                lines.add(String.format("%s#%s%s", node.name, methodNode.name, methodNode.signature));
            });
        }
        byte[] output = String.join((CharSequence)"\n", lines).getBytes();
        Files.write(Paths.get("config.txt", new String[0]), output, new OpenOption[0]);
    }
}

