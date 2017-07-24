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

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.dao.ShipDao;
import com.solar.dao.impl.LandDaoImpl;
import com.solar.dao.impl.ShipDaoImpl;
import com.solar.test.MyException;

/**
 * Servlet implementation class ShipServlet
 */
@WebServlet("/ShipServlet")
public class ShipServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(ShipServlet.class);
	private static final long serialVersionUID = 1L;
	private ShipDaoImpl dao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ShipServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			logger.debug("船端第一步：");
			PrintWriter out = response.getWriter();
			String data = request.getParameter("data");
			if(data.equals("") || data.equals(null)){
				throw new MyException("	船端请求更新的关键数据为空！");
			}
			data += "db";
			logger.debug("	获取了 请求更新，请求的数据是 : " + data);
			// 第一步 罗列出所有的组件版本
			String shipKey = "app,haitu,ditu,db";
			Map<String, Object> allShipVersion = this.getLocalVersion(shipKey);
			System.out.println(12300);
			// 第二步 在集合中，将请求的组件包含在内
			allShipVersion.put("toUpdate", data);

			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(allShipVersion);

			logger.debug("	解析成完整的json格式数据: " + json);

			// 讲请求的详细请求信息存进数据库
			dao = new ShipDaoImpl();
			dao.writeUpdateLogs(data, json);
			logger.debug("	将信息发送到岸端"); 
			request.getRequestDispatcher("/LandListener?ship=" + json).forward(request, response);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void sayHello(String name) {
		System.out.println(name);
	}

	public void sayHello(String name, String sex) {
		System.out.println(name + "," + sex);
	}

	/**
	 * @author 陈守貌
	 * @Time 2017-07-14
	 * @Funtion 船端更新版本
	 * 
	 */
	public Map<String, Object> getLocalVersion(String part) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 获取想要更新的组件的信息
		String[] updatePart = part.split(",");
		// 获取本地对应的组件的版本信息
		ShipDao dao = new ShipDaoImpl();
		map = dao.getShipVersion(updatePart);
		return map;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
