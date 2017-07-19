package com.solar.dao.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.dao.ShipDao;
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
			for (String str : key) {
				String versionPath = resouceBundleUtil.getInfo("config/ship", str) + File.separator + VERSION;

				List<String> content = FileUtils.readLines(new File(versionPath)); 
				Map<String, String> map = new HashMap<String, String>();
				if(content.size() > 0){
					map.put(str, content.get(0));
				}
				resultList.add(map); 
			}
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

	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("db", 12312);
		System.out.println(123);
	}

}
