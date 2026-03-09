/**
 * A reentrant lock implementation that uses a non-reentrant lock.
 */
public class MyReentrantLock {
    private final NonReentrantLockFactory factory;
    /**
     * Lock for thread-safe access to the MyReentrantLock state.
     */
    private final NonReentrantLock l;
    Thread owner;
    private int count;

    public MyReentrantLock(NonReentrantLockFactory factory) {
        this.factory = factory;
        l = factory.create();
        owner = null;
        count = 0;
    }

    public void lock() {
        int delay = 1;
        boolean interrupt_flag = false;
        while(!tryLock()) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                interrupt_flag = true;
            }
            if (delay < 128) delay *= 2;
        }
        if (interrupt_flag) Thread.currentThread().interrupt();
    }

    private boolean tryLock() {
        l.lock();
        try {
            if (owner == null) {
                owner = Thread.currentThread();
                count = 1;
                return true;
            } else if (owner == Thread.currentThread()) {
                count++;
                return true;
            } else return false;
        } finally {
            l.unlock();
        }
    }

    public void unlock() {
        l.lock();
        try {
            if (owner == Thread.currentThread()) {
                count -= 1;
                if (count == 0) owner = null;
            } else throw new IllegalMonitorStateException("Unlocking thread is not the owner");
        } finally {
            l.unlock();
        }
    }
}
