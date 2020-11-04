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
// * @date 2020/10/28 10:02 下午
// */
//public class AbsIntegerEncoderTest {
//
//    @Test
//    public void testEncoded() {
//        ByteBuf buf = Unpooled.buffer();
//        for (int i = 0; i < 10; i++) {
//            buf.writeInt(i * -1);
//        }
//        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
//
//        assertTrue(channel.writeOutbound(buf));
//        assertTrue(channel.finish());
//
//        // read bytes
//        for (int i = 0; i < 10; i++) {
//            assertEquals(i,(int)channel.readOutbound());
//        }
//
//        assertNull(channel.readOutbound());
//    }
//}
