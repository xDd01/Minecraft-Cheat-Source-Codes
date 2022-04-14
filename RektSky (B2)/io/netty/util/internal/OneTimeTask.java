package io.netty.util.internal;

public abstract class OneTimeTask extends MpscLinkedQueueNode<Runnable> implements Runnable
{
    @Override
    public Runnable value() {
        return this;
    }
}
