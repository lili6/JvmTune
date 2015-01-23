package lili.tune.concurrent;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liguofang on 2015/1/23.
 * 尽可能少用锁：尽可能只对需要控制的资源做加锁操作
 * 在handle的时候execute something 不需要用锁，减少锁的使用范围
 * Execute summary: Round( 10 ) Thread Per Round( 400 ) Execute Time ( 8202 ) ms
 */
public class ReduceLockHotDemo {
	private static int executeTimes = 10;
	private static int threadCount = Runtime.getRuntime().availableProcessors() * 100;
	private static CountDownLatch latch = null;

	public static void main(String[] args) throws Exception {
		HandleTask task = new HandleTask();
		long beginTime = System.currentTimeMillis();
		for (int i = 0; i < executeTimes; i++) {
			System.out.println("Round: " + (i + 1));
			latch = new CountDownLatch(threadCount);
			for (int j = 0; j < threadCount; j++) {
				new Thread(task).start();
			}
			latch.await();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Execute summary: Round( " + executeTimes + " ) Thread Per Round( " + threadCount
				+ " ) Execute Time ( " + (endTime - beginTime) + " ) ms");
	}

	static class HandleTask implements Runnable {
		private final Random random = new Random();
		@Override
		public void run() {
			Handler.getInstance().handle(random.nextInt(10000));
			latch.countDown();
		}

	}

	static class Handler {
		private static final Handler self = new Handler();
		private final Random random = new Random();
		private final Lock lock = new ReentrantLock();
		private Handler() {

		}
		public static Handler getInstance() {
			return self;
		}
		public void handle(int id) {
			// execute sth don't need lock
			try {
				Thread.sleep(random.nextInt(5));
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				lock.lock();

				// execute sth
				try {
					Thread.sleep(random.nextInt(5));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				lock.unlock();
			}
		}
	}
}
/*
Round: 1
......
Round: 10
Execute summary: Round( 10 ) Thread Per Round( 200 ) Execute Time ( 5547 ) ms
*/
