package com.scofield.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

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
                            //基于http协议，使用http编码解码器
                            pipeline.addLast(new HttpServerCodec())
                                    //因为是以块的方式写，添加ChunkedWriteHandler处理器
                                    .addLast(new ChunkedWriteHandler())
                                    /**
                                     * 1.因为http传输过程中是分段的，HttpObjectAggregator
                                     * 就是可以将多个段聚合
                                     * 这就是为什么，当浏览器发送大量数据时，就会发出多次http请求
                                     */
                                    .addLast(new HttpObjectAggregator(8192))
                                    /**
                                     * 1.对应websocket，他的数据是以帧（frame）的形式传递
                                     * 2.可以看到WebSocketFrame下面有六个子类
                                     * 3.浏览器发送请求时，ws：//localhost:7000/xxx 表示请求的uri
                                     * 4.WebSocketServerProtocolHandler核心功能将http协议升级为ws协议，保持长连接
                                     */
                                    .addLast(new WebSocketServerProtocolHandler("/hello"))
                                    //自定义Handler，处理业务逻辑
                                    .addLast(new MyTextWebSocketFrameHandler());
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
