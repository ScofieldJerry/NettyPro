package com.scofield.nio.groupchat;




import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class GroupChatClient {
    private final String HOST = "127.0.0.1";
    private final int PORT = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;
    public GroupChatClient () throws IOException {
        selector = Selector.open();
        socketChannel = socketChannel.open(new InetSocketAddress(HOST, PORT));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        userName = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println( userName+"准备好");
    }
    public void sendInfo(String msg){
        msg = userName + "说" +msg;
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void read(){
        try {
            int select = selector.select();
            if (select > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey next = iterator.next();
                    if (next.isReadable()) {
                        SocketChannel channel = (SocketChannel) next.channel();
                        ByteBuffer allocate = ByteBuffer.allocate(1024);
                        channel.read(allocate);
                        String string = new String(allocate.array());
                        System.out.println(string.trim());
                    }
                }
                iterator.remove();
            } else {
                //System.out.println("没有可以用的通道");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        GroupChatClient groupChatClient = new GroupChatClient();
        new Thread(){
            public void run(){
                while (true){
                    groupChatClient.read();
                    try {
                        Thread.currentThread().sleep(3000);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            groupChatClient.sendInfo(s);
        }
    }
}
