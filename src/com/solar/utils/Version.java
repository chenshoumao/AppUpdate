package com.solar.utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
 
 

public class Version implements Comparable{
	private String main_Version;
	private final String base_Version = "release";
	private Date date_Version ;
	public String getMain_Version() {
		return main_Version;
	}
	
	public Version(String str){
		int firstIndex = str.indexOf("_");
		int secondIndex = str.indexOf("_", firstIndex+1);
		this.main_Version = str.substring(0,firstIndex);
		//this.base_Version = str.getBase_Version();
		SimpleDateFormat dateFormet = new SimpleDateFormat("yyyyMMdd");
		try {
			this.date_Version = dateFormet.parse(str.substring(secondIndex+1,str.length()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setMain_Version(String main_Version) {
		this.main_Version = main_Version;
	}
	public Date getDate_Version() {
		return date_Version;
	}
	public void setDate_Version(Date date_Version) {
		this.date_Version = date_Version;
	}
	public String getBase_Version() {
		return base_Version;
	}
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		Version version = (Version)arg0;
		
		int date_result = version.getDate_Version().compareTo(this.getDate_Version());
		if(date_result == 0){
			int length = Math.min(this.getMain_Version().length(), version.getMain_Version().length());
			String mian_Version_1 = this.getMain_Version().substring(0, length);
			String main_Version_2 = version.getMain_Version().substring(0, length);
			int base_result = main_Version_2.compareTo(mian_Version_1);
			 
			return base_result == 0? version.getMain_Version().length() > length ? 1:-1:base_result;
		}
		else
			return date_result;
	}
	
	public String toString(){
		SimpleDateFormat dateFormet = new SimpleDateFormat("yyyyMMdd");
		return this.getMain_Version() + "_" + this.getBase_Version() + "_" + dateFormet.format(this.getDate_Version());
	}
	
	public static void main(String[] args) throws ParseException {
		List<Version> list = new ArrayList<Version>();
		String path = "D:\\海图项目\\repositories";
		File file = new File(path);
		if(file.exists()){
			File[] fileList = file.listFiles();
			for(File fileIt:fileList){
				String name = fileIt.getName();
				String[] validateName = name.split("_");
				if(validateName.length == 3)
					list.add(new Version(name));
				else
					System.out.println("命名非法！:  " + name);
			}
		}
		
		Collections.sort(list);
		
		System.out.println("打印所有的版本的信息");
		for(Version verion:list){
			System.out.println(verion.toString());
		}
		
		System.out.println("打印 最新的版本");
		System.out.println(list.get(0).toString());
		
 
	 
		String path2 = "D:\\海图项目\\zip2";
		// 获取path1,path2的所有文件夹路径,文件的md5值put map
		Map<String, FileMd5> path1Map;
		try {
			path1Map = listDir(path);

			Map<String, FileMd5> path2Map = listDir(path2);
			// compare path1 map to path2 map 得到path2没有的文件夹和文件及其md5值不同的文件
			// List<FileMd5> compareFile1 = compareFile(path1Map, path2Map);
			// compare path2 map to path1 map 得到path1没有的文件夹和文件及其md5值不同的文件
			List<FileMd5> compareFile = compareFile(path2Map, path1Map);
			// 过滤结果
			// List<FileMd5> equalsFile = filterFile(compareFile1,
			// compareFile2);
			// 输出最终结果
			// printResult(equalsFile, compareFile1, compareFile2);
			printResult(compareFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean zipFile(long size) {
		boolean stateResult = false;
		// 压缩文件目录
		Zip zip = new Zip();
		
		 
		try {
		 
			stateResult = zip.zip("D:\\海图项目\\zip3", "D:\\海图项目\\zip4\\" + size + ".zip");
			File zipFile = new File("D:\\海图项目\\zip4\\" + size + ".zip");
			long zipSize = zipFile.length();
			zipFile.renameTo(new File("D:\\海图项目\\zip4\\" + zipSize + "_" + size + ".zip"));
			//System.out.println(zipFile.getName() + "," + zipFile.length());
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			stateResult = false;
		}
		return stateResult;
	}
	
	/**
	 * 打印结果 + 复制文件
	 */
	public static void printFile(List<FileMd5> fileMd5s) {
		CopyFileUtil copyUtil = new CopyFileUtil();

		boolean stateCopyResult = false;
		for (FileMd5 fileMd5 : fileMd5s) {
			System.out.println(fileMd5.getFile().getAbsolutePath() + " " + fileMd5.getMd5());

			String filePath = fileMd5.getFile().getAbsolutePath();
			String startTag = "D:\\海图项目\\zip2";
			int index = filePath.indexOf(startTag);
			if (index != -1) {
				index = startTag.length() + 1;
				filePath = filePath.substring(index, filePath.length());
			}
			stateCopyResult = copyUtil.copyFile(fileMd5.getFile().getAbsolutePath(),
					"D:\\海图项目\\zip3" + File.separator + filePath, true);
		}

		if (stateCopyResult) {
			// 遍历目录获取文件小
			File preZip = new File("D:\\海图项目\\zip3");
			FileSize fileSize = new FileSize();
			long size = fileSize.getFileSize(preZip);
			 

			// 压缩文件目录
			boolean stateResult = zipFile(size);
			
			
			 

		}

	}
	
	
	
	/**
	 * 打印结果
	 */
	public static void printResult(List<FileMd5> compareFile) {

		System.out.println("########################结果########################");
		printFile(compareFile);

	}
	
	/**
	 * 比较两个文件夹的不同
	 */
	public static List<FileMd5> compareFile(Map<String, FileMd5> path1Map, Map<String, FileMd5> path2Map) {
		List<FileMd5> list = new ArrayList<FileMd5>();
		for (String key : path1Map.keySet()) {
			FileMd5 fileMd5 = path1Map.get(key);
			FileMd5 _fileMd5 = path2Map.get(key);

			// 不管文件夹还是文件，只要path2没有则add到比较结果集中
			if (_fileMd5 == null) {
				list.add(fileMd5);
				continue;
			}

			// 文件的md5值不同则add到比较结果集中
			if (fileMd5.getFile().isFile() && !fileMd5.getMd5().equals(_fileMd5.getMd5())) {
				list.add(fileMd5);
			}
		}
		return list;
	}
	
	/**
	 * 获取指定路径下的所有文件路径
	 */
	private static List<File> listPath(File path) {
		List<File> list = new ArrayList<File>();
		File[] files = path.listFiles();
		Arrays.sort(files);
		for (File file : files) {
			
			
			if (file.isDirectory()) {
				List<File> _list = listPath(file);
				list.addAll(_list);
			}
			else
				list.add(file);
			System.out.println(file.getAbsolutePath());
			
		}
		return list;
	}
	
	/**
	 * 获取指定文件夹下的文件夹路径和文件md5值
	 */
	private static Map<String, FileMd5> listDir(String dir) throws IOException {
		Map<String, FileMd5> map = new HashMap<String, FileMd5>();
		File path = new File(dir);
		Object[] files = listPath(path).toArray();
		Arrays.sort(files);
		for (Object _file : files) {
			File file = (File) _file;
			// String key = file.getAbsolutePath().replaceAll("\\\\", "/");
			String key = file.getAbsolutePath();
			key = key.replaceAll(dir, "");// 去掉根目录

			int index = key.indexOf(dir);
			if (index != -1) {
				index = dir.length() + 1;
				key = key.substring(index, key.length());
			}
			// path = path.replaceAll("\\\\", "\");

			String md5 = "";// 文件夹的md5默认为空,即不比较md5值
			if (file.isFile()) {
				// String text = FileUtils.readFileToString(file);
				md5 = MD5.getFileMD5(file);
				// System.out.println(md5);
			}
			FileMd5 fileMd5 = new FileMd5(file, md5);
			map.put(key, fileMd5);
		}
		return map;
	}
	
	
}
