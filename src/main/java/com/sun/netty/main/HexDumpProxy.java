package com.sun.netty.main;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
 
public class HexDumpProxy {
    public static void main(String[] args) throws Exception {
 
        // 解析命令行参数。
        int localPort = 8081;
        String remoteHost = "127.0.0.1";
        int remotePort = 8080;
        System.err.println(
                "Proxying *:" + localPort + " to " +
                remoteHost + ':' + remotePort + " ...");
        // 配置引导程序（bootstrap）。
        Executor executor = Executors.newCachedThreadPool();
        ServerBootstrap sb = new ServerBootstrap(
                new NioServerSocketChannelFactory(executor, executor));
        // 设置事件pipeline factory。
        ClientSocketChannelFactory cf =
                new NioClientSocketChannelFactory(executor, executor);
        sb.setPipelineFactory(
                new HexDumpProxyPipelineFactory(cf, remoteHost, remotePort));
        // 启动服务器。
        sb.bind(new InetSocketAddress(localPort));
    }
}