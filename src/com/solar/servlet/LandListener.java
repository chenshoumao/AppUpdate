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
		//��ȡ���İ汾
		String versionInfo = request.getParameter("ship_version");
		
		//���������汾������ͼ�����ݿ⣬web �Լ� ��Ҫ���µĶ�Ӧ�İ汾
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Map<String,String>> map = mapper.readValue(versionInfo, HashMap.class);
		//��List ���Ϸ�װ ���˵İ汾��Ϣ
		Map<String,String> shipVersionMap = new HashMap<String, String>(); 
		shipVersionMap = map.get("ship");
		 
		
		Set<String> set = shipVersionMap.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			String key = (String) it.next();
			//��ȡ�˲����ڰ��˵İ汾
			String moduleVersionOfLand = shipVersionMap.get(key);
			//�ǿգ����ȡ��Ӧ�ڴ��˵İ汾
			if(moduleVersionOfLand.equals(null)){
				String moduleVersionOfShip = shipVersionMap.get(key);
				//����Դ˲��ֶ�Ӧ�ڴ��˵İ汾����֤�Ƿ�Ϊ�գ�һ��ʼ�ڴ��˷�������ʱ��֤����
				//��������������
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
