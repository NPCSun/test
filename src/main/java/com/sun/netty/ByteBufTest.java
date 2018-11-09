package com.sun.netty;

import io.netty.buffer.*;

/**
 * Created by sun on 2017/10/11 上午1:17.
 */
public class ByteBufTest {

    public static void handleRead(ByteBuf byteBuf) {

        if (byteBuf.hasArray()) {
            byte[] array = byteBuf.array();
            //byteBuf.readByte();
            int offset = byteBuf.arrayOffset() + byteBuf.readerIndex();
            int length = byteBuf.readableBytes();
            //不用复制，直接使用。
            System.out.println("hasArray():true, " + new String(array, offset, length));
        } else if (byteBuf.isDirect()) {
            byteBuf.resetReaderIndex();
            int length = byteBuf.readableBytes();
            byte[] dest = new byte[length];
            int readerIndex = byteBuf.readerIndex();
            //需要复制到dest，即从堆外拷贝到堆内进行后续操作。
            byteBuf.getBytes(readerIndex, dest);
            System.out.println("hasArray():false, " + new String(dest));
        }
    }

    public static void main(String[] args) {
        //UnpooledByteBufAllocator allocator = UnpooledByteBufAllocator.DEFAULT;
        UnpooledByteBufAllocator allocator = new UnpooledByteBufAllocator(false);

        PooledByteBufAllocator pooledByteBufAllocator = new PooledByteBufAllocator(false);

        //可以指定容量，也可以不指定，最好指定大小，方便后面解码。
        ByteBuf directBuffer = allocator.directBuffer(5);
        byte[] src1 = "header".getBytes();
        directBuffer.writeBytes(src1);

        handleRead(directBuffer);


        byte[] src2 = "body".getBytes();
        ByteBuf heapBuffer = allocator.heapBuffer();
        heapBuffer.writeBytes(src2);
        //heapBuffer.writeBytes(directBuffer);
        //buf.capacity(2);

        handleRead(heapBuffer);

        System.out.println("引用计数：" + heapBuffer.refCnt());
        /*if(heapBuffer.refCnt()>0){
            heapBuffer.release();
        }*/


        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        messageBuf.addComponents(directBuffer, heapBuffer);
        for (ByteBuf buf : messageBuf) {
            System.out.println(buf.toString());
        }
        int length = 0;
        if(messageBuf.isReadable()){
            length = messageBuf.readableBytes();

        }else{
            length = messageBuf.capacity();
        }
        byte[] array = new byte[length];
        int readerIndex = messageBuf.readerIndex();
        messageBuf.setByte(0, 64);
        messageBuf.getBytes(readerIndex, array);

        System.out.println("hasArray():" + messageBuf.hasArray() + "," + new String(array));
    }
}
