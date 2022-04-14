package org.apache.commons.lang3;

import java.util.*;

public class ThreadUtils
{
    public static final AlwaysTruePredicate ALWAYS_TRUE_PREDICATE;
    
    public static Thread findThreadById(final long threadId, final ThreadGroup threadGroup) {
        Validate.isTrue(threadGroup != null, "The thread group must not be null", new Object[0]);
        final Thread thread = findThreadById(threadId);
        if (thread != null && threadGroup.equals(thread.getThreadGroup())) {
            return thread;
        }
        return null;
    }
    
    public static Thread findThreadById(final long threadId, final String threadGroupName) {
        Validate.isTrue(threadGroupName != null, "The thread group name must not be null", new Object[0]);
        final Thread thread = findThreadById(threadId);
        if (thread != null && thread.getThreadGroup() != null && thread.getThreadGroup().getName().equals(threadGroupName)) {
            return thread;
        }
        return null;
    }
    
    public static Collection<Thread> findThreadsByName(final String threadName, final ThreadGroup threadGroup) {
        return findThreads(threadGroup, false, new NamePredicate(threadName));
    }
    
    public static Collection<Thread> findThreadsByName(final String threadName, final String threadGroupName) {
        Validate.isTrue(threadName != null, "The thread name must not be null", new Object[0]);
        Validate.isTrue(threadGroupName != null, "The thread group name must not be null", new Object[0]);
        final Collection<ThreadGroup> threadGroups = findThreadGroups(new NamePredicate(threadGroupName));
        if (threadGroups.isEmpty()) {
            return (Collection<Thread>)Collections.emptyList();
        }
        final Collection<Thread> result = new ArrayList<Thread>();
        final NamePredicate threadNamePredicate = new NamePredicate(threadName);
        for (final ThreadGroup group : threadGroups) {
            result.addAll(findThreads(group, false, threadNamePredicate));
        }
        return Collections.unmodifiableCollection((Collection<? extends Thread>)result);
    }
    
    public static Collection<ThreadGroup> findThreadGroupsByName(final String threadGroupName) {
        return findThreadGroups(new NamePredicate(threadGroupName));
    }
    
    public static Collection<ThreadGroup> getAllThreadGroups() {
        return findThreadGroups(ThreadUtils.ALWAYS_TRUE_PREDICATE);
    }
    
    public static ThreadGroup getSystemThreadGroup() {
        ThreadGroup threadGroup;
        for (threadGroup = Thread.currentThread().getThreadGroup(); threadGroup.getParent() != null; threadGroup = threadGroup.getParent()) {}
        return threadGroup;
    }
    
    public static Collection<Thread> getAllThreads() {
        return findThreads(ThreadUtils.ALWAYS_TRUE_PREDICATE);
    }
    
    public static Collection<Thread> findThreadsByName(final String threadName) {
        return findThreads(new NamePredicate(threadName));
    }
    
    public static Thread findThreadById(final long threadId) {
        final Collection<Thread> result = findThreads(new ThreadIdPredicate(threadId));
        return result.isEmpty() ? null : result.iterator().next();
    }
    
    public static Collection<Thread> findThreads(final ThreadPredicate predicate) {
        return findThreads(getSystemThreadGroup(), true, predicate);
    }
    
    public static Collection<ThreadGroup> findThreadGroups(final ThreadGroupPredicate predicate) {
        return findThreadGroups(getSystemThreadGroup(), true, predicate);
    }
    
    public static Collection<Thread> findThreads(final ThreadGroup group, final boolean recurse, final ThreadPredicate predicate) {
        Validate.isTrue(group != null, "The group must not be null", new Object[0]);
        Validate.isTrue(predicate != null, "The predicate must not be null", new Object[0]);
        int count = group.activeCount();
        Thread[] threads;
        do {
            threads = new Thread[count + count / 2 + 1];
            count = group.enumerate(threads, recurse);
        } while (count >= threads.length);
        final List<Thread> result = new ArrayList<Thread>(count);
        for (int i = 0; i < count; ++i) {
            if (predicate.test(threads[i])) {
                result.add(threads[i]);
            }
        }
        return Collections.unmodifiableCollection((Collection<? extends Thread>)result);
    }
    
    public static Collection<ThreadGroup> findThreadGroups(final ThreadGroup group, final boolean recurse, final ThreadGroupPredicate predicate) {
        Validate.isTrue(group != null, "The group must not be null", new Object[0]);
        Validate.isTrue(predicate != null, "The predicate must not be null", new Object[0]);
        int count = group.activeGroupCount();
        ThreadGroup[] threadGroups;
        do {
            threadGroups = new ThreadGroup[count + count / 2 + 1];
            count = group.enumerate(threadGroups, recurse);
        } while (count >= threadGroups.length);
        final List<ThreadGroup> result = new ArrayList<ThreadGroup>(count);
        for (int i = 0; i < count; ++i) {
            if (predicate.test(threadGroups[i])) {
                result.add(threadGroups[i]);
            }
        }
        return Collections.unmodifiableCollection((Collection<? extends ThreadGroup>)result);
    }
    
    static {
        ALWAYS_TRUE_PREDICATE = new AlwaysTruePredicate();
    }
    
    private static final class AlwaysTruePredicate implements ThreadPredicate, ThreadGroupPredicate
    {
        @Override
        public boolean test(final ThreadGroup threadGroup) {
            return true;
        }
        
        @Override
        public boolean test(final Thread thread) {
            return true;
        }
    }
    
    public static class NamePredicate implements ThreadPredicate, ThreadGroupPredicate
    {
        private final String name;
        
        public NamePredicate(final String name) {
            Validate.isTrue(name != null, "The name must not be null", new Object[0]);
            this.name = name;
        }
        
        @Override
        public boolean test(final ThreadGroup threadGroup) {
            return threadGroup != null && threadGroup.getName().equals(this.name);
        }
        
        @Override
        public boolean test(final Thread thread) {
            return thread != null && thread.getName().equals(this.name);
        }
    }
    
    public static class ThreadIdPredicate implements ThreadPredicate
    {
        private final long threadId;
        
        public ThreadIdPredicate(final long threadId) {
            if (threadId <= 0L) {
                throw new IllegalArgumentException("The thread id must be greater than zero");
            }
            this.threadId = threadId;
        }
        
        @Override
        public boolean test(final Thread thread) {
            return thread != null && thread.getId() == this.threadId;
        }
    }
    
    public interface ThreadPredicate
    {
        boolean test(final Thread p0);
    }
    
    public interface ThreadGroupPredicate
    {
        boolean test(final ThreadGroup p0);
    }
}
