package com.solar.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectUtil { 
	
	public Connection getConn(){ 
		// 驱动程序名
		String driver = "com.mysql.jdbc.Driver"; 
		// URL指向要访问的数据库名scutcs
		String url = "jdbc:mysql://localhost:3306/test"; 
		// MySQL配置时的用户名
		String user = "root"; 
		// MySQL配置时的密码
		String password = "root"; 
		try {
			// 加载驱动程序
			Class.forName(driver); 
			// 连续数据库
			Connection conn = DriverManager.getConnection(url, user, password); 
			if (!conn.isClosed())
				System.out.println("Succeeded connecting to the Database!"); 
			// statement用来执行SQL语句

			return conn; 
		} catch (ClassNotFoundException e) {
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void closeConn(Connection conn){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean excuteSQL(Connection conn,String sql){
		boolean result = false;
		try {
			Statement statement = conn.createStatement(); 
			// 要执行的SQL语句 
			result = statement.execute(sql);
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
		}
		return result;
	}
}
