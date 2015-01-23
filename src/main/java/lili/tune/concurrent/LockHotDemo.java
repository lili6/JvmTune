package lili.tune.concurrent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;

/**
 * Created by liguofang on 2015/1/23.
 *
 * 锁竞争的状况会比较明显，这时候线程很容易处于等待锁的状况，
 * 从而导致性能下降以及CPU sy上升
 * （CPU sy 高的原因主要是线程的运行状态要经常切换，对于这种情况，常见的一种优化方法是减少线程数。）
 * Execute summary: Round( 10 ) Thread Per Round( 400 ) Execute Time ( 18192 ) ms
 */
public class LockHotDemo {
	private static int executeTimes =10;
	private static int threadCount = Runtime.getRuntime().availableProcessors()*100;
	private static CountDownLatch latch = null;

	public static  void main(String[] args) throws Exception {
		HandleTask  task = new HandleTask();
		long    beginTime = System.currentTimeMillis();
		for (int i = 0; i<executeTimes; i++) {
			System.out.println("Round:"+(i+1));
			latch = new CountDownLatch((threadCount));//初始化计数器
			for (int j=0;j<threadCount;j++){
				new Thread(task).start();
			}
			latch.await();
		}

		long  endTime = System.currentTimeMillis();
		System.out.print("Execute summary: Round( " + executeTimes + " ) Thread Per Round( " + threadCount
		               + " ) Execute Time ( " + (endTime - beginTime) + " ) ms");
	}

	static  class HandleTask implements Runnable {
		private final Random random = new Random();

		@Override
		public void run() {
		  	Handler.getInstance().handle(random.nextInt(10000));
			latch.countDown();
		}
	}

	static class  Handler {
		private static final Handler self = new Handler();
		private final Random random = new Random();
		private final Lock  lock = new ReentrantLock();
		private Handler() {

		}
		public static Handler getInstance(){
			return self;
		}
		public void handle(int id) {
			try {
				lock.lock();
				//execute something
				try {
					Thread.sleep(random.nextInt(10));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				lock.unlock();
			}


		}
	}

}
