package com.solar.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.mysql.jdbc.PreparedStatement;

public class SQLExcute {

	public static void main(String[] args) {
		updateDB();
	}

	public static void updateDB() {
		try {
			String propertyName = "config/ship";
			String prefix = "web";
			String suffix = File.separator + "db";
			String dbConfigPath = ResouceBundleUtil.getInfo(propertyName, prefix);
			File file = new File(dbConfigPath + suffix);
			File[] dbList = file.listFiles();
			for (File db : dbList) {
				if (db.getName().indexOf("sql") > 0) {
					List<String> list = FileUtils.readLines(db);
					ConnectUtil connectUtil = new ConnectUtil();
					Connection conn = connectUtil.getConn();
					for (String str : list) {
						System.out.println(str);
						connectUtil.excuteSQL(conn, str);
					}
					connectUtil.closeConn(conn);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void updateDB(File sqlFile) {
		// TODO Auto-generated method stub
		try {

			if (sqlFile.getName().indexOf("sql") > 0) {
				List<String> list = FileUtils.readLines(sqlFile);
				ConnectUtil connectUtil = new ConnectUtil();
				Connection conn = connectUtil.getConn();
				for (String str : list) {
					System.out.println(str);
					connectUtil.excuteSQL(conn, str);
				}
				connectUtil.closeConn(conn);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static String getVersionByKey(String key,String tableName){
		String sql = "slect new_version from ? where key = ? order by update_time desc";
		ConnectUtil connectUtil = new ConnectUtil();
		Connection conn = connectUtil.getConn();
		
		try {
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, tableName);
			ps.setString(2, key);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				return rs.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
