package lili.tune.cpu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by liguofang on 2015/1/23.
 * 未充分利用CPU：在能并行处理的场景中未使用足够的线程
 * （线程增加：CPU资源消耗可接受且不会带来激烈竞争锁的场景下）
 * 与有效利用CPU不同的是，这里使用的join ，那里使用的是CountDownLatch
 * 可以进行并行计算，互不影响
 */
public class CpuNotUseEffectiveDemo {
	private static int executeTimes = 10;
	private static int taskCount = 200;

	public static void main(String[] args) throws Exception {
		Task task = new Task();
		for (int i = 0; i < taskCount; i++) {
			task.addTask(Integer.toString(i));
		}

		long beginTime = System.currentTimeMillis();
		for (int i = 0; i < executeTimes; i++) {
			System.out.println("Round: " + (i + 1));
			Thread thread = new Thread(task);
			thread.start();
			thread.join();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Execute summary: Round( " + executeTimes + " ) TaskCount Per Round( " + taskCount
				+ " ) Execute Time ( " + (endTime - beginTime) + " ) ms");
	}

	static class Task implements Runnable {
		List<String> tasks = new ArrayList<String>();
		Random random = new Random();
		boolean exitFlag = false;

		public void addTask(String task) {
			List<String> copyTasks = new ArrayList<String>(tasks);
			copyTasks.add(task);

			tasks = copyTasks;
		}
		@Override
		public void run() {
			List<String> runTasks = tasks;
			List<String> removeTasks = new ArrayList<String>();
			for (String task : runTasks) {
				try {
					Thread.sleep(random.nextInt(10));
				} catch (Exception e) {
					e.printStackTrace();
				}

				removeTasks.add(task);
			}

			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
/*
Round: 1
......
Round: 10
Execute summary: Round( 10 ) TaskCount Per Round( 200 ) Execute Time ( 10687 ) ms
*/
