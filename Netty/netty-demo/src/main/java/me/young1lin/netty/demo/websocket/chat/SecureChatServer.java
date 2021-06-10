package me.young1lin.netty.demo.websocket.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/4 11:17 下午
 */
public class SecureChatServer extends ChatServer {

    private final SslContext context;

    public SecureChatServer(boolean isOriginal, SslContext context) {
        super(isOriginal);
        this.context = context;
    }

    @Override
    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group) {
        return new SecureChatServerInitializer(group,isOriginal,context);
    }

    public static void main(String[] args) throws CertificateException, SSLException {
        boolean isOriginal = true;
        int port = 9999;
        SelfSignedCertificate cert = new SelfSignedCertificate();
        SslContext context = SslContext.newServerContext(cert.certificate(),cert.privateKey());

        SecureChatServer endpoint = new SecureChatServer(isOriginal,context);
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread(endpoint::destroy));
        future.channel().closeFuture().syncUninterruptibly();
    }

}
