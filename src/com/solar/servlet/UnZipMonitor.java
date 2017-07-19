package com.solar.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.SynchronousQueue;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServlet;

import com.solar.utils.CopyFileUtil;
import com.solar.utils.FileSize;
import com.solar.utils.ReadFile;
import com.solar.utils.ResouceBundleUtil;
import com.solar.utils.SQLExcute;
import com.solar.utils.TomcatUtil;
import com.solar.utils.UnzipUtil;
import com.solar.utils.WriteFileUtil;

public class UnZipMonitor extends HttpServlet implements Runnable {

	private static String updatePath = "D:/海图项目/zip4";
	private static String sourceUpdatePath = "";

	static FileSize fileSize = new FileSize();

	public static void monitor() {

		// 输出文件路径
		String outPath = "D:/海图项目/zip5";
		String filePath = ("D:/海图项目/通知文件/压缩文件");
		String filePath2 = ("D:\\海图项目\\通知文件\\解压文件");
		try {

			// 获取文件系统的WatchService对象
			WatchService watchService = FileSystems.getDefault().newWatchService();

			Paths.get(filePath).register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
			Paths.get(filePath2).register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

			// 如要监控子文件
			File file = new File(filePath);
			LinkedList<File> fList = new LinkedList<File>();
			fList.addLast(file);
			while (fList.size() > 0) {
				File f = fList.removeFirst();
				if (f.listFiles() == null)
					continue;
				for (File file2 : f.listFiles()) {
					if (file2.isDirectory()) {// 下一级目录
						fList.addLast(file2);
						// 依次注册子目录
						Paths.get(file2.getAbsolutePath()).register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
								StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
					}
				}
			}

			while (true) {
				// 获取下一个文件改动事件
				WatchKey key = watchService.take();
				for (WatchEvent<?> event : key.pollEvents()) {
					if ((event.kind().toString()).equals("ENTRY_CREATE") || (event.kind().toString()).equals("ENTRY_MODIFY")) {
						System.out.println(event.context() + " --> " + event.kind());
						String zipPath = traverseFolder2(filePath);
						Path path = (Path)key.watchable();
						System.out.println(path.toString());
						System.out.println(filePath2);
						Thread.sleep(3000);
						if((path.toString()).equals(filePath2)){
							//更新
							CopyFileUtil copyFileUtil = new CopyFileUtil();
							ResouceBundleUtil resourceBundle = new ResouceBundleUtil(); 
							String webUrl = resourceBundle.getInfo("config/ship","app");  
							String unzipPath = resourceBundle.getInfo("config/ship","unzipPath");
							copyFileUtil.copyDirectory(unzipPath, webUrl, true);
							
							//判断增量文件中是否有sql文件
							File fileScan = new File(unzipPath+File.separator + "db");
							if(fileScan.exists()){
								File[] sqlFileList = fileScan.listFiles();
								for(File sqlFile:sqlFileList){
									//更新数据库
									SQLExcute sqlExcute = new SQLExcute();
									sqlExcute.updateDB(sqlFile);
									
								}
							} 
							
							//重启tomcat7
							TomcatUtil tomcatUtil = new TomcatUtil();
							tomcatUtil.stopTomcat();
						}
						//解压
						else{
							unzip(zipPath, outPath);
						    WriteFileUtil writeFileUtil = new WriteFileUtil();
						    ResouceBundleUtil resourceBundle = new ResouceBundleUtil(); 
						    String informUnzipFilePath = resourceBundle.getInfo("config/ship","informUnzipFilePath"); 
						    String unzipPath = resourceBundle.getInfo("config/ship","unzipPath"); 
						    writeFileUtil.writeInfoToFile(unzipPath, informUnzipFilePath);
						}
						// updateFile("","");
					}
					 System.out.println(event.kind() +"," + ((
					 event.kind().toString()).equals("ENTRY_CREATE")));

				}
				// 重设WatchKey
				boolean valid = key.reset();
				// 如果重设失败，退出监听
				if (!valid) {
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public static String traverseFolder2(String path) {

		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				System.out.println("文件夹是空的!");
				return "";
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						System.out.println("文件夹:" + file2.getAbsolutePath());
						return file2.getAbsolutePath();
						// traverseFolder2(file2.getAbsolutePath());
					} else {
						System.out.println("文件:" + file2.getAbsolutePath());
						return file2.getAbsolutePath();
					}
				}
			}
		} else {
			System.out.println("文件不存在!");
			return "";
		}
		return "";
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		ResourceBundle resource = ResourceBundle.getBundle("config/ship");
		String zipPath = resource.getString("informZipFilePath");
		zipPath = new String(zipPath.getBytes("ISO-8859-1"),"utf-8");
		System.out.println(zipPath);
		
		ReadFile readFile = new ReadFile();
		UnzipUtil unzipUtil = new UnzipUtil();
		try {
			System.out.println(readFile.readLastLine(new File(zipPath), "gbk"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static boolean unzip(String sourcePath, String outPath) throws UnsupportedEncodingException {
		ResourceBundle resource = ResourceBundle.getBundle("config/ship");
		String zipPath = resource.getString("informZipFilePath");
		zipPath = new String(zipPath.getBytes("ISO-8859-1"),"utf-8");
		ReadFile readFile = new ReadFile();
		UnzipUtil unzipUtil = new UnzipUtil();
		try {
			sourcePath = readFile.readLastLine(new File(zipPath), "gbk");
			String des = resource.getString("unzipPath");
			des = new String(des.getBytes("ISO-8859-1"),"utf-8");
			unzipUtil.unzip(sourcePath, des);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

 

	public static void updateFile(String resourcePath, String desPath) {
		File file = new File(resourcePath);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				System.out.println("文件夹是空的!");

			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						System.out.println("文件夹:" + file2.getAbsolutePath());

						// traverseFolder2(file2.getAbsolutePath());
					} else {
						System.out.println("文件:" + file2.getAbsolutePath());

					}
				}
			}
		} else {
			System.out.println("文件不存在!");

		}
	}

	public void init() {
		UnZipMonitor unzip = new UnZipMonitor();
		Thread thread = new Thread(unzip);
		thread.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(12223);
		monitor();

	}

//	public static void main(String[] args) {
//		monitor();
//	}

}