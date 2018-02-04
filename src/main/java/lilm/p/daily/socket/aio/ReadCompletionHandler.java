package lilm.p.daily.socket.aio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by lilm on 18-2-4.
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private AsynchronousSocketChannel channel;
	
	public ReadCompletionHandler(AsynchronousSocketChannel channel) {
		if (this.channel == null) {
			this.channel = channel;
		}
	}
	
	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		attachment.flip();
		byte[] body = new byte[attachment.remaining()];
		attachment.get(body);
		
		try {
			String req = new String(body, "UTF-8");
			logger.info("The time server receive order : {}", req);
			String currentTime = String.valueOf(System.currentTimeMillis());
			doWrite(currentTime);
		} catch (UnsupportedEncodingException e) {
			logger.error("error:", e);
		}
	}
	
	private void doWrite(String currentTime) {
		if (currentTime == null || currentTime.trim().length() <= 0) {
			return;
		}
		byte[] bytes = currentTime.getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
		writeBuffer.put(bytes);
		writeBuffer.flip();
		
		channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				if (attachment.hasRemaining())
					channel.write(attachment, attachment, this);
			}
			
			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				try {
					channel.close();
				} catch (IOException e) {
					logger.error("error:", e);
				}
			}
		});
	}
	
	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			this.channel.close();
		} catch (IOException e) {
			logger.error("error:", e);
		}
	}
}
