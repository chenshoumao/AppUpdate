package com.solar.servlet;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.dao.LandDao;
import com.solar.utils.Version;
import com.solar.utils.VersionListUtil;

/**
 * Servlet implementation class LandListener
 */
@WebServlet("/LandListener")
public class LandListener extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private LandDao dao;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LandListener() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		//获取船的版本
		String versionInfo = request.getParameter("ship_version");
		
		//解析各个版本，如你图，数据库，web 以及 想要更新的对应的版本
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Map<String,String>> map = mapper.readValue(versionInfo, HashMap.class);
		//用List 集合封装 船端的版本信息
		Map<String,String> shipVersionMap = new HashMap<String, String>(); 
		shipVersionMap = map.get("ship");
		 
		
		Set<String> set = shipVersionMap.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			String key = (String) it.next();
			//获取此部分在岸端的版本
			String moduleVersionOfLand = shipVersionMap.get(key);
			//非空，则获取对应在船端的版本
			if(moduleVersionOfLand.equals(null)){
				String moduleVersionOfShip = shipVersionMap.get(key);
				//这里对此部分对应于船端的版本不验证是否为空，一开始在船端发送请求时验证即可
				//生成增量升级包
			}
		}
		
	} 
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
