package lilm.p.daily.common.socket.aio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lilm on 18-2-4.
 */
public class TimeClient {
	
	private static final Logger logger = LoggerFactory.getLogger(TimeClient.class);
	
	public static void main(String[] args) {
		int port = 8000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				// 采用默认
			}
		}
		new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AsyncTimeClientHandler").start();
	}
	
	static class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {
		
		private AsynchronousSocketChannel client;
		private String host;
		private int port;
		private CountDownLatch latch;
		
		public AsyncTimeClientHandler(String host, int port) {
			this.host = host;
			this.port = port;
			
			try {
				client = AsynchronousSocketChannel.open();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			latch = new CountDownLatch(1);
			client.connect(new InetSocketAddress(host, port), this, this);
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void completed(Void result, AsyncTimeClientHandler attachment) {
			byte[] req = "QUERY TIME ORDER".getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
			writeBuffer.put(req);
			writeBuffer.flip();
			client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer buffer) {
					if (buffer.hasRemaining()) {
						client.write(buffer, buffer, this);
					} else {
						ByteBuffer readBuffer = ByteBuffer.allocate(1024);
						client.read(
								readBuffer,
								readBuffer,
								new CompletionHandler<Integer, ByteBuffer>() {
									@Override
									public void completed(Integer result, ByteBuffer buffer) {
										buffer.flip();
										byte[] bytes = new byte[buffer.remaining()];
										buffer.get(bytes);
										String body;
										
										try {
											body = new String(bytes, "UTF-8");
											logger.info("Now is {}", body);
											latch.countDown();
										} catch (UnsupportedEncodingException e) {
											e.printStackTrace();
										}
									}
									
									@Override
									public void failed(Throwable exc, ByteBuffer buffer) {
										try {
											client.close();
											latch.countDown();
										} catch (IOException e) {
											logger.error("error:", e);
										}
									}
								}
						);
					}
					
				}
				
				@Override
				public void failed(Throwable exc, ByteBuffer buffer) {
					try {
						client.close();
						latch.countDown();
					} catch (IOException e) {
						logger.error("error:", e);
					}
				}
			});
		}
		
		@Override
		public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
			try {
				client.close();
				latch.countDown();
			} catch (IOException e) {
				logger.error("error:", e);
			}
		}
	}
}
