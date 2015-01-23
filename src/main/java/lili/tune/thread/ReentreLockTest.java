package lili.tune.thread;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liguofang on 2015/1/23.
 * 结论：
 * 1)使用Lock的性能比使用Synchronized关键字要提高4——5倍
 * 2）使用信号量实现同步的速度大约比Synchronized要慢10-20%
 * 3）使用atomic包的AtomicInteger速度要比Lock快1个数量级
 */
public class ReentreLockTest {
	private static long COUNT = 1000000;
	private static Lock lock = new ReentrantLock();
	private static long lockCounter = 0;
	private static long syncCounter = 0;
	private static long semaCounter = 0;
	private static AtomicLong atomicCounter = new AtomicLong(0);
	private static Object syncLock =  new Object();
	private static Semaphore mutex = new Semaphore(1);//信号量

	public static void testLock(int num, int threadCount) {

	}

	/**
	 * 获取锁计数器
	 * @return
	 */
	static  long getLock() {
		lock.lock();
		try {
			return lockCounter;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获取同步计数器
	 * @return
	 */
	static long getSync() {
		synchronized (syncLock) {
			return syncCounter;
		}
	}
	static long getAtom() {
		return atomicCounter.get();
	}

	/**
	 * 获取信号量计数器
	 * @return
	 * @throws InterruptedException
	 */
	static long getSemaphore() throws InterruptedException {
		mutex.acquire();
		try {
			return semaCounter;
		} finally {
			mutex.release();
		}
	}
	static long getLockInc() {
		lock.lock();
		try {
			return ++lockCounter;
		} finally {
			lock.unlock();
		}
	}
	static long getSyncInc(){
		synchronized (syncLock) {
			return ++syncCounter;
		}
	}
	static long getAtomInc() {
		return atomicCounter.getAndIncrement();
	}

	static class SemaTest extends Test {
		public SemaTest(String id, CyclicBarrier barrier,long count,
		                int threadNum, ExecutorService executorService) {
			super(id,barrier,count,threadNum,executorService);
		}

		@Override
		protected void test() {
			try {
				getSemaphore();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static class LockTest extends Test {
		public LockTest(String id, CyclicBarrier barrier,long count,
		                int threadNum, ExecutorService executorService) {
			super(id,barrier,count,threadNum,executorService);
		}

		@Override
		protected void test() {
			getLock();
		}
	}

	static class SyncTest extends Test {
		public SyncTest(String id, CyclicBarrier barrier,long count,
		                int threadNum, ExecutorService executorService) {
			super(id,barrier,count,threadNum,executorService);
		}

		@Override
		protected void test() {
			getSync();
		}
	}
	static class AtomicTest extends Test {
		public AtomicTest(String id, CyclicBarrier barrier,long count,
		                int threadNum, ExecutorService executorService) {
			super(id,barrier,count,threadNum,executorService);
		}

		@Override
		protected void test() {
			getAtom();
		}
	}

	public static void test(String id, long count, int threadNum,
	                        ExecutorService executor) {
		final CyclicBarrier barrier = new CyclicBarrier(threadNum +1,
				new Thread(){
					@Override
					public void run(){

					}
				});

		System.out.println("==================================================");
		System.out.println("count = " + count + "/t" + "Thread Count = "
		                + threadNum);

		new LockTest("Lock ", barrier, COUNT, threadNum, executor).startTest();
		new SyncTest("Sync ", barrier, COUNT, threadNum, executor).startTest();
		new AtomicTest("Atom ", barrier, COUNT, threadNum, executor)
		                .startTest();
		new SemaTest("Sema ", barrier, COUNT, threadNum, executor)
		                .startTest();
		System.out.println("==============================");
	}

	public static void main(String[] args) {
        for (int i = 1; i < 5; i++) {
	            ExecutorService executor = Executors.newFixedThreadPool(10 * i);
	            test("5555", COUNT * i, 10 * i, executor);
	        }
    }


}
