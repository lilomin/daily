package lilm.p.daily.concurrent.produce_consume;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lilm on 18-1-8.
 */
public class ProduceConsume2 {
	
	private int count = 0;
	private static final int MAX = 10;
	
	private Lock lock = new ReentrantLock();
	private Condition notFull = lock.newCondition();
	private Condition notEmpty = lock.newCondition();
	
	class Produce implements Runnable {
		@Override
		public void run() {
			lock.lock();
			try {
				while (true) {
					while (count == MAX) {
						System.out.println(Thread.currentThread().getName() + " produce waiting");
						notFull.await(); // 阻塞生产者
					}
					count++;
					System.out.println(Thread.currentThread().getName() + " produced a msg! count:" + count);
					notEmpty.signalAll(); // 唤醒消费者
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
	}
	
	class Consume implements Runnable {
		@Override
		public void run() {
			lock.lock();
			try {
				while (true) {
					while (count == 0) {
						System.out.println(Thread.currentThread().getName() + " consume waiting");
						notEmpty.await(); // 阻塞消费者
					}
					count--;
					System.out.println(Thread.currentThread().getName() + " consumed a msg! count:" + count);
					notFull.signalAll(); // 唤醒生产者
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
	}
	
	public static void main(String[] args) {
		ProduceConsume2 produceConsume2 = new ProduceConsume2();
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(produceConsume2.new Produce());
		executor.execute(produceConsume2.new Produce());
		executor.execute(produceConsume2.new Consume());
		executor.execute(produceConsume2.new Consume());
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("=====end=====");
		System.exit(0);
	}
	
}
