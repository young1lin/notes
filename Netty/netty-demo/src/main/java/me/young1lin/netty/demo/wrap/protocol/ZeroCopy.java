package me.young1lin.netty.demo.wrap.protocol;

import io.netty.channel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/2 9:01 下午
 */
public class ZeroCopy extends ChannelInitializer<Channel> {


    public void zeroCopy() throws FileNotFoundException {

    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        File file = new File(System.getProperty("user.dir") + File.separator + "pom.xml");
        FileInputStream in = new FileInputStream(file);
        FileRegion region = new DefaultFileRegion(
                in.getChannel(), 0, file.length());

        ch.writeAndFlush(region).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause();
                    cause.printStackTrace();
                }
            }
        });
    }

}
