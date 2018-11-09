package com.sun.storm;

import java.util.Map;
import java.util.Random;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

/**
 * Created by sun on 2018/1/9 下午8:36.
 */
public class RandomSpout extends BaseRichSpout {

	private SpoutOutputCollector collector;
	private Random random;

	@Override
	public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
		this.collector = spoutOutputCollector;
		random = new Random();
	}

	@Override
	public void nextTuple() {
		while(true){
			Values values = new Values(random.nextInt(100));
			collector.emit(values);
			try{
				Thread.sleep(500);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("randomInt"));
	}
}
