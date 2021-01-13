package com.scofield.netty.codec2;

import com.scofield.netty.codec.StudentPOJO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

//我们自定义一个Handler需要继承netty规定好的某个HandlerAdapter（规范）
//这是我们自定义一个Handler才能成为一个Handler
public class NettyServerHandler extends SimpleChannelInboundHandler<MyDateInfo.MyMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDateInfo.MyMessage msg) throws Exception {
        MyDateInfo.MyMessage.DateType dateType = msg.getDateType();
        if (dateType == MyDateInfo.MyMessage.DateType.StudentType) {
            System.out.println(msg.getStudent().getId() + "姓名" + msg.getStudent().getName());
        } else if (dateType == MyDateInfo.MyMessage.DateType.WorkerType) {
            System.out.println(msg.getWorker().getId() + "姓名" + msg.getWorker().getName());
        } else {
            System.out.println("数据有误");
        }
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
