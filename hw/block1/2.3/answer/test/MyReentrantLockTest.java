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

        public void lock() {
            if (l.isHeldByCurrentThread()) {
                throw new IllegalMonitorStateException("Mock: Repeated lock on NonReentrantLock");
            }
            l.lock();
        }

        public void unlock() {
            if (!l.isHeldByCurrentThread()) {
                throw new IllegalMonitorStateException("Mock: Unlocking thread is not the owner");
            }
            l.unlock();
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
        int incrementsPerThread = 10000;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (int j = 0; j < incrementsPerThread; j++) {
                    l.lock();
                    try {
                        sharedCounter[0]++;
                    } finally {
                        l.unlock();
                    }
                }
                finish.countDown();
            }).start();
        }
        start.countDown();
        finish.await();

        Assertions.assertEquals(10000000, sharedCounter[0]);
    }
}
