package lilm.p.daily.concurrent.produce_consume;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lilm on 18-1-7.
 */
public class ProduceConsume1 {
	
	private int count = 0;
	
	private static final int MAX = 10;
	private static final String LOCK = "LOCK";
	
	class Produce implements Runnable {
		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() + " produce");
			for (;;) {
				synchronized (LOCK) {
					if (count < MAX) {
						count++;
						System.out.println(Thread.currentThread().getName() + " produced a msg! count:" + count);
						LOCK.notifyAll();
					} else {
						try {
							LOCK.wait();
							System.out.println(Thread.currentThread().getName() + " produce waiting");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	class Consume implements Runnable {
		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() + " consume");
			for (;;) {
				synchronized (LOCK) {
					if (count > 0) {
						count--;
						System.out.println(Thread.currentThread().getName() + " consumed a msg! count:" + count);
						LOCK.notifyAll();
					} else {
						try {
							LOCK.wait();
							System.out.println(Thread.currentThread().getName() + " consume waiting");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		ProduceConsume1 produceConsume1 = new ProduceConsume1();
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(produceConsume1.new Produce());
		executor.execute(produceConsume1.new Produce());
		executor.execute(produceConsume1.new Consume());
		executor.execute(produceConsume1.new Consume());
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("=====end=====");
		System.exit(0);
	}
}
