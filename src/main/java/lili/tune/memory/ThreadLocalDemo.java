package lili.tune.memory;

import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liguofang on 2015/1/23.
 * 释放不必要的引用：代码持有了不需要的对象引用，造成这些对象无法被GC，从而占据了JVM堆内存。
 * （使用ThreadLocal：注意在线程内动作执行完毕时，需执行 ThreadLocal.set把对象清除，避免持有不必要的对象引用）
 * 如不释放threadlocal，则系统永远无法到gc那个过程。
 */
public class ThreadLocalDemo {

	public void run() {
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		executorService.execute(new Task()) ;
		System.gc();
	}
	class Task implements Runnable {

		@Override
		public void run() {
			ThreadLocal<byte[]> local = new ThreadLocal<byte[]>();
			local.set(new byte[1024*1024*30]);
			//业务逻辑                                           。。。

			 local.set(null); //一定要释放不必要的引用。。。
		}
	}
	public static void main(String[] args) {
		ThreadLocalDemo demo = new ThreadLocalDemo();
		demo.run();
	}
}
