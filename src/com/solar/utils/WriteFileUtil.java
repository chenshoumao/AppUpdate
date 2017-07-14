package com.solar.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;

public class WriteFileUtil {

	public void writeInfoToFile(String info, String url) { 
		try {
			File txt = new File(url);
			if (!txt.exists()) {
				txt.createNewFile();
			}
			byte bytes[] = new byte[512];
			bytes = info.getBytes(); // �¼ӵ�
			int b = info.length(); // ��
			FileOutputStream fos = new FileOutputStream(txt);
			fos.write(bytes, 0, b);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		ResourceBundle resource = ResourceBundle.getBundle("config/ship");
		String informUnzipFilePath = resource.getString("informUnzipFilePath");
		informUnzipFilePath = new String(informUnzipFilePath.getBytes("ISO-8859-1"), "utf-8");

		String str = "i love china!";
		File txt = new File(informUnzipFilePath);
		if (!txt.exists()) {
			txt.createNewFile();
		}
		byte bytes[] = new byte[512];
		bytes = str.getBytes(); // �¼ӵ�
		int b = str.length(); // ��
		FileOutputStream fos = new FileOutputStream(txt);
		fos.write(bytes, 0, b);
		fos.close();

	}

}