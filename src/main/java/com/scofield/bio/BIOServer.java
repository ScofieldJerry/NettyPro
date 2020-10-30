package com.scofield.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {
    public static void main(String[] args) throws IOException {
        //创建线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        final ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动了");
        while (true){
            //监听，等待客户端链接
            final Socket accept = serverSocket.accept();
            System.out.println("有客户端链接成功");
            //创建线程，与之通信
            executorService.execute(new Runnable() {
                public void run() {
                    handler(accept);
                }
            });
        }
    }
    public static void handler(Socket socket){
        try {
            System.out.println("线程id是"+Thread.currentThread().getId()+"线程名字是"+Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            while (true){
                System.out.println("线程id是"+Thread.currentThread().getId()+"线程名字是"+Thread.currentThread().getName());
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.println(new String(bytes, 0 ,read));
                } else {
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                System.out.println("关闭客户端链接");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
