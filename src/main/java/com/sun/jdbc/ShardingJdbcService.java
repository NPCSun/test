package com.sun.jdbc;

import java.sql.*;
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

        PreparedStatement statement = conn.prepareStatement("insert into t_order(user_id, order_id) values(?, ?)");
        for (int i = 0; i < 100; i++) {
            try {
                statement.setInt(1, 1);
                statement.setInt(2, i);
                System.out.println(statement.executeUpdate());
            } catch (Throwable e) {
                //System.err.println("出现异常，事务回滚！");

                //conn.rollback();
                e.printStackTrace();
            }
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
        System.out.println("查询耗时(秒)： " + (System.currentTimeMillis() - begin) / 1000);
    }

    public void testCount() throws SQLException {
        Connection conn = shardingDataSource.getConnection();
        Statement statement = conn.createStatement();
        long begin = System.currentTimeMillis();
        int count = 0;
        ResultSet rs;
        String selectSql = "SELECT count(*) FROM t_order where user_id=1";
        rs = statement.executeQuery(selectSql);
        if (rs.next()) {
            System.out.println("count\t" + rs.getInt(1));
        }
        rs.close();
        System.out.println("查询耗时(秒)： " + (System.currentTimeMillis() - begin) / 1000);
    }

    //1+99=100 *50-100=4950
    public void testSum() throws SQLException {
        Connection conn = shardingDataSource.getConnection();
        Statement statement = conn.createStatement();
        long begin = System.currentTimeMillis();
        int count = 0;
        ResultSet rs;
        String selectSql = "SELECT sum(order_id) FROM t_order where user_id=1";
        rs = statement.executeQuery(selectSql);
        if (rs.next()) {
            System.out.println("sum\t" + rs.getInt(1));
        }
        rs.close();
        System.out.println("查询耗时(秒)： " + (System.currentTimeMillis() - begin) / 1000);
    }

}
