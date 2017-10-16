package com.sun.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

/**
 * Created by sun on 2017/10/11 上午1:17.
 */
public class ByteBufTest {
	public static void main(String[] args) {
		UnpooledByteBufAllocator allocator = UnpooledByteBufAllocator.DEFAULT;
		//可以指定容量，也可以不指定，最好指定大小，方便后面解码。
		ByteBuf buf = allocator.directBuffer(5);
		byte[] src = "hello".getBytes();
		System.out.println(src.length);
		buf.writeBytes(src);
		ByteBuf buf2 = allocator.heapBuffer();
		buf2.writeBytes(buf);
		//buf.capacity(2);
		byte[] dest = new byte[5];;
		if (buf.hasArray()) {
			buf.readBytes(dest);
			System.out.println("buf.hasArray():true, " + new String(dest));
		}else if(buf.isDirect()){
			// direct buffer 不能调用readBytes()类方法，否则抛异常
			System.out.println("direct buffer, buf.isReadable(): " + buf.isReadable() );
			buf.getBytes(0, dest);
			System.out.println("buf.hasArray():false, " + new String(dest));
		}
		if (buf2.hasArray()) {
			buf2.readBytes(dest);
			System.out.println("buf2.hasArray():true, " + new String(dest));
		}else{
			buf2.getBytes(0, dest);
			System.out.println("buf.hasArray():false, " + new String(dest));
		}
	}
}
