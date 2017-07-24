package com.solar.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.PreparedStatement;
import com.solar.dao.impl.ShipDaoImpl;
import com.solar.utils.ConnectUtil;
import com.solar.utils.ResouceBundleUtil;

/**
 * Servlet implementation class UpdateShipLogs
 */
@WebServlet("/UpdateShipLogs")
public class UpdateShipLogs extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateShipLogs() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() {
    	UpdateLogs();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void UpdateLogs(){
		// TODO Auto-generated method stub
	 
		
		ResouceBundleUtil bundleUtil = new ResouceBundleUtil();
		String data ="app,haitu,ditu,db";
		String[] dataList = data.split(","); 
		ShipDaoImpl dao = new ShipDaoImpl();
		Map<String,Object> localVersionMap = dao.getShipVersion(dataList);
	 
		try {
			
			int count = 0;
			for (String key : dataList) {
				String keyInfo = bundleUtil.getInfo("config/module", key);
				String sql = "update ship_update_logs set new_version = ? , update_time = ? , is_over= 1 where update_type =? and original_version != ? and is_over = 0";
				ConnectUtil connectUtil = new ConnectUtil();
				Connection conn = connectUtil.getConn();
				PreparedStatement ps; 
				ps = (PreparedStatement) conn.prepareStatement(sql);
				
				String version = (String) localVersionMap.get(key);
				 
				ps.setString(1, version);
				SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				ps.setString(2, simpleFormat.format(date)); 
				ps.setString(3, keyInfo);
				ps.setString(4, version);
				ps.execute();
				ps.close();
				connectUtil.closeConn(conn);
			}

		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
