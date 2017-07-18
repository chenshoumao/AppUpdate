package com.solar.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import org.apache.commons.io.FileUtils;

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
}
