package com.solar.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.dao.ShipDao;
import com.solar.dao.impl.LandDaoImpl;
import com.solar.dao.impl.ShipDaoImpl;

/**
 * Servlet implementation class ShipServlet
 */
@WebServlet("/ShipServlet")
public class ShipServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ShipDaoImpl dao;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShipServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("ster", 122);
		String str = mapper.writeValueAsString(map);
		System.out.println(str);
    	
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		String data = request.getParameter("data");
		
		//默认加上数据库的版本
		data += "db";
		dao = new ShipDaoImpl();
	 
		 
			//获取本地的对应组件的版本,存在map集合中
			Map<String, List> localVersion = this.getLocalVersion(data);
		
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(localVersion); 
			dao.writeUpdateLogs(data, json);
			request.getRequestDispatcher("/LandListener?ship="+json).forward(request,response);
	 
		//
		
//		PrintWriter out = response.getWriter();
//		out.println(localVersion);W
		
		
//		String action = request.getParameter("action");
//		String part = request.getParameter("part");
//		Class osSystem = null;
//		try {
//			osSystem = Class.forName("com.solar.servlet.ShipServlet");
//			Object obj = osSystem.newInstance();
//			// 获取方法
//			Method m = obj.getClass().getDeclaredMethod(action, String.class);
//			// 调用方法
//			m.invoke(obj, part);
//
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
	}
	

	
	public void sayHello(String name){
		System.out.println(name);
	}
	public void sayHello(String name,String sex){
		System.out.println(name + "," + sex);
	}
	
	/**
	 * @author 陈守貌
	 * @Time 2017-07-14
	 * @Funtion 船端更新版本
	 * 
	 */
	public Map<String, List> getLocalVersion(String part){
		Map<String, List> map = new HashMap<String,List>();
		//获取想要更新的组件的信息 
		String[] updatePart = part.split(","); 
		//获取本地对应的组件的版本信息
		ShipDao dao =  new ShipDaoImpl();
		map = dao.getShipVersion(updatePart); 
		return map;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
