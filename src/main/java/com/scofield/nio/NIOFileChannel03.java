package com.scofield.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel03 {
    public static void main(String[] args) throws Exception{
        FileInputStream fileInputStream = new FileInputStream("1.txt");
        FileChannel channel = fileInputStream.getChannel();
        FileOutputStream fileOutputStream = new FileOutputStream("2.txt");
        FileChannel channel1 = fileOutputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        while (true) {
            //重要的操作，复位，不然会陷入死循环
            byteBuffer.clear();
            int read = channel.read(byteBuffer);
            if (read == -1) {
                break;
            }
            byteBuffer.flip();
            channel1.write(byteBuffer);
        }
        fileOutputStream.close();
        fileInputStream.close();
    }
}
