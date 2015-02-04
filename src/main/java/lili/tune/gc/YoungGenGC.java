package lili.tune.gc;

/**
 * Created by liguofang on 2015/2/4.
 * 内存分配测试代码
 */
public class YoungGenGC {
	private static final int _1MB=1024 * 1024;
	public static void main(String[] args) {
//		 testAllocation();
//		testHandlePromotion();
		testPretenureSizeThreshold();
		// testTenuringThreshold();
		// testTenuringThreshold2();

	}

	/**
	 * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
	 * 规则一：通常情况下，对象在eden中分配。当eden无法分配时，触发一次Minor GC。
	 执行testAllocation()方法后输出了GC日志以及内存分配状况。-Xms20M -Xmx20M -Xmn10M这3个参数确定了Java堆大小为20M，
	 不可扩展，其中10M分配给新生代，剩下的10M即为老年代。-XX:SurvivorRatio=8决定了新生代中eden与survivor的空间比例是1：8，
	 从输出的结果也清晰的看到“eden space 8192K、from space 1024K、to space 1024K”的信息，新生代总可用空间为9216K（eden+1个survivor）。
	 我们也注意到在执行testAllocation()时出现了一次Minor GC，GC的结果是新生代6651K变为148K，而总占用内存则几乎没有减少
	 （因为几乎没有可回收的对象）。这次GC是发生的原因是为allocation4分配内存的时候，eden已经被占用了6M，
	 剩余空间已不足分配allocation4所需的4M内存，
	 因此发生Minor GC。GC期间虚拟机发现已有的3个2M大小的对象全部无法放入survivor空间（survivor空间只有1M大小），
	 所以直接转移到老年代去。GC后4M的allocation4对象分配在eden中。
	 */
	@SuppressWarnings("unused")
	public static void testAllocation() {
		byte[] allocation1, allocation2, allocation3, allocation4;
		allocation1 = new byte[2 * _1MB];
		allocation2 = new byte[2 * _1MB];
		allocation3 = new byte[2 * _1MB];
		allocation4 = new byte[4 * _1MB];  // 出现一次Minor GC
	}

	/**
	 * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
	 * -XX:PretenureSizeThreshold=3145728
	 * 如果设置-XX:PretenureSizeThreshold则，如果对想大于设置值将直接在老年代分配。
	 * 执行后，我们发现eden空间几乎没有被使用，而老年代的10M空间被使用了40%，也就是
	 * 说4M的allocation对象直接就分配在老年代中，则是因为参数被设置了3M，因此超过3M
	 * 的对象都会直接从老年代分配
	 * java -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3145728 lili.
	 tune.gc.YoungGenGC
	 */
	@SuppressWarnings("unused")
	public static void testPretenureSizeThreshold() {
		System.out.println("测试直接分配到老年代中");
		byte[] allocation;
		allocation = new byte[4 * _1MB];  //直接分配在老年代中
	}
	/**
	 * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=1
	 * -XX:+PrintTenuringDistribution
	 * 规则三：在eden经过GC后存活，并且survivor能容纳的对象，将移动到survivor空间内，如果对象在survivor中继续熬过若干次回收
	 * （默认为15次）将会被移动到老年代中。回收次数由MaxTenuringThreshold设置。
     分别以-XX:MaxTenuringThreshold=1和-XX:MaxTenuringThreshold=15两种设置来执行testTenuringThreshold()，
	 方法中allocation1对象需要256K内存，survivor空间可以容纳。当MaxTenuringThreshold=1时，allocation1对象在第二次GC发生时进入老年代，
	 新生代已使用的内存GC后非常干净的变成0KB。而MaxTenuringThreshold=15时，第二次GC发生后，allocation1对象则还留在新生代survivor空间，
	 这时候新生代仍然有404KB被占用。
	 */
	@SuppressWarnings("unused")
	public static void testTenuringThreshold() {
		byte[] allocation1, allocation2, allocation3;
		allocation1 = new byte[_1MB / 4];  // 什么时候进入老年代决定于XX:MaxTenuringThreshold设置
		allocation2 = new byte[4 * _1MB];
		allocation3 = new byte[4 * _1MB];
		allocation3 = null;
		allocation3 = new byte[4 * _1MB];
	}

	/**
	 * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=15
	 * -XX:+PrintTenuringDistribution
	 * 规则四：如果在survivor空间中相同年龄所有对象大小的累计值大于survivor空间的一半，大于或等于个年龄的对象就可以直接进入老年代，
	 * 无需达到MaxTenuringThreshold中要求的年龄。

	 执行testTenuringThreshold2()方法，并将设置-XX:MaxTenuringThreshold=15，发现运行结果中survivor占用仍然为0%，
	 而老年代比预期增加了6%，也就是说allocation1、allocation2对象都直接进入了老年代，而没有等待到15岁的临界年龄。
	 因为这2个对象加起来已经到达了512K，并且它们是同年的，满足同年对象达到survivor空间的一半规则。
	 我们只要注释掉其中一个对象new操作，就会发现另外一个就不会晋升到老年代中去了。
	 */
	@SuppressWarnings("unused")
	public static void testTenuringThreshold2() {
		byte[] allocation1, allocation2, allocation3, allocation4;
		allocation1 = new byte[_1MB / 4];   // allocation1+allocation2大于survivo空间一半
		allocation2 = new byte[_1MB / 4];
		allocation3 = new byte[4 * _1MB];
		allocation4 = new byte[4 * _1MB];
		allocation4 = null;
		allocation4 = new byte[4 * _1MB];
	}

	/**
	 * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:-HandlePromotionFailure
	 * 规则五：在Minor GC触发时，会检测之前每次晋升到老年代的平均大小是否大于老年代的剩余空间，如果大于，改为直接进行一次Full GC，
	 * 如果小于则查看HandlePromotionFailure设置看看是否允许担保失败，如果允许，那仍然进行Minor GC，如果不允许，则也要改为进行一次Full GC。

	前面提到过，新生代才有复制收集算法，但为了内存利用率，只使用其中一个survivor空间来作为轮换备份，因此当出现大量对象在GC后仍然存活的情况
	（最极端就是GC后所有对象都存活），就需要老年代进行分配担保，把survivor无法容纳的对象直接放入老年代。与生活中贷款担保类似，
	老年代要进行这样的担保，前提就是老年代本身还有容纳这些对象的剩余空间，一共有多少对象在GC之前是无法明确知道的，
	所以取之前每一次GC晋升到老年代对象容量的平均值与老年代的剩余空间进行比较决定是否进行Full GC来让老年代腾出更多空间。

	取平均值进行比较其实仍然是一种动态概率的手段，也就是说如果某次Minor GC存活后的对象突增，大大高于平均值的话，依然会导致担保失败，
	这样就只好在失败后重新进行一次Full GC。虽然担保失败时做的绕的圈子是最大的，但大部分情况下都还是会将HandlePromotionFailure打开，
	避免Full GC过于频繁。

	 */
	@SuppressWarnings("unused")
	public static void testHandlePromotion() {             //担保
		byte[] allocation1, allocation2, allocation3, allocation4, allocation5, allocation6, allocation7;
		allocation1 = new byte[2 * _1MB];
		allocation2 = new byte[2 * _1MB];
		allocation3 = new byte[2 * _1MB];
		allocation1 = null;
		allocation4 = new byte[2 * _1MB];
		allocation5 = new byte[2 * _1MB];
		allocation6 = new byte[2 * _1MB];
		allocation4 = null;
		allocation5 = null;
		allocation6 = null;
		allocation7 = new byte[2 * _1MB];
	}
}
