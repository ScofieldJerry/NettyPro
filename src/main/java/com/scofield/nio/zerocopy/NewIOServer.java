package com.scofield.nio.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NewIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket socket = serverSocketChannel.socket();
        socket.bind(new InetSocketAddress(7001));
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        while (true) {
            SocketChannel accept = serverSocketChannel.accept();
            int count = -1;
            try {
                while (count != -1) {
                    count = accept.read(byteBuffer);
                }
            } catch (Exception e){
                //e.printStackTrace();
                break;
            }
            //将buffer倒带，即position置为0，mark置为-1，以供下次还能使用
            byteBuffer.rewind();
        }
    }
}
