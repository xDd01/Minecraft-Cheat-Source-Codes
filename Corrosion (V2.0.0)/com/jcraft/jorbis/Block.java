/*
 * Decompiled with CFR 0.152.
 */
package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jogg.Packet;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.FuncMapping;
import com.jcraft.jorbis.Info;

public class Block {
    float[][] pcm = new float[0][];
    Buffer opb = new Buffer();
    int lW;
    int W;
    int nW;
    int pcmend;
    int mode;
    int eofflag;
    long granulepos;
    long sequence;
    DspState vd;
    int glue_bits;
    int time_bits;
    int floor_bits;
    int res_bits;

    public Block(DspState vd2) {
        this.vd = vd2;
        if (vd2.analysisp != 0) {
            this.opb.writeinit();
        }
    }

    public void init(DspState vd2) {
        this.vd = vd2;
    }

    public int clear() {
        if (this.vd != null && this.vd.analysisp != 0) {
            this.opb.writeclear();
        }
        return 0;
    }

    public int synthesis(Packet op2) {
        Info vi2 = this.vd.vi;
        this.opb.readinit(op2.packet_base, op2.packet, op2.bytes);
        if (this.opb.read(1) != 0) {
            return -1;
        }
        int _mode = this.opb.read(this.vd.modebits);
        if (_mode == -1) {
            return -1;
        }
        this.mode = _mode;
        this.W = vi2.mode_param[this.mode].blockflag;
        if (this.W != 0) {
            this.lW = this.opb.read(1);
            this.nW = this.opb.read(1);
            if (this.nW == -1) {
                return -1;
            }
        } else {
            this.lW = 0;
            this.nW = 0;
        }
        this.granulepos = op2.granulepos;
        this.sequence = op2.packetno - 3L;
        this.eofflag = op2.e_o_s;
        this.pcmend = vi2.blocksizes[this.W];
        if (this.pcm.length < vi2.channels) {
            this.pcm = new float[vi2.channels][];
        }
        for (int i2 = 0; i2 < vi2.channels; ++i2) {
            if (this.pcm[i2] == null || this.pcm[i2].length < this.pcmend) {
                this.pcm[i2] = new float[this.pcmend];
                continue;
            }
            for (int j2 = 0; j2 < this.pcmend; ++j2) {
                this.pcm[i2][j2] = 0.0f;
            }
        }
        int type = vi2.map_type[vi2.mode_param[this.mode].mapping];
        return FuncMapping.mapping_P[type].inverse(this, this.vd.mode[this.mode]);
    }
}

