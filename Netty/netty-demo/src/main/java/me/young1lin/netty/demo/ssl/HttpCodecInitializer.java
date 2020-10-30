package me.young1lin.netty.demo.ssl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/30 10:48 下午
 */
public class HttpCodecInitializer extends ChannelInitializer<Channel> {

    private final boolean isClient;

    private final SslContext context;

    public HttpCodecInitializer(boolean isClient, SslContext context) {
        this.isClient = isClient;
        this.context = context;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine sslEngine = context.newEngine(ch.alloc());
        pipeline.addLast("ssl",new SslHandler(sslEngine));

        if(isClient){
            pipeline.addLast("codec",new HttpClientCodec());
        }else {
            pipeline.addLast("codec",new HttpServerCodec());
        }
    }

}
