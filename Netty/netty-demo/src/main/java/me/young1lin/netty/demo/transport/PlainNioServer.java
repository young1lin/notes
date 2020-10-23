package me.young1lin.netty.demo.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 使用 NIO 创建非阻塞的客户端，很复杂
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/10/20 12:57 上午
 */
public class PlainNioServer implements Server {

    @Override
    public void server(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket socket = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        socket.bind(address);
        // 打开 Selector 来处理 Channel
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        final ByteBuffer msg = ByteBuffer.wrap("Hi\r\n".getBytes());
        for (; ; ) {
            try {
                // 等待需要处理的新事件；阻塞将一直持续到下一个传入事件
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            // 获取所有接收事件的 SelectionKey
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    // 检查事件是否是一个新的已经就绪可以被接受的连接
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        // 接受客户端，并将他注册到选择器
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                        System.out.println("Accepted connection from" + client);
                        if (key.isWritable()) {
                            SocketChannel clientWrite = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();
                            while (buffer.hasRemaining()) {
                                if (client.write(buffer) == 0) {
                                    break;
                                }
                            }
                            clientWrite.close();
                        }
                        // 这是我自己加的，书上有误
                        client.close();
                    }
                } catch (IOException ex) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                        // 忽略
                    }
                    ex.printStackTrace();
                }
            }
        }
    }
}
