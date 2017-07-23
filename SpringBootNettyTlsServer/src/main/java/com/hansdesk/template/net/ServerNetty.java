package com.hansdesk.template.net;

import com.hansdesk.template.event.EventPool;
import com.hansdesk.template.event.EventService;
import com.hansdesk.template.event.EventType;
import com.hansdesk.template.handler.DummyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLEngine;

/**
 * Created by shanpark on 2017. 7. 21..
 */
@Component
public class ServerNetty implements Server {
    private Logger logger = LoggerFactory.getLogger(ServerNetty.class);

    private static final int PORT = 19090;

    @Autowired
    private EventService eventService;
    @Autowired
    private EventPool eventPool;

    private EventLoopGroup acceptorGroup;
    private EventLoopGroup workerGroup;

    @Override
    public void start() {
        acceptorGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(4);

        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(acceptorGroup, workerGroup);
            sb.channel(NioServerSocketChannel.class);
            sb.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    // Add SSL handler first to encrypt and decrypt everything.
                    // In this example, we use a bogus certificate in the server side
                    // and accept any invalid certificates in the client side.
                    // You will need something more complicated to identify both
                    // and server in the real world.

                    SSLEngine engine = SslServerContextFactory.getSslContext().createSSLEngine();
                    engine.setUseClientMode(false);

                    pipeline.addLast("ssl", new SslHandler(engine)); // pipeline의 가장 상단에 SslHandler를 배치한다.

                    pipeline.addLast("frameDecoder", new LineBasedFrameDecoder(80));  // 그 후에 encoder, decoder 같은 handler를 배치한다
                    pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                    pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));

                    pipeline.addLast("dummy", new DummyServerHandler()); // 마지막으로 message object 핸들러를 배치한다
                }
            });
            sb.option(ChannelOption.SO_BACKLOG, 128);
            sb.childOption(ChannelOption.SO_KEEPALIVE, true);

            sb.bind(PORT).sync();

            logger.info("The server has been started up successfully.");
        } catch (Exception e) {
            logger.error("start() failed.", e);
            eventService.sendEvent(eventPool.getEvent(EventType.ID_SHUTDOWN));
        }
    }

    @Override
    public void stop() {
        acceptorGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        logger.info("The client has been stopped successfully.");
    }
}
