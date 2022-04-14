/*
 * Decompiled with CFR 0.152.
 */
package com.jcraft.jorbis;

import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.VorbisFile;

class ChainingExample {
    ChainingExample() {
    }

    public static void main(String[] arg2) {
        VorbisFile ov2 = null;
        try {
            ov2 = arg2.length > 0 ? new VorbisFile(arg2[0]) : new VorbisFile(System.in, null, -1);
        }
        catch (Exception e2) {
            System.err.println(e2);
            return;
        }
        if (ov2.seekable()) {
            System.out.println("Input bitstream contained " + ov2.streams() + " logical bitstream section(s).");
            System.out.println("Total bitstream playing time: " + ov2.time_total(-1) + " seconds\n");
        } else {
            System.out.println("Standard input was not seekable.");
            System.out.println("First logical bitstream information:\n");
        }
        for (int i2 = 0; i2 < ov2.streams(); ++i2) {
            Info vi2 = ov2.getInfo(i2);
            System.out.println("\tlogical bitstream section " + (i2 + 1) + " information:");
            System.out.println("\t\t" + vi2.rate + "Hz " + vi2.channels + " channels bitrate " + ov2.bitrate(i2) / 1000 + "kbps serial number=" + ov2.serialnumber(i2));
            System.out.print("\t\tcompressed length: " + ov2.raw_total(i2) + " bytes ");
            System.out.println(" play time: " + ov2.time_total(i2) + "s");
            Comment vc2 = ov2.getComment(i2);
            System.out.println(vc2);
        }
    }
}

