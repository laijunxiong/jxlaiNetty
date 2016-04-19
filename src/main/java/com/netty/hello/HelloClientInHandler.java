
package com.netty.hello;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class HelloClientInHandler extends ChannelInboundHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(HelloClientInHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf bb = (ByteBuf) msg;
		byte[] result = new byte[bb.readableBytes()];
		bb.readBytes(result);
		logger.info("the server msg is {}",new String(result));
		
		bb.release();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("client connect success and send msg");
		String msg = "are you ok";
		ByteBuf encode = ctx.alloc().buffer(msg.getBytes().length*4);
		encode.writeBytes(msg.getBytes());
		ctx.write(encode);
		ctx.flush();
	}
	
}
