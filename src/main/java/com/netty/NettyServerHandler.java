/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netty;

import com.google.common.base.Charsets;
import java.util.concurrent.ExecutorService;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nicok
 */
public class NettyServerHandler extends SimpleChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private final ExecutorService executorService;

    public NettyServerHandler(ExecutorService executorService) {
        this.executorService = executorService;
    }
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel ch = e.getChannel();
//        logger.info("Client connection from: " + ch.getRemoteAddress().toString());


    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        final ChannelBuffer requestBuffer = (ChannelBuffer) e.getMessage();
        final String requestAscii = requestBuffer.toString(Charsets.US_ASCII);

        final Channel ch = e.getChannel();
        Runnable r = new Runnable() {
            public void run() {
                ChannelBuffer response = ChannelBuffers.wrappedBuffer("ACK".getBytes());

                ch.write(response).addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture cf) throws Exception {
   //                             logger.info("Sent data");
                        Channel ch = cf.getChannel();
                        ch.close().addListener(new ChannelFutureListener() {
                            public void operationComplete(ChannelFuture cf) throws Exception {
//                                    logger.info("Channel closed");
                            }
                        });
                    }
                });
            }
        };
        executorService.submit(r);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
    }
}
