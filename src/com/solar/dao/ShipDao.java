package com.solar.dao;

import java.util.List;
import java.util.Map;

public interface ShipDao {
	public Map<String, Object> updateVersion(String json);
	
	public Map<String, Object> getShipVersion(String[] key);
	
	public Map<String, Object> upzip();
	
	public boolean writeUpdateLogs(String data,String json);
	
	public boolean updateLogs(String data,String json);
	
	
	
	public Map<String, Object> validateLogs(String data);
}
