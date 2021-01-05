package com.scofield.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MyServer {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            /**
                             * 加入一个netty提供的IdleStateHandler
                             * 是netty处理空闲状态的处理器
                             * long readerIdleTime  表示多长时间没有读，就会发送一个心跳检测包检测是否连接
                             * long writerIdleTime  表示多长时间没有写，就会发送一个心跳检测包检测是否连接
                             * long allIdleTime  表示多长时间既没有读也没有写，就会发送一个心跳检测包检测是否连接
                             * 当IdleStateHandler触发以后，就会传给管道的下一个handler去处理，通过调用下一个handler的userEventTrigger，
                             * 在该方法中去处理IdleStateHandler（读空闲，写空闲，读写空闲）
                             */
                            pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            //加入一个对空闲检测进一步处理的handler
                            pipeline.addLast(new MyServerHandler());
                        }
                    });
            ChannelFuture sync = bootstrap.bind(7000).sync();
            sync.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
