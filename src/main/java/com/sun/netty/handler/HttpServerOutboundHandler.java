package com.sun.netty.handler;


import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpServerOutboundHandler extends ChannelOutboundHandlerAdapter {

	private static Log log = LogFactory.getLog(HttpServerOutboundHandler.class);
	private static byte[] src;
	private static ByteBuf buf;
	static {
		try {
			src = "success".getBytes("UTF-8");
			PooledByteBufAllocator pooledByteBufAllocator = new PooledByteBufAllocator();
			buf = pooledByteBufAllocator.buffer(src.length).writeBytes(src);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
			throws Exception {
		if (msg instanceof DefaultHttpResponse) {
			DefaultHttpResponse response = (DefaultHttpResponse) msg;

			if (HttpUtil.isKeepAlive(response)) {
				response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			}

			ChannelFuture sendFileFuture = null;
			String uri = response.headers().get("favicon");
			if (uri != null) {
				RandomAccessFile raf;
				try {
					raf = new RandomAccessFile("/root" + uri, "r");
				} catch (FileNotFoundException ignore) {
					return;
				}
				long fileLength = raf.length();

				response = new DefaultHttpResponse(HTTP_1_1, OK);
				HttpUtil.setContentLength(response, fileLength);
				response.headers().set(CONTENT_TYPE, "image/jpeg");
				ctx.write(response);
				// Write the content. 1KB=1024*8byte=8192byte
				sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)));
			} else {

				response = new DefaultFullHttpResponse(HTTP_1_1, OK, buf);
				response.headers().set(CONTENT_TYPE, "text/plain")
						.set(CONTENT_LENGTH, buf.readableBytes())
						.set(SERVER, "Netty/sun");
				ctx.writeAndFlush(response);
			}

			if (!HttpUtil.isKeepAlive(response)) {
				if(sendFileFuture!=null){
					sendFileFuture.addListener(ChannelFutureListener.CLOSE);
				}
			}

		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}