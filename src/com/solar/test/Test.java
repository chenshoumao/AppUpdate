package com.solar.test;
import org.apache.log4j.Logger;

/**
 * @author linbingwen
 * @2015年5月18日9:14:21
 */
public class Test {
	//private static Logger logger = Logger.getLogger(Test.class); 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			throw new MyException("12312ff312");
		} catch (MyException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		
		// System.out.println("This is println message.");
		// 记录debug级别的信息
//		logger.debug("This is debug message.");
		// 记录info级别的信息
	//	logger.info("This is info message.");
		// 记录error级别的信息
//		logger.error("This is error message.");
	}
}
