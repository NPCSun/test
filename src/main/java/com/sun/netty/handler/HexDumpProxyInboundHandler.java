package com.sun.netty.handler;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

public class HexDumpProxyInboundHandler extends SimpleChannelUpstreamHandler {
    private final ClientSocketChannelFactory cf;
    private final String remoteHost;
    private final int remotePort;
    private volatile Channel outboundChannel;
    public HexDumpProxyInboundHandler(
            ClientSocketChannelFactory cf, String remoteHost, int remotePort) {
        this.cf = cf;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }
    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        // 挂起输入信息，直至连接到远程服务器。
        final Channel inboundChannel = e.getChannel();
        inboundChannel.setReadable(false);
        // 开始尝试连接。
        ClientBootstrap cb = new ClientBootstrap(cf);
        cb.getPipeline().addLast("handler", new OutboundHandler(e.getChannel()));
        ChannelFuture f = cb.connect(new InetSocketAddress(remoteHost, remotePort));
        outboundChannel = f.getChannel();
        f.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // 尝试连接成功；
                    // 开始接收输入信息。
                    inboundChannel.setReadable(true);
                } else {
                    //
                	StringBuilder response = new StringBuilder("http/1.1 504 Service Unavailable");
                	response.append("\r\n");
                	response.append("Content-Type:text/html;charset=utf-8");
                	response.append("\r\n");
                	response.append("Content-Length:" + "服务不可用".getBytes("utf-8").length);
                	response.append("\r\n");
                	response.append("\r\n");
                	response.append("服务不可用");
                    inboundChannel.write(ChannelBuffers.wrappedBuffer(response.toString().getBytes("utf-8")))
                    .addListener(ChannelFutureListener.CLOSE);;
                }
            }
        });
    }
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        ChannelBuffer msg = (ChannelBuffer) e.getMessage();
        //System.out.println(">>> " + ChannelBuffers.hexDump(msg));
        outboundChannel.write(msg);
    }
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        //e.getCause().printStackTrace();
        closeOnFlush(e.getChannel());
    }
    private static class OutboundHandler extends SimpleChannelUpstreamHandler {
        private final Channel inboundChannel;
        OutboundHandler(Channel inboundChannel) {
            this.inboundChannel = inboundChannel;
        }
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
                throws Exception {
            ChannelBuffer msg = (ChannelBuffer) e.getMessage();
            //System.out.println("<<< " + ChannelBuffers.hexDump(msg));
            inboundChannel.write(msg);
        }
        @Override
        public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
                throws Exception {
            closeOnFlush(inboundChannel);
        }
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
                throws Exception {
            //e.getCause().printStackTrace();
            closeOnFlush(e.getChannel());
        }
    }
    /**
     * 在所有队列写请求完成（flush）后，关闭指定channel。
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isConnected()) {
            ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}