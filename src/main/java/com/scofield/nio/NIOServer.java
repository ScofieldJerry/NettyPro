package com.scofield.nio;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        Selector selector = Selector.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //循环等待客户端链接
        while (true){
            if (selector.select(1000) == 0) {
                System.out.println("服务器等待一秒， 无连接");
                continue;
            }
            //返回关注事件的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey next = iterator.next();
                if (next.isAcceptable()) {
                    SocketChannel accept = serverSocketChannel.accept();
                    System.out.println("客户端连接成功");
                    accept.configureBlocking(false);
                    accept.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if (next.isReadable()) {
                    SocketChannel channel = (SocketChannel) next.channel();
                    ByteBuffer buffer = (ByteBuffer) next.attachment();
                    channel.read(buffer);
                    System.out.println("from 客户端= " + new String(buffer.array()));
                }
                //手动从集合中删除当前的SelectionKey，防止重复操作
                iterator.remove();
            }

        }
    }
}
