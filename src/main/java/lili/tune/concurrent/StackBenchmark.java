package lili.tune.concurrent;

import java.util.Stack;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by liguofang on 2015/1/23.
 * 基准测试：Treiber算法实现Stack、同步实现的Stack
 * 并发实现的ConcurrentStack比java.util.Stack的性能好
 * 前者利用CAS原理
 * Stack consume Time:  35 ms
 ConcurrentStack consume Time:  27 ms
 */
public class StackBenchmark {
	public static void main(String[] args) throws Exception {
		StackBenchmark stackBenchmark = new StackBenchmark();
		stackBenchmark.run();
	}
	private Stack<String> stack = new Stack<String>();
	private ConcurrentStack<String> concurrentStack = new ConcurrentStack<String>();
	private static  final  int THREAD_COUNT = 300;
	private CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
	private CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);

	public void run() throws Exception {
		StackTask stackTask = new StackTask();
		long beginTime = System.currentTimeMillis();
		for (int i = 0; i<THREAD_COUNT; i++) {
			new Thread(stackTask).start();
		}
		latch.await();
		long endTime = System.currentTimeMillis();
		System.out.println("Stack consume Time:  " + (endTime - beginTime) + " ms");
		//=========================================================================
		latch = new CountDownLatch(THREAD_COUNT);
		barrier = new CyclicBarrier(THREAD_COUNT);
		ConcurrentStackTask concurrentStackTask = new ConcurrentStackTask();
		beginTime = System.currentTimeMillis();
		for (int i = 0; i < THREAD_COUNT; i++) {
			new Thread(concurrentStackTask).start();
		}
		latch.await();
		endTime = System.currentTimeMillis();
		System.out.println("ConcurrentStack consume Time:  " + (endTime - beginTime) + " ms");
	}

	class StackTask implements Runnable {
		@Override
		public void run() {
			try {
				barrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
			for (int i = 0; i <10; i++) {
				stack.push(Thread.currentThread().getName());
				stack.pop();
			}
			latch.countDown();
		}
	}

	class ConcurrentStackTask implements Runnable{
		@Override
		public void run() {
			try {
				barrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
			for (int i = 0; i<10; i++) {
				concurrentStack.push(Thread.currentThread().getName());
				concurrentStack.pop();
			}
			latch.countDown();
		}
	}
}



















