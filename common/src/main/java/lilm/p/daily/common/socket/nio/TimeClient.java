package lilm.p.daily.common.socket.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lilm on 18-1-28.
 */
public class TimeClient {
	static final Logger logger = LoggerFactory.getLogger(TimeClient.class);
	
	public static void main(String[] args) {
		int port = 8000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				// 采用默认
			}
		}
		
		new Thread(new TimeClientHandler("127.0.0.1", port)).start();
	}
	
	static class TimeClientHandler implements Runnable {
		
		private String host;
		private int port;
		
		private Selector selector;
		private SocketChannel channel;
		private volatile boolean stop;
		
		public TimeClientHandler(String host, int port) {
			this.host = host;
			this.port = port;
			
			try {
				selector = Selector.open();
				channel = SocketChannel.open();
				channel.configureBlocking(false);
			} catch (IOException e) {
				logger.error("Init TimeClientHandler failed!", e);
			}
		}
		
		@Override
		public void run() {
			try {
				doConnect();
			} catch (IOException e) {
				logger.error("Connecting server error!", e);
			}
			
			while (!stop) {
				try {
					selector.select(1000);
					Set<SelectionKey> selectionKeys = selector.selectedKeys();
					Iterator<SelectionKey> it = selectionKeys.iterator();
					SelectionKey selectionKey = null;
					while (it.hasNext()) {
						selectionKey = it.next();
						it.remove();
						try {
							handleInput(selectionKey);
						} catch (Exception e) {
							if (selectionKey != null) {
								selectionKey.cancel();
								if (selectionKey.channel() != null) {
									selectionKey.channel().close();
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error("Client Error!", e);
					System.exit(1);
				}
			}
			
			if (selector != null) {
				try {
					selector.close();
				} catch (IOException e) {
					logger.error("Client connection close error!", e);
				}
			}
		}
		
		private void handleInput(SelectionKey key) throws IOException {
			if (key.isValid()) {
				SocketChannel sc = (SocketChannel) key.channel();
				if (key.isConnectable()) {
					if (sc.finishConnect()) {
						sc.register(selector, SelectionKey.OP_READ);
						doWrite(sc);
					} else {
						System.exit(1);
					}
				}
				if (key.isReadable()) {
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					int readBytes = sc.read(readBuffer);
					if (readBytes > 0) {
						readBuffer.flip();
						byte[] bytes = new byte[readBuffer.remaining()];
						readBuffer.get(bytes);
						String body = new String(bytes, "UTF-8");
						logger.info("Now is : {}", body);
					} else if (readBytes < 0) {
						key.cancel();
						sc.close();
					} else {
						; // 读到零字节忽略
					}
				}
			}
		}
		
		private void doConnect() throws IOException {
			if (channel.connect(new InetSocketAddress(host, port))) {
				channel.register(selector, SelectionKey.OP_READ);
				doWrite(channel);
			} else {
				channel.register(selector, SelectionKey.OP_CONNECT);
			}
		}
		
		private void doWrite(SocketChannel sc) throws IOException {
			byte[] req = "QUERY TIME ORDER".getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
			writeBuffer.put(req);
			writeBuffer.flip();
			sc.write(writeBuffer);
			if (!writeBuffer.hasRemaining()) {
				logger.info("Send order 2 server succeed!");
			}
		}
	}
	
}
