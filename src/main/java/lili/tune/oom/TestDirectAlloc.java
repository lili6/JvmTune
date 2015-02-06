package lili.tune.oom;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by liguofang on 2015/2/6.
 * 直接内存的溢出
 *       >java -Xmx10m -XX:+PrintGCDetails lili.tune.oom.TestDirectAlloc

 */
public class TestDirectAlloc {

	public static void main(String[] args) {

		for(int i=0;i<1024;i++) {
			ByteBuffer.allocate(1024*10240);
			System.out.println(i);
			System.gc();
		}
	}
}

/*
*
* [GC [PSYoungGen: 0K->0K(3072K)] 467K->467K(9728K), 0.0003568 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[Full GC [PSYoungGen: 0K->0K(3072K)] [ParOldGen: 467K->456K(5632K)] 467K->456K(8704K) [PSPermGen: 2474K->2474K(21504K)], 0.0053174 secs] [Times: user
=0.00 sys=0.00, real=0.00 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at java.nio.HeapByteBuffer.<init>(HeapByteBuffer.java:57)
        at java.nio.ByteBuffer.allocate(ByteBuffer.java:331)
        at lili.tune.oom.TestDirectAlloc.main(TestDirectAlloc.java:15)
Heap
 PSYoungGen      total 3072K, used 205K [0x00000000ffc80000, 0x0000000100000000, 0x0000000100000000)
  eden space 2560K, 8% used [0x00000000ffc80000,0x00000000ffcb3480,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 6656K, used 456K [0x00000000ff600000, 0x00000000ffc80000, 0x00000000ffc80000)
  object space 6656K, 6% used [0x00000000ff600000,0x00000000ff6723e0,0x00000000ffc80000)
 PSPermGen       total 21504K, used 2508K [0x00000000fa400000, 0x00000000fb900000, 0x00000000ff600000)
  object space 21504K, 11% used [0x00000000fa400000,0x00000000fa673370,0x00000000fb900000)

* */
