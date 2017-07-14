package com.solar.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.solar.bean.Version;

public class VersionListUtil {
	
	//�汾�����淶����10.0.0_db_release_20170713�����ǿɷ�Ϊ�ĸ�����
	private static int length = 4;
	
	public List<Version> getVersionFromPath(String path){
		List<Version> list = new ArrayList<Version>(); 
		File file = new File(path);
		if(file.exists()){
			File[] fileList = file.listFiles();
			for(File fileIt:fileList){
				String name = fileIt.getName();
				String[] validateName = name.split("_");
				//�жϰ汾�������Ƿ�淶�����ǲ��淶��Ҫ��ʱ֪ͨ������Ա
				if(validateName.length == length)
					list.add(new Version(name));
				else{ 
					System.out.println("�����Ƿ���:  " + name);
					//֪ͨ�����Ա���汾���������淶
				}
			}
		} 
		Collections.sort(list);
		return list;
//		System.out.println("��ӡ���еİ汾����Ϣ");
//		for(Version verion:list){
//			System.out.println(verion.toString());
//		} 
//		System.out.println("��ӡ ���µİ汾");
//		System.out.println(list.get(0).toString()); 
		
		
		
	}
}
