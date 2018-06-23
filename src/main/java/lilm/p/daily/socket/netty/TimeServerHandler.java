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
public class TimeServerHandler extends ChannelHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(TimeServerHandler.class);
	private int count;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String body = (String) msg;
		logger.info("The Time server receive order : " + body + "; count is : " + ++count);
		String currentTime = String.valueOf(System.currentTimeMillis());
		currentTime += System.getProperty("line.separator");
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.writeAndFlush(resp);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
