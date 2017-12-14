package com.sun.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.InlineShardingStrategyConfiguration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by sun on 2017/11/1 下午2:47.
 */
@Service
public class ShardingJdbcService {

	@Resource(name = "shardingDataSource")
	DataSource shardingDataSource;

	@Transactional(propagation = Propagation.REQUIRED)
	public void testInsertTransaction() throws SQLException {
		Connection conn = shardingDataSource.getConnection();
		try{
			Statement statement = conn.createStatement();
			String insertSql = "insert into t_order(user_id, order_id) values(1, 1)";
			statement.execute(insertSql);
			String orderInsertSql = "insert into t_order_message(user_id, order_id) values(1, 1)";
			statement.execute(orderInsertSql);
			if(true){
				//throw new RuntimeException("test");
			}
			System.err.println("无异常，事务提交！");
			conn.commit();
		}catch(Exception e){
			System.err.println("出现异常，事务回滚！");

			conn.rollback();
			e.printStackTrace();
		}

	}

	public void testSelect() throws SQLException {
		Connection conn = shardingDataSource.getConnection();
		Statement statement = conn.createStatement();
		long begin = System.currentTimeMillis();
		int count = 0;
		ResultSet rs;
		String selectSql = "SELECT * FROM t_order where user_id=1";
		rs = statement.executeQuery(selectSql);
		while (rs.next()) {
			System.out.println("userId\t" + rs.getInt(1));
			System.out.println("orderId\t" + rs.getInt(2));
		}
		rs.close();
		System.out.println("查询耗时(秒)： " + (System.currentTimeMillis()-begin)/1000);
	}

}
