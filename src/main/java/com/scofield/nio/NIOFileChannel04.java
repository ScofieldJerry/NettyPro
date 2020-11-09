package com.scofield.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class NIOFileChannel04 {
    public static void main(String[] args) throws Exception{
        FileInputStream fileInputStream = new FileInputStream("D:\\01.txt");
        FileChannel channel = fileInputStream.getChannel();
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\02.txt");
        FileChannel channel1 = fileOutputStream.getChannel();
        channel1.transferFrom(channel, 0, channel.size());
        fileOutputStream.close();
        fileInputStream.close();
    }
}
