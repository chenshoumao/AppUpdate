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
		String path = "D:\\��ͼ��Ŀ\\repositories";
		File file = new File(path);
		if(file.exists()){
			File[] fileList = file.listFiles();
			for(File fileIt:fileList){
				String name = fileIt.getName();
				String[] validateName = name.split("_");
				if(validateName.length == 3)
					list.add(new Version(name));
				else
					System.out.println("�����Ƿ���:  " + name);
			}
		}
		
		Collections.sort(list);
		
		System.out.println("��ӡ���еİ汾����Ϣ");
		for(Version verion:list){
			System.out.println(verion.toString());
		}
		
		System.out.println("��ӡ ���µİ汾");
		System.out.println(list.get(0).toString());
		
 
	 
		String path2 = "D:\\��ͼ��Ŀ\\zip2";
		// ��ȡpath1,path2�������ļ���·��,�ļ���md5ֵput map
		Map<String, FileMd5> path1Map;
		try {
			path1Map = listDir(path);

			Map<String, FileMd5> path2Map = listDir(path2);
			// compare path1 map to path2 map �õ�path2û�е��ļ��к��ļ�����md5ֵ��ͬ���ļ�
			// List<FileMd5> compareFile1 = compareFile(path1Map, path2Map);
			// compare path2 map to path1 map �õ�path1û�е��ļ��к��ļ�����md5ֵ��ͬ���ļ�
			List<FileMd5> compareFile = compareFile(path2Map, path1Map);
			// ���˽��
			// List<FileMd5> equalsFile = filterFile(compareFile1,
			// compareFile2);
			// ������ս��
			// printResult(equalsFile, compareFile1, compareFile2);
			printResult(compareFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean zipFile(long size) {
		boolean stateResult = false;
		// ѹ���ļ�Ŀ¼
		Zip zip = new Zip();
		
		 
		try {
		 
			stateResult = zip.zip("D:\\��ͼ��Ŀ\\zip3", "D:\\��ͼ��Ŀ\\zip4\\" + size + ".zip");
			File zipFile = new File("D:\\��ͼ��Ŀ\\zip4\\" + size + ".zip");
			long zipSize = zipFile.length();
			zipFile.renameTo(new File("D:\\��ͼ��Ŀ\\zip4\\" + zipSize + "_" + size + ".zip"));
			//System.out.println(zipFile.getName() + "," + zipFile.length());
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			stateResult = false;
		}
		return stateResult;
	}
	
	/**
	 * ��ӡ��� + �����ļ�
	 */
	public static void printFile(List<FileMd5> fileMd5s) {
		CopyFileUtil copyUtil = new CopyFileUtil();

		boolean stateCopyResult = false;
		for (FileMd5 fileMd5 : fileMd5s) {
			System.out.println(fileMd5.getFile().getAbsolutePath() + " " + fileMd5.getMd5());

			String filePath = fileMd5.getFile().getAbsolutePath();
			String startTag = "D:\\��ͼ��Ŀ\\zip2";
			int index = filePath.indexOf(startTag);
			if (index != -1) {
				index = startTag.length() + 1;
				filePath = filePath.substring(index, filePath.length());
			}
			stateCopyResult = copyUtil.copyFile(fileMd5.getFile().getAbsolutePath(),
					"D:\\��ͼ��Ŀ\\zip3" + File.separator + filePath, true);
		}

		if (stateCopyResult) {
			// ����Ŀ¼��ȡ�ļ�С
			File preZip = new File("D:\\��ͼ��Ŀ\\zip3");
			FileSize fileSize = new FileSize();
			long size = fileSize.getFileSize(preZip);
			 

			// ѹ���ļ�Ŀ¼
			boolean stateResult = zipFile(size);
			
			
			 

		}

	}
	
	
	
	/**
	 * ��ӡ���
	 */
	public static void printResult(List<FileMd5> compareFile) {

		System.out.println("########################���########################");
		printFile(compareFile);

	}
	
	/**
	 * �Ƚ������ļ��еĲ�ͬ
	 */
	public static List<FileMd5> compareFile(Map<String, FileMd5> path1Map, Map<String, FileMd5> path2Map) {
		List<FileMd5> list = new ArrayList<FileMd5>();
		for (String key : path1Map.keySet()) {
			FileMd5 fileMd5 = path1Map.get(key);
			FileMd5 _fileMd5 = path2Map.get(key);

			// �����ļ��л����ļ���ֻҪpath2û����add���ȽϽ������
			if (_fileMd5 == null) {
				list.add(fileMd5);
				continue;
			}

			// �ļ���md5ֵ��ͬ��add���ȽϽ������
			if (fileMd5.getFile().isFile() && !fileMd5.getMd5().equals(_fileMd5.getMd5())) {
				list.add(fileMd5);
			}
		}
		return list;
	}
	
	/**
	 * ��ȡָ��·���µ������ļ�·��
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
	 * ��ȡָ���ļ����µ��ļ���·�����ļ�md5ֵ
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
			key = key.replaceAll(dir, "");// ȥ����Ŀ¼

			int index = key.indexOf(dir);
			if (index != -1) {
				index = dir.length() + 1;
				key = key.substring(index, key.length());
			}
			// path = path.replaceAll("\\\\", "\");

			String md5 = "";// �ļ��е�md5Ĭ��Ϊ��,�����Ƚ�md5ֵ
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
