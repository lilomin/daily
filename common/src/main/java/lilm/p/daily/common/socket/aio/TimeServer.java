package lilm.p.daily.common.socket.aio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lilm on 18-2-4.
 */
public class TimeServer {
	
	private static final Logger logger = LoggerFactory.getLogger(AsyncTimeServerHandler.class);
	
	public static void main(String[] args) {
		int port = 8000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				// 采用默认
			}
		}
		
		AsyncTimeServerHandler handler = new AsyncTimeServerHandler(port);
		new Thread(handler, "AsyncServerHandler").start();
	}
	
	public static class AsyncTimeServerHandler implements Runnable {
		
		private int port;
		
		CountDownLatch latch;
		AsynchronousServerSocketChannel serverSocketChannel;
		
		AsyncTimeServerHandler(int port) {
			this.port = port;
			
			try {
				serverSocketChannel = AsynchronousServerSocketChannel.open();
				serverSocketChannel.bind(new InetSocketAddress(port));
				logger.info("The server is start at port:{}", port);
			} catch (IOException e) {
				logger.error("Init server error!", e);
			}
		}
		
		@Override
		public void run() {
			latch = new CountDownLatch(1);
			doAccept();
			try {
				latch.await();
			} catch (InterruptedException e) {
				logger.error("Server running error!", e);
			}
		}
		
		private void doAccept() {
			serverSocketChannel.accept(this, new AcceptCompletionHandler());
		}
	}
	
}
