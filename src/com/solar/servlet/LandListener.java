package com.solar.servlet;

import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.solar.bean.Version;
import com.solar.dao.LandDao;
import com.solar.dao.impl.LandDaoImpl;
import com.solar.utils.VersionListUtil;

/**
 * Servlet implementation class LandListener
 */
@WebServlet("/LandListener")
public class LandListener extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private LandDaoImpl dao = new LandDaoImpl();
       
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
		String versionInfo = request.getParameter("ship");
		
		 Map<String, Object> map = dao.analysisVersion(versionInfo);
		 PrintWriter out = response.getWriter();
		 out.print(map);
		
	} 
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
