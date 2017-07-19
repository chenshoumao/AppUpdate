package com.solar.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.PreparedStatement;
import com.solar.utils.ConnectUtil;

/**
 * Servlet implementation class ShipSizeServlet
 */
@WebServlet("/ShipSizeServlet")
public class ShipSizeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShipSizeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		ConnectUtil connectUtil = new ConnectUtil();
		Connection conn = connectUtil.getConn();
		String sql = "select * from ship";
		PreparedStatement ps;
		String name = "";
		String size = "";
		List list = new ArrayList();
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql); 
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				name = rs.getString(1);
				size = rs.getString(2);
				list.add(name);
				list.add(size);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PrintWriter out = response.getWriter();
		out.print(list);
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
