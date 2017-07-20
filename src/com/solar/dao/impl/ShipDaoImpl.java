package com.solar.dao.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.PreparedStatement;
import com.solar.dao.ShipDao;
import com.solar.utils.ConnectUtil;
import com.solar.utils.ReadFile;
import com.solar.utils.ResouceBundleUtil;
import com.sun.jna.Function;

public class ShipDaoImpl implements ShipDao {

	private ResourceBundle resource = ResourceBundle.getBundle("config/ship");
	private final String VERSION = "version.txt";

	/**
	 * @author 陈守貌
	 * @Time 2017-07-14
	 * @Function 船端请求更新
	 * @param json,是版本信息的格式化字符串，包括船端的版本，岸端的版本
	 */
	@Override
	public Map<String, Object> updateVersion(String json) {
		// TODO Auto-generated method stub

		return null;
	}

	/**
	 * @author 陈守貌
	 * @Time 2017-07-14
	 * @Funtion 获取船端的版本的信息
	 * @param key,一个字符串数据，即想要更新的组件的关键字
	 */
	@Override
	public Map<String, List> getShipVersion(String[] key) {
		// TODO Auto-generated method stub
		Map<String, List> result = new HashMap<String, List>();
		List resultList = new ArrayList();
		try {
			ResouceBundleUtil resouceBundleUtil = new ResouceBundleUtil();
			Map<String, String> map = new HashMap<String, String>();
			for (String str : key) {
				String versionPath = resouceBundleUtil.getInfo("config/ship", str) + File.separator + VERSION;
				File file = new File(versionPath);
				if(file.exists()){
					List<String> content = FileUtils.readLines(file);
					
					if (content.size() > 0) {
						map.put(str, content.get(0)); 
					}
					
				}
			}
			resultList.add(map);
			result.put("ship", resultList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Map<String, Object> upzip() {
		// TODO Auto-generated method stub
		return null;
	}

 

	@Override
	public boolean writeUpdateLogs(String data, String json) {
		// TODO Auto-generated method stub
		String[] keyList = data.split(",");
		ResouceBundleUtil bundleUtil = new ResouceBundleUtil();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, List<Map<String, Object>>> map = mapper.readValue(json, HashMap.class);
			List<Map<String, Object>> list = map.get("ship");
			int count = 0;
			for (String key : keyList) {
				String keyInfo = bundleUtil.getInfo("config/module", key);
				
				
				String version = null;
				for(Map<String, Object> listMap:list){
					if(listMap.get(key) != null)
						version = (String) listMap.get(key);
				}
				boolean state = selectLogs(keyInfo, version);
				if(state){
				String sql = "insert into ship_update_logs(update_type,original_version,create_time,update_state,is_over)"
						+ " value(?,?,?,?,?)";
				ConnectUtil connectUtil = new ConnectUtil();
				Connection conn = connectUtil.getConn();
				PreparedStatement ps;

				ps = (PreparedStatement) conn.prepareStatement(sql);
				ps.setString(1, keyInfo);
				ps.setString(2, (String) version);
				SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				ps.setString(3, simpleFormat.format(date));
				ps.setString(4, "等待岸端数据反馈...");
				ps.setInt(5, 0);
				return ps.execute();
				}
				else{
					return updateLogs(keyInfo, version);
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Map<String, Object> validateLogs(String data) {
		// TODO Auto-generated method stub
		String[] keyList = data.split(",");
		ResouceBundleUtil bundleUtil = new ResouceBundleUtil();
		Map<String, Object> result = new HashMap<String,Object>();
		String info = "";
		for(String key:keyList){
			String keyInfo = bundleUtil.getInfo("config/module", key);
			String sql = "select * from ship_update_logs where update_type = ? and is_over = 0";
			ConnectUtil connectUtil = new ConnectUtil();
			Connection conn = connectUtil.getConn();
			try {
				PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
				ps.setString(1, keyInfo);
				ResultSet rs = ps.executeQuery();
				if(rs.next())
					info += keyInfo + " ";
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(info != ""){
			result.put("state", false);
			result.put("info", info + "已经在等待更新中，无需再次请求");
		}
		else
			result.put("state", true);
			
		return result;
	}

	public boolean selectLogs(String keyInfo,String version){
		String sql = "select * from ship_update_logs where original_version = ? and update_type = ? and is_over = 0";
		ConnectUtil connectUtil = new ConnectUtil();
		Connection conn = connectUtil.getConn();
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, version);
			ps.setString(2, keyInfo);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean updateLogs(String keyInfo, String version) {
		 
		try {
			
			int count = 0;
		 
				String sql = "update ship_update_logs set  create_time = ? where update_type =? and original_version = ? and is_over = 0";
				ConnectUtil connectUtil = new ConnectUtil();
				Connection conn = connectUtil.getConn();
				PreparedStatement ps; 
				ps = (PreparedStatement) conn.prepareStatement(sql);
				
				SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				ps.setString(1, simpleFormat.format(date));  
				ps.setString(2, keyInfo); 
				ps.setString(3, version); 
				boolean state = ps.execute();
				ps.close();
				connectUtil.closeConn(conn);
				return state;
		 

		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	 
	}

	public Map<String, Object> validateVersion() {
		// TODO Auto-generated method stub
		Map<String, Object> returnResult = new HashMap<String, Object>();
		ConnectUtil connectUtil = new ConnectUtil();
		ResouceBundleUtil bundleUtil = new ResouceBundleUtil();
		String tempPath = bundleUtil.getInfo("config/ship", "unzipPath");
		Connection conn = connectUtil.getConn();
		String sql = "select update_type,original_version from ship_update_logs where is_over= 0";
		PreparedStatement ps;
		ObjectMapper mapper = new ObjectMapper();
		
		
		//获取本地的一系列的版本信息
		String[] data = {"app","haitu","ditu","db"};
		Map<String, List> shipVersionMap = this.getShipVersion(data);
		List<Map<String, Object>> shipVersionList = shipVersionMap.get("ship");
		
		Map<String, Object> shipVersion = shipVersionList.get(0);
		
		//审查结果
		boolean state = false;
		//详细结果
		String info = "";
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			outer:
			while(rs.next()){
				String update_type = rs.getString(1);
				String type = bundleUtil.getInfo("config/module", update_type);
				String original_version = rs.getString(2);
				File file = null;
				switch (type) {
				case "app":
					file = new File(tempPath + "/division.txt") ; 
					break;
				case "haitu":
					file = new File(tempPath + "/haitu/division.txt") ; 
					break;
				case "ditu":
					file = new File(tempPath + "/ditu/division.txt") ; 
					break;
				case "db":
					file = new File(tempPath + "/db/division.txt") ; 
					break;
				default:
					break;
				}
				
				
				if(file.exists()){
					//读取文件的内容
					List<String> line = FileUtils.readLines(file);
					//默认一行
					int size = line.size();
					if(size != 1){
						//报告错误，文件不存在
						state = false;
						break outer;
					} 
					String lineStr = line.get(0);
					List list = mapper.readValue(lineStr, ArrayList.class);
					size = line.size();
					if(size != 1){
						//报告错误，文件不存在
						state = false;
						break outer;
					} 
					Map<String, String> map = (Map<String, String>) list.get(0);
					
					String old_version = map.get("from");
					
					//获取船端对应的版本
					String original_verion = (String) shipVersion.get(type);
					if(!original_verion.equals(old_version)){
						//报告错误，版本不匹配！
						state = false;
						info = "当前的应用版本为 ：" + original_verion + ",但是增量的起始版本是: " + old_version;
						break outer;
					}
					state = true;
					
				}else{
					//报告错误，文件不存在
					state = false;
					break outer;
				}
					
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		returnResult.put("state", state);
		returnResult.put("info", info);
		return returnResult;
	}
	
	public static void main(String[] args) {
		try {
			List<String> line = FileUtils.readLines(new File("D:\\海图项目\\临时文件\\增量文件\\division.txt"));
			 
			System.out.println(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
