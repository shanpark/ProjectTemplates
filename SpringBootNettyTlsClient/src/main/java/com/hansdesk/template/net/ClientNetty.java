package com.hansdesk.template.net;

import com.hansdesk.template.event.EventPool;
import com.hansdesk.template.event.EventService;
import com.hansdesk.template.event.EventType;
import com.hansdesk.template.handler.DummyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
public class ClientNetty implements Client {
    private Logger logger = LoggerFactory.getLogger(ClientNetty.class);

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 19090;

    @Autowired
    private EventService eventService;
    @Autowired
    private EventPool eventPool;

    private EventLoopGroup workerGroup;

    @Override
    public void start() {
        workerGroup = new NioEventLoopGroup(4);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    SSLEngine engine = SslClientContextFactory.getSslContext().createSSLEngine();
                    engine.setUseClientMode(true);

                    pipeline.addLast("ssl", new SslHandler(engine)); // pipeline의 가장 상단에 SslHandler를 배치한다.

                    pipeline.addLast("frameDecoder", new LineBasedFrameDecoder(80));  // 그 후에 encoder, decoder 같은 handler를 배치한다
                    pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                    pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));

                    pipeline.addLast("dummy", new DummyClientHandler()); // 마지막으로 message object 핸들러를 배치한다
                }
            });

            // Start the connection attempt.
            bootstrap.connect(HOST, PORT).sync();

            logger.info("The client connected to server successfully.");
        } catch (Exception e) {
            logger.error("start() failed.", e);
            eventService.sendEvent(eventPool.getEvent(EventType.ID_SHUTDOWN));
        }
    }

    @Override
    public void stop() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();

            logger.info("The client has been stopped successfully.");
        }
    }
}
