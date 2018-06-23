package lilm.p.daily.common.concurrent.produce_consume;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lilm on 18-1-8.
 */
public class ProduceConsume3 {
	
	private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(MAX);
	
	private static final int MAX = 10;
	
	class Produce implements Runnable {
		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() + " produce");
			for (;;) {
				try {
					Thread.sleep(1000);
					queue.put("a");
					System.out.println(Thread.currentThread().getName() + " produced a msg! count:" + queue.size());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class Consume implements Runnable {
		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() + " consume");
			for (;;) {
				try {
					queue.take();
					Thread.sleep(2000);
					System.out.println(Thread.currentThread().getName() + " consumed a msg! count:" + queue.size());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		ProduceConsume3 produceConsume3 = new ProduceConsume3();
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(produceConsume3.new Produce());
		executor.execute(produceConsume3.new Produce());
		executor.execute(produceConsume3.new Consume());
		executor.execute(produceConsume3.new Consume());
		
		// try {
		// 	Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// 	e.printStackTrace();
		// }
		// System.out.println("=====end=====");
		// System.exit(0);
	}
	
}
