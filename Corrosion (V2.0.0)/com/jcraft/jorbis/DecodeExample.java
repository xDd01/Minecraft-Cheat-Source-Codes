/*
 * Decompiled with CFR 0.152.
 */
package com.jcraft.jorbis;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import java.io.FileInputStream;
import java.io.InputStream;

class DecodeExample {
    static int convsize = 8192;
    static byte[] convbuffer = new byte[convsize];

    DecodeExample() {
    }

    public static void main(String[] arg2) {
        InputStream input = System.in;
        if (arg2.length > 0) {
            try {
                input = new FileInputStream(arg2[0]);
            }
            catch (Exception e2) {
                System.err.println(e2);
            }
        }
        SyncState oy2 = new SyncState();
        StreamState os2 = new StreamState();
        Page og2 = new Page();
        Packet op2 = new Packet();
        Info vi2 = new Info();
        Comment vc2 = new Comment();
        DspState vd2 = new DspState();
        Block vb2 = new Block(vd2);
        int bytes = 0;
        oy2.init();
        while (true) {
            boolean eos = false;
            int index = oy2.buffer(4096);
            byte[] buffer = oy2.data;
            try {
                bytes = input.read(buffer, index, 4096);
            }
            catch (Exception e3) {
                System.err.println(e3);
                System.exit(-1);
            }
            oy2.wrote(bytes);
            if (oy2.pageout(og2) != 1) {
                if (bytes < 4096) break;
                System.err.println("Input does not appear to be an Ogg bitstream.");
                System.exit(1);
            }
            os2.init(og2.serialno());
            vi2.init();
            vc2.init();
            if (os2.pagein(og2) < 0) {
                System.err.println("Error reading first page of Ogg bitstream data.");
                System.exit(1);
            }
            if (os2.packetout(op2) != 1) {
                System.err.println("Error reading initial header packet.");
                System.exit(1);
            }
            if (vi2.synthesis_headerin(vc2, op2) < 0) {
                System.err.println("This Ogg bitstream does not contain Vorbis audio data.");
                System.exit(1);
            }
            int i2 = 0;
            while (i2 < 2) {
                int result;
                while (i2 < 2 && (result = oy2.pageout(og2)) != 0) {
                    if (result != 1) continue;
                    os2.pagein(og2);
                    while (i2 < 2 && (result = os2.packetout(op2)) != 0) {
                        if (result == -1) {
                            System.err.println("Corrupt secondary header.  Exiting.");
                            System.exit(1);
                        }
                        vi2.synthesis_headerin(vc2, op2);
                        ++i2;
                    }
                }
                index = oy2.buffer(4096);
                buffer = oy2.data;
                try {
                    bytes = input.read(buffer, index, 4096);
                }
                catch (Exception e4) {
                    System.err.println(e4);
                    System.exit(1);
                }
                if (bytes == 0 && i2 < 2) {
                    System.err.println("End of file before finding all Vorbis headers!");
                    System.exit(1);
                }
                oy2.wrote(bytes);
            }
            byte[][] ptr = vc2.user_comments;
            for (int j2 = 0; j2 < ptr.length && ptr[j2] != null; ++j2) {
                System.err.println(new String(ptr[j2], 0, ptr[j2].length - 1));
            }
            System.err.println("\nBitstream is " + vi2.channels + " channel, " + vi2.rate + "Hz");
            System.err.println("Encoded by: " + new String(vc2.vendor, 0, vc2.vendor.length - 1) + "\n");
            convsize = 4096 / vi2.channels;
            vd2.synthesis_init(vi2);
            vb2.init(vd2);
            float[][][] _pcm = new float[1][][];
            int[] _index = new int[vi2.channels];
            while (!eos) {
                int result;
                while (!eos && (result = oy2.pageout(og2)) != 0) {
                    if (result == -1) {
                        System.err.println("Corrupt or missing data in bitstream; continuing...");
                        continue;
                    }
                    os2.pagein(og2);
                    while ((result = os2.packetout(op2)) != 0) {
                        int samples;
                        if (result == -1) continue;
                        if (vb2.synthesis(op2) == 0) {
                            vd2.synthesis_blockin(vb2);
                        }
                        while ((samples = vd2.synthesis_pcmout(_pcm, _index)) > 0) {
                            float[][] pcm = _pcm[0];
                            int bout = samples < convsize ? samples : convsize;
                            for (i2 = 0; i2 < vi2.channels; ++i2) {
                                int ptr2 = i2 * 2;
                                int mono = _index[i2];
                                for (int j3 = 0; j3 < bout; ++j3) {
                                    int val = (int)((double)pcm[i2][mono + j3] * 32767.0);
                                    if (val > Short.MAX_VALUE) {
                                        val = Short.MAX_VALUE;
                                    }
                                    if (val < Short.MIN_VALUE) {
                                        val = Short.MIN_VALUE;
                                    }
                                    if (val < 0) {
                                        val |= 0x8000;
                                    }
                                    DecodeExample.convbuffer[ptr2] = (byte)val;
                                    DecodeExample.convbuffer[ptr2 + 1] = (byte)(val >>> 8);
                                    ptr2 += 2 * vi2.channels;
                                }
                            }
                            System.out.write(convbuffer, 0, 2 * vi2.channels * bout);
                            vd2.synthesis_read(bout);
                        }
                    }
                    if (og2.eos() == 0) continue;
                    eos = true;
                }
                if (eos) continue;
                index = oy2.buffer(4096);
                buffer = oy2.data;
                try {
                    bytes = input.read(buffer, index, 4096);
                }
                catch (Exception e5) {
                    System.err.println(e5);
                    System.exit(1);
                }
                oy2.wrote(bytes);
                if (bytes != 0) continue;
                eos = true;
            }
            os2.clear();
            vb2.clear();
            vd2.clear();
            vi2.clear();
        }
        oy2.clear();
        System.err.println("Done.");
    }
}

