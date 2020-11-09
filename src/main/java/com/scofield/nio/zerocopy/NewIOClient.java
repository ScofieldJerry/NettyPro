package com.scofield.nio.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NewIOClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 7001));
        String fileName = "测试文件名字";
        FileChannel fileChannel = new FileInputStream(fileName).getChannel();
        long l = System.currentTimeMillis();
        //在linux下，使用transferTo方法就可以完成传输
        //在windows下，transferTo方法每次只可以传输8M文件，就需要分段传输，而且要注意传输时的位置
        //transferTo底层使用了0拷贝
        long l1 = fileChannel.transferTo(0, fileChannel.size(), socketChannel);
        long time = l1 - l;
        fileChannel.close();
    }
}
