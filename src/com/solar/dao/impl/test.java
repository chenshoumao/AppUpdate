package com.solar.dao.impl;

import static java.lang.System.out;  

import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.nio.ByteBuffer;  
import java.nio.channels.FileChannel;  
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;  
  
  
public class test {  
      
   
    public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {  
    	//D:/海图项目/reposities/应用/1.0.0.1_app_release_20170718/web
		//D:/海图项目/reposities/应用/1.0.0.1_app_release_20170718/web/hello.jsp
    	
    	String temp = "D:/海图项目/reposities/应用/1.0.0.1_app_release_20170718/web";
    	
    	String source = "D:/海图项目/reposities/应用/1.0.0.1_app_release_20170718/web/hello.jsp";
    	
    	System.out.println(source.indexOf("D") > 0);
    }  
}  