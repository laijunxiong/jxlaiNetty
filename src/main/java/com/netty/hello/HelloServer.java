package com.netty.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.util.logging.resources.logging;

public class HelloServer {
	
	private static Logger logger = LoggerFactory.getLogger(HelloServer.class);
	
	public void start(int port){
		NioEventLoopGroup bossLoop = new NioEventLoopGroup();
		NioEventLoopGroup workerLoop = new NioEventLoopGroup();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossLoop, workerLoop).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel arg0) throws Exception {
					arg0.pipeline().addLast(new HelloServerInHandler());
				}
			}).option(ChannelOption.SO_BACKLOG, 128).option(ChannelOption.SO_KEEPALIVE, true);
			
			logger.info("server start 1");
			
			ChannelFuture future = bootstrap.bind(port).sync();
			logger.info("server start 2");
			future.channel().closeFuture().sync();
			logger.info("server start 3");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			bossLoop.shutdownGracefully();
			workerLoop.shutdownGracefully();
		}
		
	}

	public static void main(String[] args) {
		HelloServer server = new HelloServer();
		server.start(8080);
		
	}
	
}
