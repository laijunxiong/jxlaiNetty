package com.netty.myhttp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServer {
	
	private static Logger logger = LoggerFactory.getLogger(HttpServer.class);
	
	public void start(int port){
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel paramC) throws Exception {
					paramC.pipeline().addLast(new HttpResponseEncoder());
					paramC.pipeline().addLast(new HttpRequestDecoder());
					
				}
			}).option(ChannelOption.SO_BACKLOG, 126).option(ChannelOption.SO_KEEPALIVE, true);
			
			
			ChannelFuture future = bootstrap.bind(port).sync();

			future.channel().closeFuture().sync();
			
		} catch (Exception e) {
			logger.error("",e);
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		HttpServer server = new HttpServer();
		server.start(8080);
	}

}
