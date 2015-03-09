package my.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
	public static Stats stats = Stats.getInstance();

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		String ip = sc.remoteAddress().getHostString();
		ChannelPipeline pipeline = sc.pipeline();
		pipeline.addLast(new HttpRequestDecoder());
		pipeline.addLast(new HttpResponseEncoder());
		pipeline.addLast(new Handler(ip));
	}
}