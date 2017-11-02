package com.sun.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.InlineShardingStrategyConfiguration;

/**
 * Created by sun on 2017/11/1 下午2:47.
 */
public class ShardingJdbcTest {
	public static void main(String[] args) {
		// 配置真实数据源
		Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();

		// 配置第一个数据源
		DruidDataSource dataSource1 = new DruidDataSource();
		dataSource1.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource1.setUrl("jdbc:mysql://localhost:3306/ds_0");
		dataSource1.setUsername("root");
		dataSource1.setPassword("123456");
		dataSourceMap.put("ds_0", dataSource1);

		// 配置第二个数据源
		DruidDataSource dataSource2 = new DruidDataSource();
		dataSource2.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource2.setUrl("jdbc:mysql://localhost:3306/ds_1");
		dataSource2.setUsername("root");
		dataSource2.setPassword("123456");
		dataSourceMap.put("ds_1", dataSource2);

		// 配置表规则
		TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration();
		tableRuleConfiguration.setLogicTable("t_order");
		tableRuleConfiguration.setActualDataNodes("ds_${0..1}.t_order_${0..1}");//"ds_0.t_order_0, ds_0.t_order_1, ds_1.t_order_0, ds_1.t_order_1"

		// 配置分库策略
		tableRuleConfiguration.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "ds_${user_id % 2}"));

		// 配置分表策略
		tableRuleConfiguration.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("order_id", "t_order_${order_id % 2}"));

		// 配置分片规则
		ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
		shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfiguration);

		// 获取数据源对象
		try {
			DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig);
			Connection conn = dataSource.getConnection();
			Statement statement = conn.createStatement();
			//
			/*String insertSql = "insert into t_order(user_id, order_id) values(1, 1)";
			System.out.println(statement.execute(insertSql));
			insertSql = "insert into t_order(user_id, order_id) values(2, 2)";
			System.out.println(statement.execute(insertSql));
			insertSql = "insert into t_order(user_id, order_id) values(3, 3)";
			System.out.println(statement.execute(insertSql));
			insertSql = "insert into t_order(user_id, order_id) values(4, 4)";
			System.out.println(statement.execute(insertSql));*/

			/*String insertSql = "insert into t_order(user_id, order_id) values(5, 6)";
			System.out.println(statement.execute(insertSql));*/

			String selectSql = "SELECT * FROM t_order limit 5, 5";
			ResultSet rs = statement.executeQuery(selectSql);
			while (rs.next()) {
				System.out.println("userId\t" + rs.getInt(1));
				System.out.println("orderId\t" + rs.getInt(2));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
