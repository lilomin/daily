package lilm.p.daily.common.socket.aio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by lilm on 18-2-4.
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, TimeServer.AsyncTimeServerHandler> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void completed(AsynchronousSocketChannel result, TimeServer.AsyncTimeServerHandler attachment) {
		attachment.serverSocketChannel.accept(attachment, this);
		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		result.read(buffer, buffer, new ReadCompletionHandler(result));
	}
	
	@Override
	public void failed(Throwable exc, TimeServer.AsyncTimeServerHandler attachment) {
		logger.error("Server accept error!", exc);
		attachment.latch.countDown();
	}
}
