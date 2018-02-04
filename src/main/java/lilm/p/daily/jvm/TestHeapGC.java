package lilm.p.daily.jvm;

/**
 * Created by lilm on 18-1-15.
 */
public class TestHeapGC {
	
	public static void main(String[] args) {
		byte[] b1 = new byte[1024*1024/2];
		byte[] b2 = new byte[1024*1024*8];
		
		b2 = null;
		b2 = new byte[1024*1024*8]; // 进行一次新生代的
		System.gc();
	}
	
}
