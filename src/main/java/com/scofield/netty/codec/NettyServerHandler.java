package com.scofield.netty.codec;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

//我们自定义一个Handler需要继承netty规定好的某个HandlerAdapter（规范）
//这是我们自定义一个Handler才能成为一个Handler
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    //读取数据的事件（这里我们可以读取客户端发送的消息）
    //ChannelHandlerContext：上下文对象，含有pipeline， channel，地址
    //Object msg:就是客户端发送的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        StudentPOJO.Student student = (StudentPOJO.Student) msg;
        System.out.println("学生id" + student.getId() + "学生姓名" + student.getName());
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //writeAndFlush是write并且flush，将数据写入缓存并刷新
        //一般讲，我们要对发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端", CharsetUtil.UTF_8));
    }

    //处理异常，一般需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
