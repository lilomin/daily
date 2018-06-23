package lilm.p.daily.common.socket.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

/**
 * Created by lilm on 18-2-19.
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
	private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
	
	private String url;
	
	public HttpFileServerHandler(String url) {
		this.url = url;
	}
	
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		if (!request.getDecoderResult().isSuccess()) {
			sendError(ctx, HttpResponseStatus.BAD_REQUEST);
			return;
		}
		if (request.getMethod() != HttpMethod.GET) {
			sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
			return;
		}
		final String uri = request.getUri();
		final String path = sanitizeUri(uri);
		if (path == null) {
			sendError(ctx, HttpResponseStatus.FORBIDDEN);
			return;
		}
		
		File file = new File(path);
		if (file.isHidden() || !file.exists()) {
			sendError(ctx, HttpResponseStatus.NOT_FOUND);
			return;
		}
		if (file.isDirectory()) {
			if (uri.startsWith("/")) {
				sendListing(ctx, file);
			} else {
				sendRedirect(ctx, uri + '/');
			}
			return;
		}
		if (!file.isFile()) {
			sendError(ctx, HttpResponseStatus.FORBIDDEN);
			return;
		}
		RandomAccessFile randomAccessFile = null;
		
		try {
			randomAccessFile = new RandomAccessFile(file, "r");
		} catch (FileNotFoundException e) {
			sendError(ctx, HttpResponseStatus.NOT_FOUND);
			return;
		}
		long fileLength = randomAccessFile.length();
		HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		// setContentLength(response, fileLength);
		setContentTypeHeader(response, file);
		ctx.write(response);
		ChannelFuture sendFileFuture = ctx.writeAndFlush(new ChunkedFile(randomAccessFile, 0, fileLength, 8192),
				ctx.newProgressivePromise());
		sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
			@Override
			public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
				if (total < 0) {
					System.out.println("Transfer Progress: " + progress);
				} else {
					System.out.println("Transfer Progress: " + progress + "/" +total);
				}
			}
			
			@Override
			public void operationComplete(ChannelProgressiveFuture future) throws Exception {
				System.out.println("Transfer Complete.");
			}
		});
		sendFileFuture.addListener(ChannelFutureListener.CLOSE);
		
		// ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		// lastContentFuture.addListener(ChannelFutureListener.CLOSE);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		if (ctx.channel().isActive()) {
			sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String sanitizeUri(String uri) {
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// 忽略这个异常
		}
		if (!uri.startsWith(url)) {
			return null;
		}
		uri = uri.replace('/', File.separatorChar);
		if (uri.contains(File.separator + '.')
				|| uri.contains('.' + File.separator) || uri.startsWith(".")
				|| uri.endsWith(".") || INSECURE_URI.matcher(uri).matches()) {
			return null;
		}
		return System.getProperty("user.dir") + uri;
	}
	
	private void sendListing(ChannelHandlerContext ctx, File dir) {
		if (dir == null) {
			return;
		}
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set("Content-Type", "text/html;charset=UTF-8");
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>\r\n");
		sb.append("<html><head><title>");
		sb.append(dir.getPath());
		sb.append(" 目录: ");
		sb.append("</title></head><body>\r\n");
		sb.append("<h3>");
		sb.append(dir.getPath()).append(" 目录: ");
		sb.append("</h3>\r\n");
		sb.append("<ul>");
		sb.append("<li>链接: <a href=\"../\">..</a></li>\r\n");
		for (File file : files) {
			if (file.isHidden() || !file.canRead()) {
				continue;
			}
			String name = file.getName();
			if (file.isFile()) {
				sb.append("<li>链接: <a href=\"").append(name).append("\">").append(name).append("</a></li>\r\n");
			} else {
				sb.append("<li>链接: <a href=\"").append(name).append("/\">").append(name).append("</a></li>\r\n");
			}
		}
		sb.append("</ul></body></html>\r\n");
		ByteBuf byteBuf = Unpooled.copiedBuffer(sb, CharsetUtil.UTF_8);
		response.content().writeBytes(byteBuf);
		byteBuf.release();
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	private void sendRedirect(ChannelHandlerContext ctx, String newUri) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
		response.headers().set("Location", newUri);
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
				Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
		response.headers().set("Content-Type", "text/plain; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	private void setContentTypeHeader(HttpResponse response, File file) {
		MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
		response.headers().set("Content-Type", mimetypesFileTypeMap.getContentType(file.getPath()));
	}
	
	// private void setContentLength(HttpResponse response, Object length) {
	// 	response.headers().set("Content-Length", response);
	// }
	
}
