package com.sun.netty.handler;


import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.RandomAccessFile;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpServerOutboundHandler extends ChannelOutboundHandlerAdapter {

	private static Log log = LogFactory.getLog(HttpServerOutboundHandler.class);


	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
			throws Exception {
		if (msg instanceof DefaultHttpResponse) {
			DefaultHttpResponse response = (DefaultHttpResponse) msg;

			if (HttpUtil.isKeepAlive(response)) {
				response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			}

			ChannelFuture sendFileFuture = null;
			if (response.headers().get("favicon") != null) {
				RandomAccessFile raf;
				try {
					raf = new RandomAccessFile("/root/test.jpeg", "r");
				} catch (FileNotFoundException ignore) {
					return;
				}
				long fileLength = raf.length();

				response = new DefaultHttpResponse(HTTP_1_1, OK);
				HttpUtil.setContentLength(response, fileLength);
				response.headers().set(CONTENT_TYPE, "image/jpeg");
				// Write the initial line and the header.
				ctx.write(response);

				// Write the content. 1KB=1024*8byte=8192byte
				sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)));
			} else {
				response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("success".getBytes("UTF-8")));
				response.headers().set(CONTENT_TYPE, "text/plain")
						//.set(CONTENT_LENGTH, response.content().readableBytes())
						.set(SERVER, "Netty/sun");
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