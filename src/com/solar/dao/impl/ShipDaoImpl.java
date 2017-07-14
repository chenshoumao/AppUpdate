package com.solar.dao.impl;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.solar.dao.ShipDao;
import com.solar.utils.ReadFile;
import com.sun.jna.Function;

public class ShipDaoImpl implements ShipDao {
	
	private ResourceBundle resource = ResourceBundle.getBundle("ship");
	private final String  VERSION = "version.txt";

	/**
	 * @author ����ò
	 * @Time 2017-07-14
	 * @Function �����������
	 * @param json,�ǰ汾��Ϣ�ĸ�ʽ���ַ������������˵İ汾�����˵İ汾
	 */
	@Override
	public Map<String, Object> updateVersion(String json) {
		// TODO Auto-generated method stub
		
		return null;
	}

	/**
	 * @author ����ò
	 * @Time 2017-07-14
	 * @Funtion ��ȡ���˵İ汾����Ϣ
	 * @param key,һ���ַ������ݣ�����Ҫ���µ�����Ĺؼ���
	 */
	@Override
	public Map<String, Object> getShipVersion(String[] key) {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<String,Object>();
		for(String str:key){
			String versionPath = resource.getString(str) + File.separator + key; 
			ReadFile readFile = new ReadFile();
			Map<String, Object> map = readFile.readFileByLines(versionPath + File.separator + VERSION);
			result.putAll(map);
		}
		return result;
	}

	@Override
	public Map<String, Object> upzip() {
		// TODO Auto-generated method stub
		return null;
	}

}