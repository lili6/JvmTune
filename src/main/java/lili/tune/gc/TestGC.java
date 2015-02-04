package lili.tune.gc;

/**
 * Created by liguofang on 2015/2/4.
 * java -verbosegc classfile
 */
public class TestGC {

	public static void main(String[] args) {
	   new TestGC();
		System.gc();
		System.runFinalization();
	}
}
