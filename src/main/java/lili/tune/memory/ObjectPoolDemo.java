package lili.tune.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by liguofang on 2015/1/23.
 * 使用对象缓存池：创建对象要消耗一定的CPU以及内存，使用对象缓存池一定程度上可降低JVM堆内存的使用。
 * NOTUSE POOL：
 * Execute summary: Round( 10 ) Thread Per Round( 100 ) Object Factor ( 10 ) Execute Time ( 8278 ) ms
 * USE POOL：
 *  Execute summary: Round( 10 ) Thread Per Round( 100 ) Object Factor ( 10 ) Execute Time ( 346 ) ms
 *
 */
public class ObjectPoolDemo {
	private static int executeTimes = 10;         //执行次数
	private static int maxFactor = 10;
	private static int threadCount = 100; //线程数
	private static final int NOTUSE_OBJECTPOOL = 1;
	private static final int USE_OBJECTPOOL =2;
	private static int runMode = USE_OBJECTPOOL;//NOTUSE_OBJECTPOOL;
	private static CountDownLatch latch = null;

	public static void  main(String[] args) throws Exception {
		Task task = new Task();
		long beginTime = System.currentTimeMillis();
		for (int i =0;i < executeTimes; i++) {
			System.out.println("Round:" + (i+1));
			latch = new CountDownLatch(threadCount);
			for (int j = 0; j < threadCount; j++) {
				new Thread(task).start();
			}
			latch.await();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Execute summary: Round( " + executeTimes + " ) Thread Per Round( " + threadCount
		               + " ) Object Factor ( " + maxFactor + " ) Execute Time ( " + (endTime - beginTime) + " ) ms");

}
	static class Task implements Runnable {
		@Override
		public void run() {
			for (int j =0; j<maxFactor; j++) {
				if (runMode == USE_OBJECTPOOL) {
					BigObjectPool.getInstance().getBigObject(j);
				} else {
					new BigObject(j);
				}
			}
			latch.countDown();
		}
	}

	static class BigObjectPool {
		private static final BigObjectPool self = new BigObjectPool();
		private final Map<Integer, BigObject>  cacheObjects = new HashMap<Integer, BigObject>();
		private BigObjectPool() {

		}
		public static BigObjectPool getInstance() {
			return self;
		}
		public BigObject getBigObject(int factor) {
			if (cacheObjects.containsKey(factor)){
				return cacheObjects.get(factor);
			}else {
				BigObject object = new BigObject(factor);
				cacheObjects.put(factor,object);
				return object;
			}
		}

	}
	static class BigObject {
		private byte[] bytes = null;

		public BigObject(int factor) {
			bytes = new byte[(factor +1) *1024*1024];
		}
		public byte[] getBytes() {
			return bytes;
		}
	}
}
