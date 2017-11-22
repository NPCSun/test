package com.sun.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingjdbc.core.rule.BindingTableRule;
import io.shardingjdbc.core.rule.ShardingRule;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by sun on 2017/11/1 下午2:47.
 */
//@Service
public class ShardingJdbcTest {

	public static void testInsertTransaction(Map<String, DataSource> dataSourceMap, ShardingRuleConfiguration shardingRuleConfig) throws SQLException {
		// 获取数据源对象
			DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig);
			Connection conn = dataSource.getConnection();
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

			Statement statement = conn.createStatement();
			String insertSql = "insert into t_order(user_id, order_id) values(1, 1)";
			System.out.println(statement.execute(insertSql));

			String orderInsertSql = "insert into t_order_message(user_id, order_id) values(1, 1)";
			System.out.println(statement.execute(orderInsertSql));
			throw new NullPointerException("test");

			/*insertSql = "insert into t_order(user_id, order_id) values(2, 2)";
			System.out.println(statement.execute(insertSql));
			insertSql = "insert into t_order(user_id, order_id) values(3, 3)";
			System.out.println(statement.execute(insertSql));
			insertSql = "insert into t_order(user_id, order_id) values(4, 4)";
			System.out.println(statement.execute(insertSql));

			insertSql = "insert into t_order(user_id, order_id) values(5, 6)";
			System.out.println(statement.execute(insertSql));
*/
			/*long begin = System.currentTimeMillis();
			int count = 0;
			ResultSet rs;
			String selectSql = "SELECT * FROM t_order limit 0, 1";
			while(count<5000){
				count ++;
				rs = statement.executeQuery(selectSql);
				while (rs.next()) {
					//System.out.println("userId\t" + rs.getInt(1));
					//System.out.println("orderId\t" + rs.getInt(2));
				}
				rs.close();
			}
			System.out.println("1/10万次查询耗时(秒)： " + (System.currentTimeMillis()-begin)/1000);*/

	}
	@Transactional
	public static void init() throws SQLException {
		// 配置真实数据源
		Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();

		// 配置第一个数据源
		DruidDataSource dataSource1 = new DruidDataSource();
		dataSource1.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource1.setUrl("jdbc:mysql://127.0.0.1:3306/ds_0");
		dataSource1.setUsername("root");
		dataSource1.setPassword("123456");
		dataSourceMap.put("ds_0", dataSource1);

		// 配置第二个数据源
		DruidDataSource dataSource2 = new DruidDataSource();
		dataSource2.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource2.setUrl("jdbc:mysql://127.0.0.1:3306/ds_1");
		dataSource2.setUsername("root");
		dataSource2.setPassword("123456");
		dataSourceMap.put("ds_1", dataSource2);

		// 配置表规则
		TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
		orderTableRuleConfig.setLogicTable("t_order");
		orderTableRuleConfig.setActualDataNodes("ds_${0..1}.t_order_${0..1}");//"ds_0.t_order_0, ds_0.t_order_1, ds_1.t_order_0, ds_1.t_order_1"
		orderTableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "ds_${user_id % 2}"));
		orderTableRuleConfig.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("order_id", "t_order_${order_id % 2}"));

		TableRuleConfiguration messageTableRuleConfiguration = new TableRuleConfiguration();
		messageTableRuleConfiguration.setLogicTable("t_order_message");
		messageTableRuleConfiguration.setActualDataNodes("ds_${0..1}.t_order_message${0..1}");
		messageTableRuleConfiguration.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "ds_${user_id % 2}"));
		messageTableRuleConfiguration.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("order_id", "t_order_message_${order_id % 2}"));


		// 配置分片规则
		ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
		shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);
		shardingRuleConfig.getTableRuleConfigs().add(messageTableRuleConfiguration);
		shardingRuleConfig.getBindingTableGroups().add("t_order");
		shardingRuleConfig.getBindingTableGroups().add("t_order_message");

		testInsertTransaction(dataSourceMap, shardingRuleConfig);
	}
	public static void main(String[] args) throws SQLException {



	}
}
