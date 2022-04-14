/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.RBBINode;
import com.ibm.icu.text.RBBIRuleBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class RBBITableBuilder {
    private RBBIRuleBuilder fRB;
    private int fRootIx;
    private List<RBBIStateDescriptor> fDStates;

    RBBITableBuilder(RBBIRuleBuilder rb2, int rootNodeIx) {
        this.fRootIx = rootNodeIx;
        this.fRB = rb2;
        this.fDStates = new ArrayList<RBBIStateDescriptor>();
    }

    void build() {
        if (this.fRB.fTreeRoots[this.fRootIx] == null) {
            return;
        }
        this.fRB.fTreeRoots[this.fRootIx] = this.fRB.fTreeRoots[this.fRootIx].flattenVariables();
        if (this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("ftree") >= 0) {
            System.out.println("Parse tree after flattening variable references.");
            this.fRB.fTreeRoots[this.fRootIx].printTree(true);
        }
        if (this.fRB.fSetBuilder.sawBOF()) {
            RBBINode bofLeaf;
            RBBINode bofTop = new RBBINode(8);
            bofTop.fLeftChild = bofLeaf = new RBBINode(3);
            bofTop.fRightChild = this.fRB.fTreeRoots[this.fRootIx];
            bofLeaf.fParent = bofTop;
            bofLeaf.fVal = 2;
            this.fRB.fTreeRoots[this.fRootIx] = bofTop;
        }
        RBBINode cn2 = new RBBINode(8);
        cn2.fLeftChild = this.fRB.fTreeRoots[this.fRootIx];
        this.fRB.fTreeRoots[this.fRootIx].fParent = cn2;
        cn2.fRightChild = new RBBINode(6);
        cn2.fRightChild.fParent = cn2;
        this.fRB.fTreeRoots[this.fRootIx] = cn2;
        this.fRB.fTreeRoots[this.fRootIx].flattenSets();
        if (this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("stree") >= 0) {
            System.out.println("Parse tree after flattening Unicode Set references.");
            this.fRB.fTreeRoots[this.fRootIx].printTree(true);
        }
        this.calcNullable(this.fRB.fTreeRoots[this.fRootIx]);
        this.calcFirstPos(this.fRB.fTreeRoots[this.fRootIx]);
        this.calcLastPos(this.fRB.fTreeRoots[this.fRootIx]);
        this.calcFollowPos(this.fRB.fTreeRoots[this.fRootIx]);
        if (this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("pos") >= 0) {
            System.out.print("\n");
            this.printPosSets(this.fRB.fTreeRoots[this.fRootIx]);
        }
        if (this.fRB.fChainRules) {
            this.calcChainedFollowPos(this.fRB.fTreeRoots[this.fRootIx]);
        }
        if (this.fRB.fSetBuilder.sawBOF()) {
            this.bofFixup();
        }
        this.buildStateTable();
        this.flagAcceptingStates();
        this.flagLookAheadStates();
        this.flagTaggedStates();
        this.mergeRuleStatusVals();
        if (this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("states") >= 0) {
            this.printStates();
        }
    }

    void calcNullable(RBBINode n2) {
        if (n2 == null) {
            return;
        }
        if (n2.fType == 0 || n2.fType == 6) {
            n2.fNullable = false;
            return;
        }
        if (n2.fType == 4 || n2.fType == 5) {
            n2.fNullable = true;
            return;
        }
        this.calcNullable(n2.fLeftChild);
        this.calcNullable(n2.fRightChild);
        n2.fNullable = n2.fType == 9 ? n2.fLeftChild.fNullable || n2.fRightChild.fNullable : (n2.fType == 8 ? n2.fLeftChild.fNullable && n2.fRightChild.fNullable : n2.fType == 10 || n2.fType == 12);
    }

    void calcFirstPos(RBBINode n2) {
        if (n2 == null) {
            return;
        }
        if (n2.fType == 3 || n2.fType == 6 || n2.fType == 4 || n2.fType == 5) {
            n2.fFirstPosSet.add(n2);
            return;
        }
        this.calcFirstPos(n2.fLeftChild);
        this.calcFirstPos(n2.fRightChild);
        if (n2.fType == 9) {
            n2.fFirstPosSet.addAll(n2.fLeftChild.fFirstPosSet);
            n2.fFirstPosSet.addAll(n2.fRightChild.fFirstPosSet);
        } else if (n2.fType == 8) {
            n2.fFirstPosSet.addAll(n2.fLeftChild.fFirstPosSet);
            if (n2.fLeftChild.fNullable) {
                n2.fFirstPosSet.addAll(n2.fRightChild.fFirstPosSet);
            }
        } else if (n2.fType == 10 || n2.fType == 12 || n2.fType == 11) {
            n2.fFirstPosSet.addAll(n2.fLeftChild.fFirstPosSet);
        }
    }

    void calcLastPos(RBBINode n2) {
        if (n2 == null) {
            return;
        }
        if (n2.fType == 3 || n2.fType == 6 || n2.fType == 4 || n2.fType == 5) {
            n2.fLastPosSet.add(n2);
            return;
        }
        this.calcLastPos(n2.fLeftChild);
        this.calcLastPos(n2.fRightChild);
        if (n2.fType == 9) {
            n2.fLastPosSet.addAll(n2.fLeftChild.fLastPosSet);
            n2.fLastPosSet.addAll(n2.fRightChild.fLastPosSet);
        } else if (n2.fType == 8) {
            n2.fLastPosSet.addAll(n2.fRightChild.fLastPosSet);
            if (n2.fRightChild.fNullable) {
                n2.fLastPosSet.addAll(n2.fLeftChild.fLastPosSet);
            }
        } else if (n2.fType == 10 || n2.fType == 12 || n2.fType == 11) {
            n2.fLastPosSet.addAll(n2.fLeftChild.fLastPosSet);
        }
    }

    void calcFollowPos(RBBINode n2) {
        if (n2 == null || n2.fType == 3 || n2.fType == 6) {
            return;
        }
        this.calcFollowPos(n2.fLeftChild);
        this.calcFollowPos(n2.fRightChild);
        if (n2.fType == 8) {
            for (RBBINode i2 : n2.fLeftChild.fLastPosSet) {
                i2.fFollowPos.addAll(n2.fRightChild.fFirstPosSet);
            }
        }
        if (n2.fType == 10 || n2.fType == 11) {
            for (RBBINode i2 : n2.fLastPosSet) {
                i2.fFollowPos.addAll(n2.fFirstPosSet);
            }
        }
    }

    void calcChainedFollowPos(RBBINode tree) {
        ArrayList<RBBINode> endMarkerNodes = new ArrayList<RBBINode>();
        ArrayList<RBBINode> leafNodes = new ArrayList<RBBINode>();
        tree.findNodes(endMarkerNodes, 6);
        tree.findNodes(leafNodes, 3);
        RBBINode userRuleRoot = tree;
        if (this.fRB.fSetBuilder.sawBOF()) {
            userRuleRoot = tree.fLeftChild.fRightChild;
        }
        Assert.assrt(userRuleRoot != null);
        Set<RBBINode> matchStartNodes = userRuleRoot.fFirstPosSet;
        for (RBBINode tNode : leafNodes) {
            int cLBProp;
            int c2;
            RBBINode endNode = null;
            for (RBBINode endMarkerNode : endMarkerNodes) {
                if (!tNode.fFollowPos.contains(endMarkerNode)) continue;
                endNode = tNode;
                break;
            }
            if (endNode == null || this.fRB.fLBCMNoChain && (c2 = this.fRB.fSetBuilder.getFirstChar(endNode.fVal)) != -1 && (cLBProp = UCharacter.getIntPropertyValue(c2, 4104)) == 9) continue;
            for (RBBINode startNode : matchStartNodes) {
                if (startNode.fType != 3 || endNode.fVal != startNode.fVal) continue;
                endNode.fFollowPos.addAll(startNode.fFollowPos);
            }
        }
    }

    void bofFixup() {
        RBBINode bofNode = this.fRB.fTreeRoots[this.fRootIx].fLeftChild.fLeftChild;
        Assert.assrt(bofNode.fType == 3);
        Assert.assrt(bofNode.fVal == 2);
        Set<RBBINode> matchStartNodes = this.fRB.fTreeRoots[this.fRootIx].fLeftChild.fRightChild.fFirstPosSet;
        for (RBBINode startNode : matchStartNodes) {
            if (startNode.fType != 3 || startNode.fVal != bofNode.fVal) continue;
            bofNode.fFollowPos.addAll(startNode.fFollowPos);
        }
    }

    void buildStateTable() {
        int lastInputSymbol = this.fRB.fSetBuilder.getNumCharCategories() - 1;
        RBBIStateDescriptor failState = new RBBIStateDescriptor(lastInputSymbol);
        this.fDStates.add(failState);
        RBBIStateDescriptor initialState = new RBBIStateDescriptor(lastInputSymbol);
        initialState.fPositions.addAll(this.fRB.fTreeRoots[this.fRootIx].fFirstPosSet);
        this.fDStates.add(initialState);
        block0: while (true) {
            RBBIStateDescriptor T = null;
            for (int tx2 = 1; tx2 < this.fDStates.size(); ++tx2) {
                RBBIStateDescriptor temp = this.fDStates.get(tx2);
                if (temp.fMarked) continue;
                T = temp;
                break;
            }
            if (T == null) break;
            T.fMarked = true;
            int a2 = 1;
            while (true) {
                if (a2 > lastInputSymbol) continue block0;
                Set<RBBINode> U = null;
                for (RBBINode p2 : T.fPositions) {
                    if (p2.fType != 3 || p2.fVal != a2) continue;
                    if (U == null) {
                        U = new HashSet<RBBINode>();
                    }
                    U.addAll(p2.fFollowPos);
                }
                int ux2 = 0;
                boolean UinDstates = false;
                if (U != null) {
                    Assert.assrt(U.size() > 0);
                    for (int ix2 = 0; ix2 < this.fDStates.size(); ++ix2) {
                        RBBIStateDescriptor temp2 = this.fDStates.get(ix2);
                        if (!U.equals(temp2.fPositions)) continue;
                        U = temp2.fPositions;
                        ux2 = ix2;
                        UinDstates = true;
                        break;
                    }
                    if (!UinDstates) {
                        RBBIStateDescriptor newState = new RBBIStateDescriptor(lastInputSymbol);
                        newState.fPositions = U;
                        this.fDStates.add(newState);
                        ux2 = this.fDStates.size() - 1;
                    }
                    T.fDtran[a2] = ux2;
                }
                ++a2;
            }
            break;
        }
    }

    void flagAcceptingStates() {
        ArrayList<RBBINode> endMarkerNodes = new ArrayList<RBBINode>();
        this.fRB.fTreeRoots[this.fRootIx].findNodes(endMarkerNodes, 6);
        for (int i2 = 0; i2 < endMarkerNodes.size(); ++i2) {
            RBBINode endMarker = (RBBINode)endMarkerNodes.get(i2);
            for (int n2 = 0; n2 < this.fDStates.size(); ++n2) {
                RBBIStateDescriptor sd2 = this.fDStates.get(n2);
                if (!sd2.fPositions.contains(endMarker)) continue;
                if (sd2.fAccepting == 0) {
                    sd2.fAccepting = endMarker.fVal;
                    if (sd2.fAccepting == 0) {
                        sd2.fAccepting = -1;
                    }
                }
                if (sd2.fAccepting == -1 && endMarker.fVal != 0) {
                    sd2.fAccepting = endMarker.fVal;
                }
                if (!endMarker.fLookAheadEnd) continue;
                sd2.fLookAhead = sd2.fAccepting;
            }
        }
    }

    void flagLookAheadStates() {
        ArrayList<RBBINode> lookAheadNodes = new ArrayList<RBBINode>();
        this.fRB.fTreeRoots[this.fRootIx].findNodes(lookAheadNodes, 4);
        for (int i2 = 0; i2 < lookAheadNodes.size(); ++i2) {
            RBBINode lookAheadNode = (RBBINode)lookAheadNodes.get(i2);
            for (int n2 = 0; n2 < this.fDStates.size(); ++n2) {
                RBBIStateDescriptor sd2 = this.fDStates.get(n2);
                if (!sd2.fPositions.contains(lookAheadNode)) continue;
                sd2.fLookAhead = lookAheadNode.fVal;
            }
        }
    }

    void flagTaggedStates() {
        ArrayList<RBBINode> tagNodes = new ArrayList<RBBINode>();
        this.fRB.fTreeRoots[this.fRootIx].findNodes(tagNodes, 5);
        for (int i2 = 0; i2 < tagNodes.size(); ++i2) {
            RBBINode tagNode = (RBBINode)tagNodes.get(i2);
            for (int n2 = 0; n2 < this.fDStates.size(); ++n2) {
                RBBIStateDescriptor sd2 = this.fDStates.get(n2);
                if (!sd2.fPositions.contains(tagNode)) continue;
                sd2.fTagVals.add(tagNode.fVal);
            }
        }
    }

    void mergeRuleStatusVals() {
        if (this.fRB.fRuleStatusVals.size() == 0) {
            this.fRB.fRuleStatusVals.add(1);
            this.fRB.fRuleStatusVals.add(0);
            TreeSet s0 = new TreeSet();
            Integer izero = 0;
            this.fRB.fStatusSets.put(s0, izero);
            TreeSet<Integer> s1 = new TreeSet<Integer>();
            s1.add(izero);
            this.fRB.fStatusSets.put(s0, izero);
        }
        for (int n2 = 0; n2 < this.fDStates.size(); ++n2) {
            RBBIStateDescriptor sd2 = this.fDStates.get(n2);
            SortedSet<Integer> statusVals = sd2.fTagVals;
            Integer arrayIndexI = this.fRB.fStatusSets.get(statusVals);
            if (arrayIndexI == null) {
                arrayIndexI = this.fRB.fRuleStatusVals.size();
                this.fRB.fStatusSets.put(statusVals, arrayIndexI);
                this.fRB.fRuleStatusVals.add(statusVals.size());
                this.fRB.fRuleStatusVals.addAll(statusVals);
            }
            sd2.fTagsIdx = arrayIndexI;
        }
    }

    void printPosSets(RBBINode n2) {
        if (n2 == null) {
            return;
        }
        RBBINode.printNode(n2);
        System.out.print("         Nullable:  " + n2.fNullable);
        System.out.print("         firstpos:  ");
        this.printSet(n2.fFirstPosSet);
        System.out.print("         lastpos:   ");
        this.printSet(n2.fLastPosSet);
        System.out.print("         followpos: ");
        this.printSet(n2.fFollowPos);
        this.printPosSets(n2.fLeftChild);
        this.printPosSets(n2.fRightChild);
    }

    int getTableSize() {
        int size = 0;
        if (this.fRB.fTreeRoots[this.fRootIx] == null) {
            return 0;
        }
        size = 16;
        int numRows = this.fDStates.size();
        int numCols = this.fRB.fSetBuilder.getNumCharCategories();
        int rowSize = 8 + 2 * numCols;
        size += numRows * rowSize;
        while (size % 8 > 0) {
            ++size;
        }
        return size;
    }

    short[] exportTable() {
        if (this.fRB.fTreeRoots[this.fRootIx] == null) {
            return new short[0];
        }
        Assert.assrt(this.fRB.fSetBuilder.getNumCharCategories() < Short.MAX_VALUE && this.fDStates.size() < Short.MAX_VALUE);
        int numStates = this.fDStates.size();
        int rowLen = 4 + this.fRB.fSetBuilder.getNumCharCategories();
        int tableSize = this.getTableSize() / 2;
        short[] table = new short[tableSize];
        table[0] = (short)(numStates >>> 16);
        table[1] = (short)(numStates & 0xFFFF);
        table[2] = (short)(rowLen >>> 16);
        table[3] = (short)(rowLen & 0xFFFF);
        int flags = 0;
        if (this.fRB.fLookAheadHardBreak) {
            flags |= 1;
        }
        if (this.fRB.fSetBuilder.sawBOF()) {
            flags |= 2;
        }
        table[4] = (short)(flags >>> 16);
        table[5] = (short)(flags & 0xFFFF);
        int numCharCategories = this.fRB.fSetBuilder.getNumCharCategories();
        for (int state = 0; state < numStates; ++state) {
            RBBIStateDescriptor sd2 = this.fDStates.get(state);
            int row = 8 + state * rowLen;
            Assert.assrt(Short.MIN_VALUE < sd2.fAccepting && sd2.fAccepting <= Short.MAX_VALUE);
            Assert.assrt(Short.MIN_VALUE < sd2.fLookAhead && sd2.fLookAhead <= Short.MAX_VALUE);
            table[row + 0] = (short)sd2.fAccepting;
            table[row + 1] = (short)sd2.fLookAhead;
            table[row + 2] = (short)sd2.fTagsIdx;
            for (int col = 0; col < numCharCategories; ++col) {
                table[row + 4 + col] = (short)sd2.fDtran[col];
            }
        }
        return table;
    }

    void printSet(Collection<RBBINode> s2) {
        for (RBBINode n2 : s2) {
            RBBINode.printInt(n2.fSerialNum, 8);
        }
        System.out.println();
    }

    void printStates() {
        int c2;
        System.out.print("state |           i n p u t     s y m b o l s \n");
        System.out.print("      | Acc  LA    Tag");
        for (c2 = 0; c2 < this.fRB.fSetBuilder.getNumCharCategories(); ++c2) {
            RBBINode.printInt(c2, 3);
        }
        System.out.print("\n");
        System.out.print("      |---------------");
        for (c2 = 0; c2 < this.fRB.fSetBuilder.getNumCharCategories(); ++c2) {
            System.out.print("---");
        }
        System.out.print("\n");
        for (int n2 = 0; n2 < this.fDStates.size(); ++n2) {
            RBBIStateDescriptor sd2 = this.fDStates.get(n2);
            RBBINode.printInt(n2, 5);
            System.out.print(" | ");
            RBBINode.printInt(sd2.fAccepting, 3);
            RBBINode.printInt(sd2.fLookAhead, 4);
            RBBINode.printInt(sd2.fTagsIdx, 6);
            System.out.print(" ");
            for (c2 = 0; c2 < this.fRB.fSetBuilder.getNumCharCategories(); ++c2) {
                RBBINode.printInt(sd2.fDtran[c2], 3);
            }
            System.out.print("\n");
        }
        System.out.print("\n\n");
    }

    void printRuleStatusTable() {
        int thisRecord = 0;
        int nextRecord = 0;
        List<Integer> tbl = this.fRB.fRuleStatusVals;
        System.out.print("index |  tags \n");
        System.out.print("-------------------\n");
        while (nextRecord < tbl.size()) {
            thisRecord = nextRecord;
            nextRecord = thisRecord + tbl.get(thisRecord) + 1;
            RBBINode.printInt(thisRecord, 7);
            for (int i2 = thisRecord + 1; i2 < nextRecord; ++i2) {
                int val = tbl.get(i2);
                RBBINode.printInt(val, 7);
            }
            System.out.print("\n");
        }
        System.out.print("\n\n");
    }

    static class RBBIStateDescriptor {
        boolean fMarked;
        int fAccepting;
        int fLookAhead;
        SortedSet<Integer> fTagVals = new TreeSet<Integer>();
        int fTagsIdx;
        Set<RBBINode> fPositions = new HashSet<RBBINode>();
        int[] fDtran;

        RBBIStateDescriptor(int maxInputSymbol) {
            this.fDtran = new int[maxInputSymbol + 1];
        }
    }
}

