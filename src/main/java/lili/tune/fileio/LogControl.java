package lili.tune.fileio;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liguofang on 2015/1/23.
 * 日志控制：采用简单策略为统计一段时间内日志输出频率， 当超出这个频率时，一段时间内不再写log
 *
 * //TODO 有问题，没有测出期望的结果
 */
public class LogControl {
	public static void main(String[] args) {
		for (int i =1; i <= 1000; i++) {
			if (LogControl.isLog()) {
				System.out.println("errorINFO  " + i);
			}
			if (i %100==0) {//被100整除
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static final long INTERVAL = 100;
	private static final long PUNISH_TIME = 50;
	private static final int  ERROR_THRESHOLD = 5; //超出时间频率
	private  static AtomicInteger count = new AtomicInteger(0);
	private static long beginTime;
	private static long punishTimeEnd;
	     //忽略此处的并发问题
	 public  static boolean isLog() {

		 if (punishTimeEnd > 0 && punishTimeEnd >System.currentTimeMillis()) {
			 return false;
		 }
		 //重新计数
		 if (count.getAndDecrement() == 0 ) {
			 beginTime = System.currentTimeMillis();
			 return true;
		 }else {//已在计数
			//超过阀门，设置count为0并设置一段时间内不写日志
			 if (count.get() > ERROR_THRESHOLD) {
				 count.set(0);
				 punishTimeEnd = PUNISH_TIME + System.currentTimeMillis();
				 return  false;
			 }
			 //没有超过阀门，且当前时间已超过技术周期，则重新计算
			 else if (System.currentTimeMillis() > (beginTime + INTERVAL)) {
				 count.set(0);
			 }
			 return true;


		 }

	 }

}
