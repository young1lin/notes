package me.young1lin.netty.demo.transport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/21 3:44 下午
 */
public class TransportTest {

    public static void main(String[] args) throws IOException {
//        Server server = new NettyNioServer();
//        server.server(7777);
//        Server server = new NettyNioServer();
//        server.server(7777);
//        Server server = new PlainNioServer();
//        server.server(7777);
//        Charset utf8 = StandardCharsets.UTF_8;
//        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
//        ByteBuf sliced = buf.slice(0,15);
//        System.out.println(sliced.toString(utf8));
//        buf.setByte(0,(byte)'J');
//        assert buf.getByte(0) == sliced.getByte(0);
//        System.out.println(buf.toString(utf8));

//        Charset utf8 =  StandardCharsets.UTF_8;
//        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!",utf8);
//        ByteBuf copy = buf.copy();
//        System.out.println(copy.toString(utf8));
//        buf.setByte(0,(byte)'J');
//        System.out.println(buf.toString(utf8));
//        assert buf.getByte(0) != copy.getByte(0);
//        System.out.println(copy.toString(utf8));

//        Charset utf8 = StandardCharsets.UTF_8;
//        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
//        System.out.println((char)buf.getByte(0));
//        int readerIndex = buf.readerIndex();
//        int writerIndex = buf.writerIndex();
//        buf.setByte(0,(byte)'B');
//        System.out.println((char)buf.getByte(0));
//        assert readerIndex == buf.readerIndex();
//        assert writerIndex == buf.writerIndex();

        Charset utf8 = StandardCharsets.UTF_8;
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        System.out.println((char)buf.readByte());
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        buf.writeByte((byte)'?');
        assert readerIndex == buf.readerIndex();
        assert writerIndex == buf.writerIndex();
    }
}
