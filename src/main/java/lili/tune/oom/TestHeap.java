package lili.tune.oom;

import java.util.ArrayList;

/**
 * Created by liguofang on 2015/2/6.
 * 测试创建大量的对象，对溢出的情况
 * -Xmx10M -Xms10M
 */
public class TestHeap {

	public static void main(String[] args) {
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		for(int i=0;i<1024;i++) {
			list.add(new byte[1024*1024]);
		}
	}
}
/*
很快就会报错
java -Xmx10m -Xms10m lili.tune.oom.TestHeap
		Exception in thread "main" java.lang.OutOfMemoryError: Java oom space
		at lili.tune.oom.TestHeap.main(TestHeap.java:15)
	占用大量的堆空间，直接溢出
	解决方法：增加堆空间，及时释放内存。
*/
