package com.solar.servlet;

import java.io.IOException;
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

import com.solar.dao.ShipDao;
import com.solar.dao.impl.ShipDaoImpl;

/**
 * Servlet implementation class ShipServlet
 */
@WebServlet("/ShipServlet")
public class ShipServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShipServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		String action = request.getParameter("action");
		String part = request.getParameter("part");
		Class osSystem = null;
		try {
			osSystem = Class.forName("com.solar.servlet.ShipServlet");
			Object obj = osSystem.newInstance();
			// ��ȡ����
			Method m = obj.getClass().getDeclaredMethod(action, String.class);
			// ���÷���
			m.invoke(obj, part);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String name = "csm";
		String sex = "boy";
		String method1 = "sayHello";
		
		 Class osSystem = null;
		   try {
		      osSystem = Class.forName("com.solar.servlet.ShipServlet");
		      Object obj = osSystem.newInstance();
		      //��ȡ����  
		      Method m = obj.getClass().getDeclaredMethod(method1, String.class);
		      //���÷���  
		     m.invoke(obj, name);
		     
		     //��ȡ����  
		      Method m2 = obj.getClass().getDeclaredMethod(method1, String.class,String.class);
		     m2.invoke(obj, name,sex);
		     

		   } catch (Exception e1) {
		      e1.printStackTrace();
		   }

	
	 
	}
	
	public void sayHello(String name){
		System.out.println(name);
	}
	public void sayHello(String name,String sex){
		System.out.println(name + "," + sex);
	}
	
	/**
	 * @author ����ò
	 * @Time 2017-07-14
	 * @Funtion ���˸��°汾
	 * 
	 */
	public Map<String, Object> getLocalVersion(String part){
		Map<String, Object> map = new HashMap<String,Object>();
		//��ȡ��Ҫ���µ��������Ϣ 
		String[] updatePart = part.split(","); 
		//��ȡ���ض�Ӧ������İ汾��Ϣ
		ShipDao dao =  new ShipDaoImpl();
		Map<String, Object> shipVersion = dao.getShipVersion(updatePart); 
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
