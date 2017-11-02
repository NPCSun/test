/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.netty.self;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;


public class DefaultFuture {


    private static final Map<Long, Channel>       CHANNELS   = new ConcurrentHashMap<Long, Channel>();

    private static final Map<Long, DefaultFuture> FUTURES   = new ConcurrentHashMap<Long, DefaultFuture>();

    // invoke id.
    private final long                            id;

    private final Channel                         channel;
    
    private Message                               response;

    private final Lock                            lock = new ReentrantLock();

    private final Condition                       done = lock.newCondition();

    private final long                            start = System.currentTimeMillis();

    private volatile long                         sent;
    
    public DefaultFuture(Channel channel, Message message){
        this.channel = channel;
        this.id = message.getId();
        // put into waiting map.
        FUTURES.put(id, this);
        CHANNELS.put(id, channel);
    }
    
    public Object get(int timeout){
        if (! isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                while (! isDone()) {
                    done.await(timeout, TimeUnit.MILLISECONDS);
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
		if (! isDone()) {
			throw new RuntimeException("RPC invoke timeout!");
		}
        return response;
    }
    

    public boolean isDone() {
        return response != null;
    }



    private long getId() {
        return id;
    }
    
    private Channel getChannel() {
        return channel;
    }
    
    private boolean isSent() {
        return sent > 0;
    }

    private long getStartTimestamp() {
        return start;
    }

    public static DefaultFuture getFuture(long id) {
        return FUTURES.get(id);
    }

    public static boolean hasFuture(Channel channel) {
        return CHANNELS.containsValue(channel);
    }

    private void doSent() {
        sent = System.currentTimeMillis();
    }

    public static void received(TransferMessage response) {
		Message message = null;
        try {
			message = JSON.parseObject(response.getBody(), Message.class);
            DefaultFuture future = FUTURES.remove(message.getId());
            if (future != null) {
				future.doReceived(message);
			}
        } finally {
            if(message != null){
            	CHANNELS.remove(message.getId());
			}
        }
    }

    private void doReceived(Message message) {
        lock.lock();
        try {
        	if(1001 == message.getId()){
				this.response = message;
			}

            if (done != null) {
                done.signal();
            }
        } finally {
            lock.unlock();
        }
    }


}