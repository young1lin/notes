//package me.young1lin.netty.demo.test;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.embedded.EmbeddedChannel;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * @author young1lin
// * @version 1.0
// * @date 2020/10/28 9:27 下午
// */
//public class FixedLengthFrameDecoderTest {
//
//    @Test
//    public void testFramesDecoded() {
//        ByteBuf buf = Unpooled.buffer();
//        for (int i = 0; i < 9; i++) {
//            buf.writeByte(i);
//        }
//        ByteBuf input = buf.duplicate();
//        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
//        assertTrue(channel.writeInbound(input.retain()));
//        assertTrue(channel.finish());
//
//        ByteBuf read = channel.readInbound();
//        assertEquals(buf.readSlice(3),read);
//        read.release();
//
//        read = channel.readInbound();
//        assertEquals(buf.readSlice(3),read);
//        read.release();
//
//        read = channel.readInbound();
//        assertEquals(buf.readSlice(3),read);
//        read.release();
//
//        // 这里会出错
//        assertNull(channel.readInbound());
//        buf.release();
//    }
//
//    @Test
//    public void testFramesDecoded2(){
//        ByteBuf buf = Unpooled.buffer();
//        for (int i = 0; i < 9; i++) {
//            buf.writeByte(i);
//        }
//        ByteBuf input = buf.duplicate();
//
//        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
//        assertFalse(channel.writeInbound(input.readBytes(2)));
//        assertTrue(channel.writeInbound(input.readBytes(7)));
//
//        assertTrue(channel.finish());
//        ByteBuf read = channel.readInbound();
//        assertEquals(buf.readSlice(3),read);
//        read.release();
//
//        read = channel.readInbound();
//        assertEquals(buf.readSlice(3),read);
//        read.release();
//
//        // 同样这里会报错，期待是 null，实际是还有值的
//        assertNull(channel.readInbound());
//        buf.release();
//    }
//
//}
