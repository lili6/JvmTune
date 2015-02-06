package lili.tune.memory;

import java.nio.ByteBuffer;

/**
 * Created by liguofang on 2015/1/23.
 *  direct bytebuffer消耗的是jvm堆外的内存，但同样是基于GC方式来释放的。
 */
public class MemoryHighDemo {
	public static void main(String[] args) throws Exception{
		Thread.sleep(20000);
		System.out.println("ready to create bytes,so jvm oom will be used");
		byte[] bytes=new byte[128*1000*1000];
		bytes[0]=1;
		bytes[1]=2;
		Thread.sleep(10000);
		System.out.println("ready" +
				" to allocate & put direct bytebuffer,no jvm oom should be used");
		ByteBuffer buffer= ByteBuffer.allocateDirect(128 * 1024 * 1024);
		buffer.put(bytes);
		buffer.flip();
		Thread.sleep(10000);
		System.out.println("ready to gc,jvm oom will be freed");
		bytes=null;
		System.gc();
		Thread.sleep(10000);
		System.out.println("ready to get bytes,then jvm oom will be used");
		byte[] resultbytes=new byte[128*1000*1000];
		buffer.get(resultbytes);
		System.out.println("resultbytes[1] is: "+resultbytes[1]);
		Thread.sleep(10000);
		System.out.println("ready to gc all");
		buffer=null;
		resultbytes=null;
		System.gc();
		Thread.sleep(10000);
	}
}
