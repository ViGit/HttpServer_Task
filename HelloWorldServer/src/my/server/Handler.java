package my.server;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

public class Handler extends ChannelInboundHandlerAdapter {

	private static final int DELAY = 10;
	private String uri;
	private String ip;
	private int sentBytes;
	private int recievedBytes;
	private double speed;

	Handler(String ip) {
		this.ip = ip;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		speed = System.currentTimeMillis();
		ServerInitializer.stats.setTotalRequests();
		ServerInitializer.stats.incrOpenCon();
		Stats.getInstance().setFirstIP(ip);
		ServerInitializer.stats.registerRequestFromIp(ip, new Date());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		recievedBytes += msg.toString().length();

		if (!(msg instanceof HttpRequest))
			return;

		uri = ((HttpRequest) msg).getUri();
		FullHttpResponse response = serverResponse((uri));
		if (uri.contains("%3C")) {
			this.uri = uri.substring(17, uri.length() - 3);
		}
		ServerInitializer.stats.setCountUniqueConnection(uri);

		if (response != null) {
			this.sentBytes = response.content().writerIndex();
			ctx.write(response).addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		speed = (System.currentTimeMillis() - speed) / 1000;
		speed = (recievedBytes + sentBytes) / speed;
		speed = new BigDecimal(speed).setScale(2, RoundingMode.UP)
				.doubleValue();
		IP cip = new IP(ip, uri, sentBytes, recievedBytes, speed);
		ServerInitializer.stats.addConnection(cip);
		ServerInitializer.stats.decrOpenCon();
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	public FullHttpResponse serverResponse(String value)
			throws InterruptedException {
		FullHttpResponse response;
		String url = "";
		if (value.contains("%3C")) {
			url = value.substring(17, value.length() - 3);
			value = value.substring(0, 9);
			Stats.getInstance().registerURL(url);
		}

		switch (value) {
		case "/hello":
			String hello = "<head><font size=\"10\">HELLO WORLD!</font></head>";
			response = new DefaultFullHttpResponse(HTTP_1_1, OK,
					Unpooled.copiedBuffer(hello, CharsetUtil.US_ASCII));
			Thread.sleep(DELAY * 1000);
			return response;
		case "/status":
			response = new DefaultFullHttpResponse(HTTP_1_1, OK,
					Unpooled.copiedBuffer(ServerInitializer.stats.getReport(),
							CharsetUtil.US_ASCII));
			return response;
		case "/redirect": // for example /redirect?url=<http://google.com>
			response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
			response.headers().set(HttpHeaders.Names.LOCATION, url);
			return response;
		default:
			String notFound = "<head><tr>PAGE NOT FOUND</tr></head>";
			response = new DefaultFullHttpResponse(HTTP_1_1, OK,
					Unpooled.copiedBuffer(notFound, CharsetUtil.US_ASCII));
			return response;
		}
	}
}
