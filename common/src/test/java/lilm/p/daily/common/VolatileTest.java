package lilm.p.daily.common;

import org.junit.Test;

/**
 * Created by lilm on 18-1-8.
 */
public class VolatileTest {
	
	private volatile boolean isExist = false;
	
	/**
	 * 使用volatile申明isExist将使线程始终从主内存区读取变量</br>
	 * 因此很有可能 “isExist == !isExist” 等式线程读取第一个isExist时为false，读取第二个isExists时已经被swap线程修改了值</br>
	 * 而不使用volatile申明，则 “isExist == !isExist” 很难成立，线程加载时会将isExists变量load到线程工作区，在store回主内存区</br>
	 */
	private void tryExist() {
		if (isExist == !isExist) {
			System.exit(0);
		}
	}
	
	private void swapExist() {
		isExist = !isExist;
	}
	
	@Test
	public void test() throws InterruptedException {
		final VolatileTest volatileTest = new VolatileTest();
		Thread mainThread = new Thread(() -> {
			System.out.println("mainThread start");
			while (true) {
				volatileTest.tryExist(); // 不停尝试退出
			}
		});
		mainThread.start();
		Thread swapThread = new Thread(() -> {
			System.out.println("swapThread start");
			while (true) {
				volatileTest.swapExist(); // 不停修改isExist值
			}
		});
		swapThread.start();
		
		
		Thread.sleep(20000);
	}
	
}
