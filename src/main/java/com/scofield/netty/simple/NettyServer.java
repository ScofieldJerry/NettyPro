package com.scofield.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        //创建两个线程组bossGroup和workerGroup
        //bossGroup只是处理连接请求，真正和客户端业务处理的会交给workerGroup完成
        //两个都是无限循环
        //bossGroup 和 workerGroup含有的子线程（NioEventLoop）的个数默认是CPU核数 * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务器的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)//设置两个线程组
                    .channel(NioServerSocketChannel.class)//使用NioSocketChannel作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG, 128)//设置线程队列链接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置保持活动链接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {//创建一个通道测试对象
                        //给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            //绑定一个端口并且同步，生成一个ChannelFuture对象
            //启动服务器
            ChannelFuture cf = bootstrap.bind(6668).sync();
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (cf.isSuccess()) {
                        System.out.println("监听端口6668成功");
                    } else {
                        System.out.println("监听6668失败");
                    }
                }
            });
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
