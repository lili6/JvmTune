package lili.tune.oom;

import java.util.HashMap;

/**
 * Created by liguofang on 2015/2/6.
 * PermGen space
 * 解决办法：增加Perm区，运行class回收
 */
public class TestPermGen {

	public static void main(String[] args) {
		for(int i = 0; i<100000;i++) {
//			CglibBean bean = new CblibBean("geym.jvm.ch3.perm.bean"+i,new HashMap<K,V>());
		}
	}
}
