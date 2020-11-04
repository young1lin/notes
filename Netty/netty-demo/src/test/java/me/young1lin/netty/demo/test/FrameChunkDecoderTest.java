//package me.young1lin.netty.demo.test;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.embedded.EmbeddedChannel;
//import io.netty.handler.codec.TooLongFrameException;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * @author young1lin
// * @version 1.0
// * @date 2020/10/28 10:24 下午
// */
//public class FrameChunkDecoderTest {
//
//    @Test
//    public void testFrameDecoded(){
//        ByteBuf buffer = Unpooled.buffer();
//        for (int i = 0; i < 9; i++) {
//            buffer.writeByte(i);
//        }
//        ByteBuf input = buffer.duplicate();
//
//        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));
//
//        assertTrue(channel.writeInbound(input.readBytes(2)));
//
//        try {
//            channel.writeInbound(input.readBytes(4));
//            //Assert.fail();
//        }catch (TooLongFrameException e){
//
//        }
//
//        assertTrue(channel.writeInbound(input.readBytes(3)));
//        assertTrue(channel.finish());
//
//        // Read frames
//        ByteBuf read = channel.readInbound();
//        assertEquals(buffer.readSlice(2),read);
//        read.release();
//
//        read = channel.readInbound();
//        assertEquals(buffer.skipBytes(4).readSlice(3),read);
//        read.release();
//        buffer.release();
//
//    }
//
//}
