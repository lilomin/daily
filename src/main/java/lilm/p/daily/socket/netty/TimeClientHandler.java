package lilm.p.daily.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lilm on 18-2-7.
 */
public class TimeClientHandler extends ChannelHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(TimeClientHandler.class);
	private int count;
	private byte[] req;
	
	public TimeClientHandler() {
		req = ("Query Time Order" + System.getProperty("line.separator")).getBytes();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf msg = null;
		for (int i = 0; i < 100; i++) {
			msg = Unpooled.buffer(req.length);
			msg.writeBytes(req);
			ctx.writeAndFlush(msg);
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String body = (String) msg;
		System.out.println("Now is : " + body + "; count is : " + ++count);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.warn("Unexpected exception from downstream :", cause);
		ctx.close();
	}
}
