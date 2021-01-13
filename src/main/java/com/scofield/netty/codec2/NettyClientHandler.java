package com.scofield.netty.codec2;

import com.scofield.netty.codec.StudentPOJO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Random;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    //通道就绪时，就会触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int i = new Random().nextInt(3);
        MyDateInfo.MyMessage myMessage = null;
        if (i == 0) {
            myMessage = MyDateInfo.MyMessage.newBuilder().setDateType(MyDateInfo.MyMessage.DateType.StudentType).setStudent(MyDateInfo.Student.newBuilder().setId(1).setName("鲁智深").build()).build();
        } else {
            myMessage = MyDateInfo.MyMessage.newBuilder().setDateType(MyDateInfo.MyMessage.DateType.WorkerType).setWorker(MyDateInfo.Worker.newBuilder().setId(456).setName("隔壁老王").build()).build();
        }
        ctx.writeAndFlush(myMessage);
    }

    //当通道有读取事件时会触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("服务器回复的消息"+byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址："+ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
