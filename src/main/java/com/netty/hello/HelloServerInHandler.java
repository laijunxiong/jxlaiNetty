package com.netty.hello;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.util.logging.resources.logging;

import com.sun.org.apache.bcel.internal.generic.NEW;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * server logic
 * @author lizhi
 *
 */
public class HelloServerInHandler extends ChannelInboundHandlerAdapter {
	
	
	private static Logger logging = LoggerFactory.getLogger(HelloServerInHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf bb = (ByteBuf) msg;
		byte[] data = new byte[bb.readableBytes()];
		bb.readBytes(data);
		System.out.println(new String(data));
		logging.info("the msg is from client {}",new String(data));

		String response = "i am ok";
		ByteBuf encode = ctx.alloc().buffer(4 * response.getBytes().length);
		encode.writeBytes(response.getBytes());
		ctx.write(encode);
		ctx.flush();
		
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
}
