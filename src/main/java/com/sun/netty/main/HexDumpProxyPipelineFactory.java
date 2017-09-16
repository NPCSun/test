package com.sun.netty.main;

import static org.jboss.netty.channel.Channels.*;

import java.net.InetAddress;

import com.sun.netty.handler.HexDumpProxyInboundHandler;
import com.sun.netty.handler.IPInterfaceRateFilter;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.handler.ipfilter.IpFilterRule;
import org.jboss.netty.handler.ipfilter.IpSubnetFilterRule;

public class HexDumpProxyPipelineFactory implements ChannelPipelineFactory {
    private final ClientSocketChannelFactory cf;
    private final String remoteHost;
    private final int remotePort;

    public HexDumpProxyPipelineFactory(
            ClientSocketChannelFactory cf, String remoteHost, int remotePort) {
        this.cf = cf;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline p = pipeline(); // 注意，这里使用了static import。
        //ip过滤
        IPInterfaceRateFilter ipInterfaceRateFilter = new IPInterfaceRateFilter();
        IpFilterRule ipFilterRule = new IpSubnetFilterRule(false,InetAddress.getByName("192.168.199.2"),24);
        ipInterfaceRateFilter.add(ipFilterRule);
        p.addLast("ipFilter", ipInterfaceRateFilter);
        p.addLast("handler", new HexDumpProxyInboundHandler(cf, remoteHost, remotePort));
        return p;
    }
}