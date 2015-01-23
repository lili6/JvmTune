package lili.tune.fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
 * Created by liguofang on 2015/1/23.
 * 文件IO消耗严重的原因主要是多个线程在写进行大量的数据到同一文件，
 * 导致文件很快变得很大，从而写入速度越来越慢，并造成各线程激烈争抢文件锁。

 */
public class IOWaitHighDemo {  private String fileName = "iowait.log";

	private static int threadCount = Runtime.getRuntime().availableProcessors();

	private Random random = new Random();

	public static void main(String[] args) throws Exception {
		if (args.length == 1) {
			threadCount = Integer.parseInt(args[1]);
		}

		IOWaitHighDemo demo = new IOWaitHighDemo();
		demo.runTest();
	}

	private void runTest() throws Exception {
		File file = new File(fileName);
		file.createNewFile();

		for (int i = 0; i < threadCount; i++) {
			new Thread(new Task()).start();
		}

	}

	class Task implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					StringBuilder strBuilder = new StringBuilder("====begin====/n");
					String threadName = Thread.currentThread().getName();
					for (int i = 0; i < 100000; i++) {
						strBuilder.append(threadName);
						strBuilder.append("/n");
					}
					strBuilder.append("====end====/n");

					BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
					writer.write(strBuilder.toString());
					writer.close();
					Thread.sleep(random.nextInt(10));
				} catch (Exception e) {

				}
			}
		}

	}

}

/**
 * /E/study/github (master)
 $ jstack 9084
 2015-01-23 15:01:39
 Full thread dump Java HotSpot(TM) 64-Bit Server VM (24.45-b08 mixed mode):

 "DestroyJavaVM" prio=6 tid=0x000000000210f000 nid=0x2278 waiting on condition [0
 x0000000000000000]
 java.lang.Thread.State: RUNNABLE

 "Thread-3" prio=6 tid=0x0000000009e37800 nid=0x2548 runnable [0x000000000b06f000
 ]
 java.lang.Thread.State: RUNNABLE
 at java.io.FileOutputStream.writeBytes(Native Method)
 at java.io.FileOutputStream.write(FileOutputStream.java:345)
 at sun.nio.cs.StreamEncoder.writeBytes(StreamEncoder.java:221)
 at sun.nio.cs.StreamEncoder.implWrite(StreamEncoder.java:282)
 at sun.nio.cs.StreamEncoder.write(StreamEncoder.java:125)
 - locked <0x00000000fab118d0> (a java.io.FileWriter)
 at java.io.OutputStreamWriter.write(OutputStreamWriter.java:207)
 at java.io.BufferedWriter.flushBuffer(BufferedWriter.java:129)
 - locked <0x00000000fab118d0> (a java.io.FileWriter)
 at java.io.BufferedWriter.write(BufferedWriter.java:230)
 - locked <0x00000000fab118d0> (a java.io.FileWriter)
 at java.io.Writer.write(Writer.java:157)
 at lili.tune.fileio.IOWaitHighDemo$Task.run(IOWaitHighDemo.java:53)
 at java.lang.Thread.run(Thread.java:744)

 "Thread-2" prio=6 tid=0x0000000009e36800 nid=0x19bc runnable [0x000000000af3f000
 ]
 java.lang.Thread.State: RUNNABLE
 at java.io.FileOutputStream.close0(Native Method)
 at java.io.FileOutputStream.close(FileOutputStream.java:393)
 at sun.nio.cs.StreamEncoder.implClose(StreamEncoder.java:320)
 at sun.nio.cs.StreamEncoder.close(StreamEncoder.java:149)
 - locked <0x00000000f98f12a0> (a java.io.FileWriter)
 at java.io.OutputStreamWriter.close(OutputStreamWriter.java:233)
 at java.io.BufferedWriter.close(BufferedWriter.java:266)
 - locked <0x00000000f98f12a0> (a java.io.FileWriter)
 at lili.tune.fileio.IOWaitHighDemo$Task.run(IOWaitHighDemo.java:54)
 at java.lang.Thread.run(Thread.java:744)

 "Thread-1" prio=6 tid=0x0000000009db3800 nid=0x243c runnable [0x000000000acee000
 ]
 java.lang.Thread.State: RUNNABLE
 at java.io.FileOutputStream.writeBytes(Native Method)
 at java.io.FileOutputStream.write(FileOutputStream.java:345)
 at sun.nio.cs.StreamEncoder.writeBytes(StreamEncoder.java:221)
 at sun.nio.cs.StreamEncoder.implWrite(StreamEncoder.java:282)
 at sun.nio.cs.StreamEncoder.write(StreamEncoder.java:125)
 - locked <0x00000000fb143410> (a java.io.FileWriter)
 at java.io.OutputStreamWriter.write(OutputStreamWriter.java:207)
 at java.io.BufferedWriter.flushBuffer(BufferedWriter.java:129)
 - locked <0x00000000fb143410> (a java.io.FileWriter)
 at java.io.BufferedWriter.write(BufferedWriter.java:230)
 - locked <0x00000000fb143410> (a java.io.FileWriter)
 at java.io.Writer.write(Writer.java:157)
 at lili.tune.fileio.IOWaitHighDemo$Task.run(IOWaitHighDemo.java:53)
 at java.lang.Thread.run(Thread.java:744)

 "Thread-0" prio=6 tid=0x0000000009dca000 nid=0x197c runnable [0x000000000aa8f000
 ]
 java.lang.Thread.State: RUNNABLE
 at java.io.FileOutputStream.writeBytes(Native Method)
 at java.io.FileOutputStream.write(FileOutputStream.java:345)
 at sun.nio.cs.StreamEncoder.writeBytes(StreamEncoder.java:221)
 at sun.nio.cs.StreamEncoder.implWrite(StreamEncoder.java:282)
 at sun.nio.cs.StreamEncoder.write(StreamEncoder.java:125)
 - locked <0x00000000fb13a800> (a java.io.FileWriter)
 at java.io.OutputStreamWriter.write(OutputStreamWriter.java:207)
 at java.io.BufferedWriter.flushBuffer(BufferedWriter.java:129)
 - locked <0x00000000fb13a800> (a java.io.FileWriter)
 at java.io.BufferedWriter.write(BufferedWriter.java:230)
 - locked <0x00000000fb13a800> (a java.io.FileWriter)
 at java.io.Writer.write(Writer.java:157)
 at lili.tune.fileio.IOWaitHighDemo$Task.run(IOWaitHighDemo.java:53)
 at java.lang.Thread.run(Thread.java:744)

 "Monitor Ctrl-Break" daemon prio=6 tid=0x0000000009dcb800 nid=0x2264 runnable [0
 x000000000a65f000]
 java.lang.Thread.State: RUNNABLE
 at java.net.DualStackPlainSocketImpl.accept0(Native Method)
 at java.net.DualStackPlainSocketImpl.socketAccept(DualStackPlainSocketIm
 pl.java:131)
 at java.net.AbstractPlainSocketImpl.accept(AbstractPlainSocketImpl.java:
 398)
 at java.net.PlainSocketImpl.accept(PlainSocketImpl.java:198)
 - locked <0x00000000c18ac7e0> (a java.net.SocksSocketImpl)
 at java.net.ServerSocket.implAccept(ServerSocket.java:530)
 at java.net.ServerSocket.accept(ServerSocket.java:498)
 at com.intellij.rt.execution.application.AppMain$1.run(AppMain.java:85)
 at java.lang.Thread.run(Thread.java:744)

 "Service Thread" daemon prio=6 tid=0x0000000009d59800 nid=0x27f8 runnable [0x000
 0000000000000]
 java.lang.Thread.State: RUNNABLE

 "C2 CompilerThread1" daemon prio=10 tid=0x0000000009d40800 nid=0x2530 waiting on
 condition [0x0000000000000000]
 java.lang.Thread.State: RUNNABLE

 "C2 CompilerThread0" daemon prio=10 tid=0x000000000850a000 nid=0x26cc waiting on
 condition [0x0000000000000000]
 java.lang.Thread.State: RUNNABLE

 "Attach Listener" daemon prio=10 tid=0x0000000008509000 nid=0x180c waiting on co
 ndition [0x0000000000000000]
 java.lang.Thread.State: RUNNABLE

 "Signal Dispatcher" daemon prio=10 tid=0x0000000008502000 nid=0x3d4 runnable [0x
 0000000000000000]
 java.lang.Thread.State: RUNNABLE

 "Finalizer" daemon prio=8 tid=0x0000000008495000 nid=0x2728 in Object.wait() [0x
 000000000986f000]
 java.lang.Thread.State: WAITING (on object monitor)
 at java.lang.Object.wait(Native Method)
 - waiting on <0x00000000c1896378> (a java.lang.ref.ReferenceQueue$Lock)
 at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:135)
 - locked <0x00000000c1896378> (a java.lang.ref.ReferenceQueue$Lock)
 at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:151)
 at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:189)

 "Reference Handler" daemon prio=10 tid=0x000000000848c000 nid=0x2340 in Object.w
 ait() [0x000000000968f000]
 java.lang.Thread.State: WAITING (on object monitor)
 at java.lang.Object.wait(Native Method)
 - waiting on <0x00000000c18960b8> (a java.lang.ref.Reference$Lock)
 at java.lang.Object.wait(Object.java:503)
 at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:133)
 - locked <0x00000000c18960b8> (a java.lang.ref.Reference$Lock)

 "VM Thread" prio=10 tid=0x0000000008488800 nid=0x26dc runnable

 "GC task thread#0 (ParallelGC)" prio=6 tid=0x0000000001fbe800 nid=0x2510 runnabl
 e

 "GC task thread#1 (ParallelGC)" prio=6 tid=0x0000000001fc0000 nid=0x257c runnabl
 e

 "GC task thread#2 (ParallelGC)" prio=6 tid=0x0000000001fc1800 nid=0xffc runnable


 "GC task thread#3 (ParallelGC)" prio=6 tid=0x0000000001fc3800 nid=0x2630 runnabl
 e

 "VM Periodic Task Thread" prio=10 tid=0x0000000009d63000 nid=0x23a4 waiting on c
 ondition

 JNI global references: 158


 */