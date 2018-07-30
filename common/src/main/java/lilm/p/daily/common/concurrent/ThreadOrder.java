package lilm.p.daily.common.concurrent;

/**
 * Created by lilm on 18-7-1.
 */
public class ThreadOrder {
	
	static class MyThread implements Runnable {
		private String str;
		
		public MyThread(String str) {
			this.str = str;
		}
		
		@Override
		public void run() {
			System.out.println("Str:" + str);
		}
	}
	
	public static void main(String[] args) throws Exception{
		Thread t1 = new Thread(new MyThread("A"));
		t1.start();
		t1.join();
		
		Thread t2 = new Thread(new MyThread("B"));
		t2.start();
		t2.join();
		
		Thread t3 = new Thread(new MyThread("C"));
		t3.start();
		
	}
}
