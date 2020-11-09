package com.scofield.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;
    public GroupChatServer(){
        try {
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void listen(){
        try {
            while (true){
                int count = selector.select(2000);
                if (count > 0){
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey next = iterator.next();
                        if (next.isAcceptable()) {
                            SocketChannel accept = listenChannel.accept();
                            accept.configureBlocking(false);
                            accept.register(selector, SelectionKey.OP_READ);
                            System.out.println(accept.getRemoteAddress()+"上线");
                        }
                        if (next.isReadable()) {
                            readData(next);
                        }
                        iterator.remove();
                    }
                } else {
                    System.out.println("等待客户端连接");
                }
            }
        } catch (Exception e){

        } finally {

        }
    }
    public void readData(SelectionKey selectionKey){
        SocketChannel socketChannel = null;
        try {
            socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int count = socketChannel.read(byteBuffer);
            if (count > 0) {
                String s = new String(byteBuffer.array());
                System.out.println("客户端："+socketChannel.getRemoteAddress()+"说"+ s);
                sendIntoOtherClients(s, socketChannel);
            }
        } catch (Exception e) {
            try {
                System.out.println(socketChannel.getRemoteAddress() + "离线了");
                selectionKey.cancel();
                socketChannel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    public void sendIntoOtherClients(String s, SocketChannel socketChannel) throws IOException {
        System.out.println("转发消息");
        for (SelectionKey key:selector.keys()) {
            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel != socketChannel) {
                SocketChannel dest = (SocketChannel) channel;
                ByteBuffer wrap = ByteBuffer.wrap(s.getBytes());
                dest.write(wrap);
            }
        }
    }
    public static void main(String[] args) {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
