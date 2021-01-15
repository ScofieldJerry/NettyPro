package com.scofield.netty.inboundhandlerandoutboundhandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyClientHandler extends SimpleChannelInboundHandler<Long> {
    @Override protected void channelRead0(ChannelHandlerContext channelHandlerContext, Long aLong) throws Exception {

    }

    @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("发送数据");
        ctx.writeAndFlush(123456L);
    }
}
