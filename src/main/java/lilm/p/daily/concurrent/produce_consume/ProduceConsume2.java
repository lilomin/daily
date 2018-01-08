package lilm.p.daily.concurrent.produce_consume;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lilm on 18-1-8.
 */
public class ProduceConsume2 {
	
	private int count = 0;
	private static final int MAX = 10;
	
	private ReentrantLock lock = new ReentrantLock();
	private Condition notFull = lock.newCondition();
	private Condition notEmpty = lock.newCondition();
	
	class Produce implements Runnable {
		@Override
		public void run() {
			try {
				lock.lock();
				if (count > MAX) {
					notEmpty.signal();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
			
		}
	}
	
	class Consumes implements Runnable {
		@Override
		public void run() {
		
		}
	}
	
}
