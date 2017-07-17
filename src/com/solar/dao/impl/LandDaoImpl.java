package com.solar.dao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.bean.Version;
import com.solar.dao.LandDao;
import com.solar.utils.CopyFileUtil;
import com.solar.utils.FileMd5;
import com.solar.utils.FileSize;
import com.solar.utils.MD5;
import com.solar.utils.ReadFile;
import com.solar.utils.VersionListUtil;
import com.solar.utils.Zip;

public class LandDaoImpl implements LandDao {

	// 版本命名规范，如10.0.0_db_release_20170713，可是可分为四个部分
	private static int length = 4;

	private final String VERSION = "version.txt";

	private static ResourceBundle resource = ResourceBundle.getBundle("config/path");

	
	
	@Override
	public Map<String, Object> analysisVersion(String versionInfo) {
		// TODO Auto-generated method stub
		// 解析各个版本，如你图，数据库，web 以及 想要更新的对应的版本
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			Map<String, Map<String, String>> map;
			map = mapper.readValue(versionInfo, HashMap.class);

			// 用List 集合封装 船端的版本信息
			Map<String, String> shipVersionMap = new HashMap<String, String>();
			shipVersionMap = map.get("ship");

			Set<String> set = shipVersionMap.keySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				// 获取此部分在船端的版本
				String moduleVersionOfShip = shipVersionMap.get(key);
				// 非空，则获取对应在岸端的版本
				if (!moduleVersionOfShip.equals(null)) {
					// 获取在船端的最新版本
					List<Version> keyList = getVersionFromPath(resource.getString(key));
					String upToDateVersion = keyList.get(0).toString();
					resultMap = versionFilter(key, shipVersionMap, upToDateVersion);
					// 生成增量升级包
					if ((boolean) resultMap.get("result")) {
						resultMap.clear();
						// 生成对应的增量包
						resultMap.put("result",generateIncrement(key, moduleVersionOfShip, upToDateVersion)); 
					} else {
						return resultMap;
					}

				}
			}

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @author 陈守貌
	 * @time 2017-07-13
	 * @function 获取版本的集合信息
	 * @param path
	 *            版本的路径
	 */
	@Override
	public List<Version> getVersionFromPath(String path) {
		// TODO Auto-generated method stub
		List<Version> list = new ArrayList<Version>();
		try {
			path = new String(path.getBytes("ISO-8859-1"), "utf-8");
		
		
		File file = new File(path);
		if (file.exists()) {
			File[] fileList = file.listFiles();
			for (File fileIt : fileList) {
				String name = fileIt.getName();
				String[] validateName = name.split("_");
				// 判断版本的命名是否规范，若是不规范，要及时通知技术人员
				if (validateName.length == length)
					list.add(new Version(name));
				else {
					System.out.println("命名非法！:  " + name);
					// 通知相关人员，版本库命名不规范
				}
			}
		}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.sort(list);
		return list;
	}

	/**
	 * @author 陈守貌
	 * @time 2017-07-13
	 * @function 生成对应的版本增量升级包，并且放在指定目录之下
	 * @param key
	 *            对应的更新部分名称
	 * @param moduleVersionOfShip
	 *            船端的版本
	 * @param moduleVersionOfLand
	 *            岸端的版本
	 */
	public boolean generateIncrement(String key, String moduleVersionOfShip, String moduleVersionOfLand) {
		// TODO Auto-generated method stub
		boolean result = false;
		// 旧版本（即船端的版本）
		String oldVersionPath = resource.getString(key) + "/" + moduleVersionOfShip + "/";
		
		// 新版本（即岸端最新版本）
		String newVersionPath = resource.getString(key) +"/"+ moduleVersionOfLand + "/";
		try {
			oldVersionPath = new String(oldVersionPath.getBytes("ISO-8859-1"), "utf-8");
			newVersionPath = new String(newVersionPath.getBytes("ISO-8859-1"), "utf-8");
		
		
		    Map<String, FileMd5> oldVersionMap;
		
			// 用md5 标示旧版本的文件
			oldVersionMap = listDir(oldVersionPath);
			// 用md5 标示新版本的文件
			Map<String, FileMd5> newVersionMap = listDir(newVersionPath);

			// 比较两版本的文件，将增量结果储存在 compareFile 的集合中
			List<FileMd5> compareFile = compareFile(newVersionMap, oldVersionMap);

			// 复制增量到一个临时目录
			copyFile(key,compareFile, newVersionPath);
			result = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	/**
	 * 打印结果 + 复制文件
	 */
	public static void copyFile(String key,List<FileMd5> fileMd5s, String startTag) {
		CopyFileUtil copyUtil = new CopyFileUtil();

		// 获取指定的临时目录
		String tempPath = resource.getString("tempPath");
		try {
			tempPath = new String(tempPath.getBytes("ISO-8859-1"), "utf-8");

			boolean stateCopyResult = false;
			for (FileMd5 fileMd5 : fileMd5s) {
				System.out.println(fileMd5.getFile().getAbsolutePath() + " " + fileMd5.getMd5());

				String filePath = fileMd5.getFile().getAbsolutePath();
				filePath = filePath.replaceAll("\\\\", "/");
				// String startTag = "D:\\海图项目\\zip2";
				int index = filePath.indexOf(startTag);
				if (index != -1) {
					index = startTag.length();
					filePath = filePath.substring(index, filePath.length());
				}
				stateCopyResult = copyUtil.copyFile(fileMd5.getFile().getAbsolutePath(),
						tempPath + File.separator + key + File.separator + filePath, true);
				// 如果复制文件出现差错，则写倒日志中去
				if (!stateCopyResult) {
					// 写到文件中
				}
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 打包文件，形成一个增量包
		Zip zip = new Zip();
		String sourcePath = resource.getString("tempPath"); 
		String outPutZipPath = resource.getString("zipPath");
		//转码
		try {
			sourcePath =  new String(sourcePath.getBytes("ISO-8859-1"), "utf-8");
			outPutZipPath =  new String(outPutZipPath.getBytes("ISO-8859-1"), "utf-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
		zip.zip(sourcePath, outPutZipPath);

		// if (stateCopyResult) {
		// // 遍历目录获取文件小
		// File preZip = new File("D:\\海图项目\\zip3");
		// FileSize fileSize = new FileSize();
		// long size = fileSize.getFileSize(preZip);
		//
		//
		// // 压缩文件目录
		// boolean stateResult = zipFile(size);
		//
		//
		//
		//
		// }

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
			// System.out.println(zipFile.getName() + "," + zipFile.length());
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			stateResult = false;
		}
		return stateResult;
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
	 * 获取指定文件夹下的文件夹路径和文件md5值
	 */
	private static Map<String, FileMd5> listDir(String dir) throws IOException {
		Map<String, FileMd5> map = new HashMap<String, FileMd5>();
		File path = new File(dir);
		Object[] files = listPath(path).toArray();
		Arrays.sort(files);
		for (Object _file : files) {
			File file = (File)_file;
			// String key = file.getAbsolutePath().replaceAll("\\\\", "/");
			String key = file.getAbsolutePath();
			key = key.replaceAll("\\\\", "/");
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
			} else
				list.add(file);
			System.out.println(file.getAbsolutePath());

		}
		return list;
	}

	/**
	 * @author 陈守貌
	 * @time 2017-07-13
	 * @function 判断版本间的差异，是否有增量，或者是否存在版本的依赖
	 * @param key
	 *            对应的更新部分名称
	 * @param moduleVersionOfShip
	 *            船端的版本
	 * @param moduleVersionOfLand
	 *            岸端的版本
	 */
	public Map<String, Object> versionFilter(String key, Map<String, String> shipVerionMap,
			String moduleVersionOfLand) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		String moduleVersionOfShip = shipVerionMap.get(key);
		if (!moduleVersionOfLand.equals(moduleVersionOfShip)) {
			// 检查是否存在依赖
			String path = resource.getString(key) + File.separator + moduleVersionOfLand + File.separator + VERSION;
			try {
				path = new String(path.getBytes("ISO-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ReadFile readFile = new ReadFile();
			map = readFile.readFileByLines(path);
			Set set = map.keySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String mapKey = (String) it.next();
				// 本地依赖
				String localDep = (String) map.get(mapKey);
				String shipVersion = shipVerionMap.get(mapKey);
				if (localDep.contains(shipVersion)) {
					continue;
				}
				map.clear();
				String responseInfo = "版本存在依赖，需要把" + mapKey + "更新";
				map.put("info", responseInfo);
				map.put("result", false);
				return map;
			}

			map.put("result", true);
		} else {
			// 版本一致，无需更新
			String responseInfo = "版本一致，无需更新";
			map.put("info", responseInfo);
			map.put("result", false);
		}

		return map;
	}

	public static void sfdsd() {
		String path = "D://海图//版本库//数据库//10.0.0_db_release_20170713//dep.txt";
		File file = new File(path);
		String result = "";
		BufferedReader reader = null;
		try {
			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				System.out.println("line " + line + ": " + tempString);
				String[] str = tempString.split(":");
				if (str.length == 2)
					result += tempString;
				else {
					// 通知技术人员 命名不规范
				}
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		System.out.println(result);
	}

}
