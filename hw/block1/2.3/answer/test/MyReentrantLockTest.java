import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class MyReentrantLockTest {

    /**
     * Mock for NonReentrantLock.
     */
    static class MockNonReentrantLock implements NonReentrantLock {
        private final ReentrantLock l = new ReentrantLock();
        private Thread owner = null;

        public void lock() {
            l.lock();
            try {
                if (owner == Thread.currentThread()) {
                    throw new IllegalMonitorStateException("Mock: Repeated lock on NonReentrantLock");
                }
                l.lock();
                owner = Thread.currentThread();
            } finally {
                l.unlock();
            }
        }

        public void unlock() {
            l.lock();
            try {
                if (owner != Thread.currentThread()) {
                    throw new IllegalMonitorStateException("Mock: Unlocking thread is not the owner");
                }
                owner = null;
                l.unlock();
            } finally {
                l.unlock();
            }
        }
    }

    /**
     * Mock for NonReentrantLockFactory.
     */
    static class MockNonReentrantLockFactory implements NonReentrantLockFactory {
        public NonReentrantLock create() {
            return new MockNonReentrantLock();
        }
    }

    NonReentrantLockFactory mockFactory = new MockNonReentrantLockFactory();

    @Test
    public void testSingleThreadReentrancy() {
        MyReentrantLock l = new MyReentrantLock(mockFactory);
        l.lock();
        l.lock();
        l.unlock();
        l.unlock();
        Assertions.assertNull(l.owner);
    }

    @Test
    public void testUnlockByNotOwner() throws InterruptedException {
        MyReentrantLock l = new MyReentrantLock(mockFactory);
        l.lock();
        Thread wrongThread = new Thread(() -> {
            Assertions.assertThrows(IllegalMonitorStateException.class, l::unlock);
        });
        wrongThread.start();
        wrongThread.join();
        l.unlock();
    }

    @Test
    public void testMutualExclusion() throws InterruptedException {
        MyReentrantLock l = new MyReentrantLock(mockFactory);
        int[] sharedCounter = {0};
        int threadsCount = 1000;
        int incrementsPerThread = 10;
        CountDownLatch latch = new CountDownLatch(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    l.lock();
                    try {
                        sharedCounter[0]++;
                    } finally {
                        l.unlock();
                    }
                }
                latch.countDown();
            }).start();
        }
        latch.await();

        Assertions.assertEquals(10000, sharedCounter[0]);
    }
}
