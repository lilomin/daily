package lilm.p.daily.socket.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lilm on 18-1-27.
 */
public class MultiTimeService implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(MultiTimeService.class);
	
	private Selector selector;
	private ServerSocketChannel channel;
	private volatile boolean stop;
	
	public MultiTimeService(int port) {
		try {
			selector = Selector.open();
			channel = ServerSocketChannel.open();
			channel.configureBlocking(false);
			channel.socket().bind(new InetSocketAddress(port), 1024);
			channel.register(selector, SelectionKey.OP_ACCEPT);
			logger.info("Time server is start in port:" + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		this.stop = true;
	}
	
	/**
	 * @see Runnable#run()
	 */
	@Override
	public void run() {
		while (!stop) {
			try {
				selector.select(1000);
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				SelectionKey key = null;
				while (it.hasNext()) {
					key = it.next();
					it.remove();
					try {
						handleInput(key);
					} catch (Exception e) {
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			} catch (Throwable e) {
				logger.error("Server handler error!", e);
			}
		}
		
		if (selector != null) {
			try {
				selector.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void handleInput(SelectionKey key) throws IOException {
		if (key.isValid()) {
			if (key.isAcceptable()) {
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}
			if (key.isReadable()) {
				SocketChannel sc = (SocketChannel) key.channel();
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if (readBytes > 0) {
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("The time server receive order:" + body);
					String current = new Date().toString();
					doWrite(sc, current);
				}
			}
		}
	}
	
	private void doWrite(SocketChannel channel, String response) throws IOException {
		if (response != null && response.trim().length() > 0) {
			byte[] bytes = response.getBytes();
			ByteBuffer writerBuffer = ByteBuffer.allocate(bytes.length);
			writerBuffer.put(bytes);
			writerBuffer.flip();
			channel.write(writerBuffer);
		}
	}
	
}
