package com.solar.dao;

import java.util.List;
import java.util.Map;

import com.solar.bean.Version;

public interface LandDao {
	
	public Map<String,Object> analysisVersion(String versionInfo);
	
	public Map<String, Object> versionFilter(String key,List<String> keyList,Map<String, Object> allVersion,String shipVerion,String moduleVersionOfLand);
	
	public List<Version> getVersionFromPath(String path);
	
	public boolean generateIncrement(String key,String moduleVersionOfShip,String moduleVersionOfLand);
}
