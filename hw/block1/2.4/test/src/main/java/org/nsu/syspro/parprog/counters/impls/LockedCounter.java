package org.nsu.syspro.parprog.counters.impls;

import java.util.concurrent.locks.ReentrantLock;

public class LockedCounter implements Counter {

    private long data;
    ReentrantLock l;

    public LockedCounter(boolean fair) {
        this.data = 0;
        ReentrantLock l = new ReentrantLock(fair);
    }

    @Override
    public void increment() {
       l.lock();
       try {
           data++;
       } finally {
           l.unlock();
       }

    }

    @Override
    public long get() {
        l.lock();
        try {
            return data;
        } finally {
            l.unlock();
        }
    }
}
