package com.solar.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
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

import com.solar.utils.FileSize;
import com.solar.utils.ReadFile;
import com.solar.utils.UnzipUtil;

public class UnZipMonitor extends HttpServlet implements Runnable {

	private static String updatePath = "D:/海图项目/zip4";
	private static String sourceUpdatePath = "";

	static FileSize fileSize = new FileSize();

	public static void monitor() {

		// 输出文件路径
		String outPath = "D:/海图项目/zip5";
		String filePath = ("D:/海图项目/通知文件/压缩文件");
		String filePath2 = ("D:/海图项目/通知文件/解压文件");
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
						Thread.sleep(3000);
						if(event.context().equals(filePath2)){
							//更新
						}
						//解压
						else
							unzip(zipPath, outPath);
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

//	public static boolean unzip(String sourcePath, String outPath) {
//		boolean stateResult = false;
//		
//		long zipFileSize = 0;
//		try {
//			
//			
//
//			// 文件输入流
//			FileInputStream fin = null;
//
//			
//			boolean runState = false;
//			while (!runState) {
//				
//				
//				try { 
//					fin = new FileInputStream(sourcePath);
//				File fileJudge = new File(sourcePath);
//				String name = "";
//				name = fileJudge.getName(); 
//				long zipSize = Long.valueOf(name.substring(0, name.indexOf("_")));
//				zipFileSize = Long.valueOf(name.substring(name.indexOf("_") + 1, name.indexOf(".")));
//				long itSize = fileJudge.length();
//				System.out.println(itSize + ",,," + zipSize);
//				if (itSize == zipSize)
//					break;
//
//				System.out.println("................");
//				 
//				runState = true;
//				} catch (Exception e) {
//					// TODO: handle exception
//					System.out.println(e);
//					runState = false;
//					Thread.sleep(4000);
//				}
//			}
//			
//			
//			// 需要维护所读取数据校验和的输入流。校验和可用于验证输入数据的完整性
//			CheckedInputStream checkIn = new CheckedInputStream(fin, new CRC32());
//			// 指定编码 否则会出现中文文件解压错误
//			Charset gbk = Charset.forName("GBK");
//			// zip格式的输入流
//			ZipInputStream zin = new ZipInputStream(checkIn, gbk);
//
//			// 遍历压缩文件中的所有压缩条目
//			ZipEntry zinEntry;
//
//			while ((zinEntry = zin.getNextEntry()) != null) {
//				System.out.println(zinEntry);
//				File targetFile = new File(outPath + File.separator + zinEntry.getName());
//
//				System.out.println("..." + targetFile + "   " + targetFile.getParentFile());
//
//				sourceUpdatePath = targetFile.toString();
//				if (!targetFile.getParentFile().exists()) {
//					System.out.println("..." + targetFile + "   " + targetFile.getParentFile());
//					targetFile.getParentFile().mkdirs();
//				}
//				if (zinEntry.isDirectory()) {
//					targetFile.mkdirs();
//				} else {
//					FileOutputStream fout = new FileOutputStream(targetFile);
//					byte[] buff = new byte[1024];
//					int length;
//					while ((length = zin.read(buff)) > 0) {
//						fout.write(buff, 0, length);
//					}
//					fout.close();
//				}
//			}
//
//			zin.close();
//			fin.close();
//			System.out.println(checkIn.getChecksum().getValue());
//			checkIn.close();
//
//			Thread.sleep(4000);
//			File file = new File(sourcePath);
//
//			if (file.exists())
//				file.delete();
//			File afterUnZip5 = new File("D:\\海图项目\\zip5");
//
//			long afterUnZip5Size = fileSize.getFileSize(afterUnZip5);
//			long preUnZip5Size = zipFileSize;
//
//			boolean resultState = preUnZip5Size == afterUnZip5Size;
//			if (resultState){
//				System.out.println("解压数据成功");
//				stateResult = true;
//			}
//			else{
//				System.out.println("解压数据损失");
//				stateResult = false;
//			}
//
//		} catch (Exception e) {
//			// TODO: handle exception
//			stateResult = false;
//			System.out.println(e);
//		}
//		return stateResult;
//		// System.exit(0);
//	}

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