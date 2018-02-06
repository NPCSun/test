package com.sun.push.openfire;

import java.io.IOException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

/**
 * Created by sun on 2018/1/30 上午10:58.
 */
public class SmackClient {
	public static void main(String[] args) throws IOException, InterruptedException, XMPPException, SmackException {
		XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
				.setXmppDomain("localhost")
				.setHost("127.0.0.1")
				.setPort(5222)
				.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
				.build();
		AbstractXMPPConnection connection = new XMPPTCPConnection(config);
		connection.addConnectionListener(new ConnectionListener() {
			@Override
			public void connected(XMPPConnection connection) {
				//已连接上服务器
				System.out.println("已连接上服务器");
			}

			@Override
			public void authenticated(XMPPConnection connection, boolean resumed) {
				//已认证
				System.out.println("已认证");
			}

			@Override
			public void connectionClosed() {
				//连接已关闭
				System.out.println("连接已关闭");
			}

			@Override
			public void connectionClosedOnError(Exception e) {
				//关闭连接发生错误
				System.out.println("关闭连接发生错误");
			}

			@Override
			public void reconnectionSuccessful() {
				//重连成功
				System.out.println("重连成功");
			}

			@Override
			public void reconnectingIn(int seconds) {
				//重连中
				System.out.println("重连中");
			}

			@Override
			public void reconnectionFailed(Exception e) {
				//重连失败
				System.out.println("重连失败");
			}
		});
		connection.connect();
		connection.login("sun", "sun");
		// Create a new presence. Pass in false to indicate we're unavailable._
		Presence presence = new Presence(Presence.Type.available);
		presence.setStatus("Gone fishing");
		// Send the stanza (assume we have an XMPPConnection instance called "con").
		connection.sendStanza(presence);
		ChatManager chatManager= ChatManager.getInstanceFor(connection);//从连接中得到聊天管理器
		chatManager.addIncomingListener(new IncomingChatMessageListener() {
			@Override
			public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
				System.out.println("New message from " + from + ": " + message.getBody());
				try {
					Thread.sleep(2000);
					chat.send("hello, i am sun");
				} catch (SmackException.NotConnectedException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		chatManager.addOutgoingListener(new OutgoingChatMessageListener() {
			@Override
			public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
				//System.out.println("New message to " + to + ": " + message.getBody());
			}
		});
		EntityBareJid jid = JidCreate.entityBareFrom("sun2@localhost");
		Chat chat= chatManager.chatWith(jid);//创建一个聊天，username为对方用户名
		chat.send("hello, i am sun");
		Thread.sleep(50000);
	}
}
