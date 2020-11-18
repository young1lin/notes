package tk.young1lin.rcp.consumer;

import tk.young1lin.rpc.service.SayHelloService;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 8:06 上午
 */
public class SocketChannelConsumer {
    public static void main(String[] args) throws NoSuchMethodException, IOException {
        new SocketChannelConsumer().start();
    }

    private void start() throws IOException, NoSuchMethodException {
        // 接口名称
        String interfaceName = SayHelloService.class.getName();
        // 需要远程执行的方法
        Method method = SayHelloService.class.getMethod("sayHello", java.lang.String.class);
        // 需要传递到远端的参数
        Object[] arguments = {"hello"};

        SocketChannel socketChannel = SocketChannel.open();
        // 设置客户端阻塞式模式
        socketChannel.configureBlocking(true);

        Selector selector = Selector.open();

        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 1234);

        socketChannel.connect(socketAddress);

        while (true) {
            // 监听客户端 Channel 的事件，这里会一直等待，直到有监听的事件到达。
            // 对于客户端，首先监听到的应该是 SelectionKey.OP_CONNECT 事件
            // 然后在后续代码中才会将 SelectionKey.OP_READ 和 WRITE 事件注册到 Selector中
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                // 监听到客户端 Channel 的 SelectionKey.OP_CONNECT 事件，并且处理该事件
                if (key.isConnectable()) {
                    connect(key, selector);
                }
                // 如果客户端连接中有可读取的数据，则处理该事件
                if (key.isReadable()) {
                    read(key);
                }
                // 如果可往客户端连接中写入数据，则处理该事件
                if (key.isValid() && key.isWritable()) {
                    write(key, selector);
                }
            }
        }
    }

    private void connect(SelectionKey key, Selector selector) throws IOException {
        // 由于是客户端Channel，因而可以直接强转为SocketChannel对象
        SocketChannel channel = (SocketChannel) key.channel();
        channel.finishConnect();
        // 连接建立完成后就监听该Channel的WRITE事件，以供客户端写入数据发送到服务器
        channel.register(selector, SelectionKey.OP_WRITE);
    }

    private void write(SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        String message = "message from client";
        System.out.println("**********client: write message**********");
        System.out.println(message);
        // 客户端写入数据到服务器Channel中
        channel.write(ByteBuffer.wrap(message.getBytes()));
        // 数据写入完成后，客户端Channel监听OP_READ事件，以等待服务器发送数据过来
        channel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        System.out.println("**********client: read message**********");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.wrap(new byte[1024]);
        // 接收到客户端Channel的SelectionKey.OP_READ事件，说明服务器发送数据过来了，
        // 此时可以从Channel中读取数据，并且进行相应的处理
        int len = channel.read(buffer);
        if (len == -1) {
            channel.close();
            return;
        }

        System.out.println(new String(buffer.array(), 0, len));
    }
}
