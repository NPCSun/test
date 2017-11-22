package com.sun.jdbc;

import java.sql.Connection;
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
		//conn.setAutoCommit(false);
		/*conn.setAutoCommit(false);
		PreparedStatement preparedStatement = conn.prepareStatement("insert into t_order(user_id, order_id) values(?, ?)");
		long begin = System.currentTimeMillis();
		for(int i=100000; i<1000000; i++){
			preparedStatement.setInt(1, i);
			preparedStatement.setInt(2, i-1);
			preparedStatement.executeUpdate();
			if(i%50==0){
				conn.commit();
			}
			preparedStatement.clearParameters();

		}
		System.out.println("1万次插入耗时(秒)： " + (System.currentTimeMillis()-begin)/1000);*/
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

}
