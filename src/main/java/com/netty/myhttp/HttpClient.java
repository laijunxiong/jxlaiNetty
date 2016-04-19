package com.netty.myhttp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模拟浏览器
 * 
 * @author lizhi
 *
 */
public class HttpClient {

	private static Logger logger = LoggerFactory.getLogger(HttpClient.class);

	public void connect(String host, int port) {
		NioEventLoopGroup group = new NioEventLoopGroup();

		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel paramC)
								throws Exception {
							paramC.pipeline()
									.addLast(new HttpResponseDecoder());
							paramC.pipeline().addLast(new HttpRequestEncoder());
							paramC.pipeline().addLast(new HttpClientInboundHandler());
						}
					}).option(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture future = bootstrap.bind(host, port).sync();

			URI uri = new URI("http://127.0.0.1:8844");
			String msg = "Are you ok?";
			DefaultFullHttpRequest request = new DefaultFullHttpRequest(
					HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString(),
					Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));

			// 构建http请求
			request.headers().set(HttpHeaders.Names.HOST, host);
			request.headers().set(HttpHeaders.Names.CONNECTION,
					HttpHeaders.Values.KEEP_ALIVE);
			request.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
					request.content().readableBytes());
			// 发送http请求
			future.channel().write(request);
			future.channel().flush();
			future.channel().closeFuture().sync();

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		HttpClient client = new HttpClient();
		client.connect("127.0.0.1", 8088);
	}

}
