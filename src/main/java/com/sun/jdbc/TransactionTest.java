package com.sun.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by sun on 2017/10/31 下午3:50.
 */
public class TransactionTest {
	public static void main(String[] args) throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		System.err.println("成功加载 mysql jdbc 驱动");

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			String url = "jdbc:mysql://10.0.0.213:3306/xkhstar?user=root&password=123456&useUnicode=true&characterEncoding=UTF8";
			connection = DriverManager.getConnection(url);
			//一定要在开启事务
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			connection.setAutoCommit(false);
			int isolation = connection.getTransactionIsolation();
			if(isolation==4){
				System.out.println("数据库隔离级别为：Repeatable read.");
				System.out.println("可避免脏读、不可重复读情况的发生。（可重复读）不可以避免虚读");
			}
			System.err.println("成功获取连接");

			statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			String sql = "select * from activity_config";
			resultSet = statement.executeQuery(sql);

			resultSet.beforeFirst();
			while (resultSet.next()) {
				System.out.print(resultSet.getString(1) + "\t");
				System.out.print(resultSet.getString(2) + "\t");
				System.out.println(resultSet.getString(3));
			}
			System.err.println("成功操作数据库");
		} catch(Throwable t) {
			// TODO 处理异常
			t.printStackTrace();
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
			System.err.println("成功关闭资源");
		}

	}
}
