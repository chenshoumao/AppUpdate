package com.solar.dao;

import java.util.List;
import java.util.Map;

public interface ShipDao {
	public Map<String, Object> updateVersion(String json);
	
	public Map<String, List> getShipVersion(String[] key);
	
	public Map<String, Object> upzip();
}
