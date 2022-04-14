/*
 * Decompiled with CFR 0.152.
 */
package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.FuncFloor;
import com.jcraft.jorbis.FuncMapping;
import com.jcraft.jorbis.FuncResidue;
import com.jcraft.jorbis.FuncTime;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.InfoMode;
import com.jcraft.jorbis.Mdct;
import com.jcraft.jorbis.PsyLook;
import com.jcraft.jorbis.Util;

class Mapping0
extends FuncMapping {
    static int seq = 0;
    float[][] pcmbundle = null;
    int[] zerobundle = null;
    int[] nonzero = null;
    Object[] floormemo = null;

    Mapping0() {
    }

    void free_info(Object imap) {
    }

    void free_look(Object imap) {
    }

    Object look(DspState vd2, InfoMode vm2, Object m2) {
        Info vi2 = vd2.vi;
        LookMapping0 look = new LookMapping0();
        InfoMapping0 info = look.map = (InfoMapping0)m2;
        look.mode = vm2;
        look.time_look = new Object[info.submaps];
        look.floor_look = new Object[info.submaps];
        look.residue_look = new Object[info.submaps];
        look.time_func = new FuncTime[info.submaps];
        look.floor_func = new FuncFloor[info.submaps];
        look.residue_func = new FuncResidue[info.submaps];
        for (int i2 = 0; i2 < info.submaps; ++i2) {
            int timenum = info.timesubmap[i2];
            int floornum = info.floorsubmap[i2];
            int resnum = info.residuesubmap[i2];
            look.time_func[i2] = FuncTime.time_P[vi2.time_type[timenum]];
            look.time_look[i2] = look.time_func[i2].look(vd2, vm2, vi2.time_param[timenum]);
            look.floor_func[i2] = FuncFloor.floor_P[vi2.floor_type[floornum]];
            look.floor_look[i2] = look.floor_func[i2].look(vd2, vm2, vi2.floor_param[floornum]);
            look.residue_func[i2] = FuncResidue.residue_P[vi2.residue_type[resnum]];
            look.residue_look[i2] = look.residue_func[i2].look(vd2, vm2, vi2.residue_param[resnum]);
        }
        if (vi2.psys == 0 || vd2.analysisp != 0) {
            // empty if block
        }
        look.ch = vi2.channels;
        return look;
    }

    void pack(Info vi2, Object imap, Buffer opb) {
        int i2;
        InfoMapping0 info = (InfoMapping0)imap;
        if (info.submaps > 1) {
            opb.write(1, 1);
            opb.write(info.submaps - 1, 4);
        } else {
            opb.write(0, 1);
        }
        if (info.coupling_steps > 0) {
            opb.write(1, 1);
            opb.write(info.coupling_steps - 1, 8);
            for (i2 = 0; i2 < info.coupling_steps; ++i2) {
                opb.write(info.coupling_mag[i2], Util.ilog2(vi2.channels));
                opb.write(info.coupling_ang[i2], Util.ilog2(vi2.channels));
            }
        } else {
            opb.write(0, 1);
        }
        opb.write(0, 2);
        if (info.submaps > 1) {
            for (i2 = 0; i2 < vi2.channels; ++i2) {
                opb.write(info.chmuxlist[i2], 4);
            }
        }
        for (i2 = 0; i2 < info.submaps; ++i2) {
            opb.write(info.timesubmap[i2], 8);
            opb.write(info.floorsubmap[i2], 8);
            opb.write(info.residuesubmap[i2], 8);
        }
    }

    Object unpack(Info vi2, Buffer opb) {
        int i2;
        InfoMapping0 info = new InfoMapping0();
        info.submaps = opb.read(1) != 0 ? opb.read(4) + 1 : 1;
        if (opb.read(1) != 0) {
            info.coupling_steps = opb.read(8) + 1;
            for (i2 = 0; i2 < info.coupling_steps; ++i2) {
                int testM = info.coupling_mag[i2] = opb.read(Util.ilog2(vi2.channels));
                int testA = info.coupling_ang[i2] = opb.read(Util.ilog2(vi2.channels));
                if (testM >= 0 && testA >= 0 && testM != testA && testM < vi2.channels && testA < vi2.channels) continue;
                info.free();
                return null;
            }
        }
        if (opb.read(2) > 0) {
            info.free();
            return null;
        }
        if (info.submaps > 1) {
            for (i2 = 0; i2 < vi2.channels; ++i2) {
                info.chmuxlist[i2] = opb.read(4);
                if (info.chmuxlist[i2] < info.submaps) continue;
                info.free();
                return null;
            }
        }
        for (i2 = 0; i2 < info.submaps; ++i2) {
            info.timesubmap[i2] = opb.read(8);
            if (info.timesubmap[i2] >= vi2.times) {
                info.free();
                return null;
            }
            info.floorsubmap[i2] = opb.read(8);
            if (info.floorsubmap[i2] >= vi2.floors) {
                info.free();
                return null;
            }
            info.residuesubmap[i2] = opb.read(8);
            if (info.residuesubmap[i2] < vi2.residues) continue;
            info.free();
            return null;
        }
        return info;
    }

    synchronized int inverse(Block vb2, Object l2) {
        int j2;
        int i2;
        DspState vd2 = vb2.vd;
        Info vi2 = vd2.vi;
        LookMapping0 look = (LookMapping0)l2;
        InfoMapping0 info = look.map;
        InfoMode mode = look.mode;
        int n2 = vb2.pcmend = vi2.blocksizes[vb2.W];
        float[] window = vd2.window[vb2.W][vb2.lW][vb2.nW][mode.windowtype];
        if (this.pcmbundle == null || this.pcmbundle.length < vi2.channels) {
            this.pcmbundle = new float[vi2.channels][];
            this.nonzero = new int[vi2.channels];
            this.zerobundle = new int[vi2.channels];
            this.floormemo = new Object[vi2.channels];
        }
        for (i2 = 0; i2 < vi2.channels; ++i2) {
            float[] pcm = vb2.pcm[i2];
            int submap = info.chmuxlist[i2];
            this.floormemo[i2] = look.floor_func[submap].inverse1(vb2, look.floor_look[submap], this.floormemo[i2]);
            this.nonzero[i2] = this.floormemo[i2] != null ? 1 : 0;
            for (j2 = 0; j2 < n2 / 2; ++j2) {
                pcm[j2] = 0.0f;
            }
        }
        for (i2 = 0; i2 < info.coupling_steps; ++i2) {
            if (this.nonzero[info.coupling_mag[i2]] == 0 && this.nonzero[info.coupling_ang[i2]] == 0) continue;
            this.nonzero[info.coupling_mag[i2]] = 1;
            this.nonzero[info.coupling_ang[i2]] = 1;
        }
        for (i2 = 0; i2 < info.submaps; ++i2) {
            int ch_in_bundle = 0;
            for (int j3 = 0; j3 < vi2.channels; ++j3) {
                if (info.chmuxlist[j3] != i2) continue;
                this.zerobundle[ch_in_bundle] = this.nonzero[j3] != 0 ? 1 : 0;
                this.pcmbundle[ch_in_bundle++] = vb2.pcm[j3];
            }
            look.residue_func[i2].inverse(vb2, look.residue_look[i2], this.pcmbundle, this.zerobundle, ch_in_bundle);
        }
        for (i2 = info.coupling_steps - 1; i2 >= 0; --i2) {
            float[] pcmM = vb2.pcm[info.coupling_mag[i2]];
            float[] pcmA = vb2.pcm[info.coupling_ang[i2]];
            for (j2 = 0; j2 < n2 / 2; ++j2) {
                float mag = pcmM[j2];
                float ang2 = pcmA[j2];
                if (mag > 0.0f) {
                    if (ang2 > 0.0f) {
                        pcmM[j2] = mag;
                        pcmA[j2] = mag - ang2;
                        continue;
                    }
                    pcmA[j2] = mag;
                    pcmM[j2] = mag + ang2;
                    continue;
                }
                if (ang2 > 0.0f) {
                    pcmM[j2] = mag;
                    pcmA[j2] = mag + ang2;
                    continue;
                }
                pcmA[j2] = mag;
                pcmM[j2] = mag - ang2;
            }
        }
        for (i2 = 0; i2 < vi2.channels; ++i2) {
            float[] pcm = vb2.pcm[i2];
            int submap = info.chmuxlist[i2];
            look.floor_func[submap].inverse2(vb2, look.floor_look[submap], this.floormemo[i2], pcm);
        }
        for (i2 = 0; i2 < vi2.channels; ++i2) {
            float[] pcm = vb2.pcm[i2];
            ((Mdct)vd2.transform[vb2.W][0]).backward(pcm, pcm);
        }
        for (i2 = 0; i2 < vi2.channels; ++i2) {
            int j4;
            float[] pcm = vb2.pcm[i2];
            if (this.nonzero[i2] != 0) {
                for (j4 = 0; j4 < n2; ++j4) {
                    int n3 = j4;
                    pcm[n3] = pcm[n3] * window[j4];
                }
                continue;
            }
            for (j4 = 0; j4 < n2; ++j4) {
                pcm[j4] = 0.0f;
            }
        }
        return 0;
    }

    class LookMapping0 {
        InfoMode mode;
        InfoMapping0 map;
        Object[] time_look;
        Object[] floor_look;
        Object[] floor_state;
        Object[] residue_look;
        PsyLook[] psy_look;
        FuncTime[] time_func;
        FuncFloor[] floor_func;
        FuncResidue[] residue_func;
        int ch;
        float[][] decay;
        int lastframe;

        LookMapping0() {
        }
    }

    class InfoMapping0 {
        int submaps;
        int[] chmuxlist = new int[256];
        int[] timesubmap = new int[16];
        int[] floorsubmap = new int[16];
        int[] residuesubmap = new int[16];
        int[] psysubmap = new int[16];
        int coupling_steps;
        int[] coupling_mag = new int[256];
        int[] coupling_ang = new int[256];

        InfoMapping0() {
        }

        void free() {
            this.chmuxlist = null;
            this.timesubmap = null;
            this.floorsubmap = null;
            this.residuesubmap = null;
            this.psysubmap = null;
            this.coupling_mag = null;
            this.coupling_ang = null;
        }
    }
}

