/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nicok
 */
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public static final class NamedThreadFactory implements ThreadFactory {
        int i = 0;
        private final String prefix;

        public NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(prefix + i);
            i++;
            return t;
        }
    }

    public static void main(String[] args) throws Exception {
        logger.info("Starting server.");
        ChannelFactory factory = new NioServerSocketChannelFactory(
            Executors.newCachedThreadPool(new NamedThreadFactory("Accept")),
            Executors.newCachedThreadPool(new NamedThreadFactory("IO")),
            1
        );
        /*ChannelFactory factory = new OioServerSocketChannelFactory(
            Executors.newSingleThreadExecutor(new NamedThreadFactory("Accept")),
            Executors.newSingleThreadExecutor(new NamedThreadFactory("IO"))
        );*/


        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        final ExecutorService es = Executors.newCachedThreadPool(new NamedThreadFactory("Work"));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(
                    new DelimiterBasedFrameDecoder(1024, Delimiters.lineDelimiter()),
                    new NettyServerHandler(es)
                );
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        bootstrap.bind(new InetSocketAddress(8080));
    }
}
