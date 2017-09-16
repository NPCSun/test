package com.sun.netty.handler;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.ipfilter.IpFilterRule;
import org.jboss.netty.handler.ipfilter.IpFilterRuleHandler;
import org.jboss.netty.handler.ipfilter.IpSubnetFilterRule;

/**
 * 
 * @author user
 *
 */
@Sharable
public class IPInterfaceRateFilter extends IpFilterRuleHandler {

	private static final ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();
	 @Override
	    protected boolean accept(ChannelHandlerContext ctx, ChannelEvent e, InetSocketAddress inetSocketAddress)
	            throws Exception {
		 boolean flag = super.accept(ctx, e, inetSocketAddress);
		 if(!flag){
			return false; 
		 }else{
			 String remoteIP = inetSocketAddress.getAddress().toString();
			 if(map.containsKey(remoteIP)){
				 String value = map.get(remoteIP);
				 int index = value.indexOf("-");
				 int count = Integer.parseInt(value.substring(0, index));
				 long lastTime = Long.parseLong(value.substring(index+1));
				 long currentTime = System.currentTimeMillis();
				 long interval = currentTime - lastTime;
				 if(interval<500){
					 map.put(remoteIP, count + 1 + "-" + currentTime);
					 if(count<3){
						 //StringBuilder response = new StringBuilder("http/1.1 504 Service Unavailable");
				         //e.getChannel().write(ChannelBuffers.wrappedBuffer(response.toString().getBytes("utf-8")));
						 return true;
					 }else{
						 IpFilterRule ipFilterRule = new IpSubnetFilterRule(false,inetSocketAddress.getAddress(),32);
					     super.add(ipFilterRule);
						 super.addIfAbsent(ipFilterRule);
						 //推荐直接返回false，关闭连接。首次直接关闭连接
						 return false;
					 }
				 }else{
					 map.put(remoteIP, count + "-" + currentTime);
					 return true;
				 }
			 }else{
				 map.put(remoteIP, "1-" + System.currentTimeMillis());
				 return true;
			 }
		 }
	 }
}
