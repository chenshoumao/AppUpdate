package com.solar.dao.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.ResourceBundle;

public class RunTest {
	public static void main(String[] args) {
		try {
		//	String json = "{\"ship\": {\"db\":\"1.0.0.0_db_release_20170713\",\"haitu\":\"1.0.0.0_haitu_release_20170713\"}}";
			String json = "{\"ship\": [{\"app\":\"1.0.0.0_app_release_20170713\"}]}";
//			String json = "{\"ship\": [{\"db\":\"1.0.0.0_db_release_20170713\"}]}";
			LandDaoImpl landDaoImpl = new LandDaoImpl();
			Map<String, Object> map = landDaoImpl.analysisVersion(json);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
	}
	
//	public static void main(String[] args) {
//		String str = "D:/海图项目/临时文件/增量文件";
//		File file = new File(str);
//		System.out.println(file.getAbsolutePath());
//		String path = file.getAbsolutePath();
//		path = path.replaceAll("\\\\", "22");
//		System.out.println(path);
//		System.out.println("\\");
//		System.out.println(path.indexOf("\\"));
//	}
}
