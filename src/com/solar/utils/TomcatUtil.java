package com.solar.utils;

import java.io.IOException;

public class TomcatUtil {
	public static void main(String[] args) {
		Runtime r = Runtime.getRuntime();
		try {
			Process p = r.exec("net start tomcat7");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void startTomcat(){
		Runtime r = Runtime.getRuntime();
		try {
			Process p = r.exec("net start tomcat7");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopTomcat(){
		Runtime r = Runtime.getRuntime();
		try {
			Process p = r.exec("net stop tomcat7");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void restartTomcat(){
		this.stopTomcat();
		try {
			Thread.sleep(4000);
			this.startTomcat();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
