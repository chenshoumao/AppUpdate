package com.solar.dao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.DebugGraphics;

import org.apache.log4j.Logger;
import org.apache.log4j.pattern.LogEvent;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.PreparedStatement;
import com.solar.bean.Version;
import com.solar.dao.LandDao;
import com.solar.test.MyException;
import com.solar.utils.ConnectUtil;
import com.solar.utils.CopyFileUtil;
import com.solar.utils.FileMd5;
import com.solar.utils.FileSize;
import com.solar.utils.MD5;
import com.solar.utils.ReadFile;
import com.solar.utils.ResouceBundleUtil;
import com.solar.utils.SQLExcute;
import com.solar.utils.VersionListUtil;
import com.solar.utils.WriteFileUtil;
import com.solar.utils.Zip;

public class LandDaoImpl implements LandDao {

	private static Logger logger = Logger.getLogger(LandDaoImpl.class);
	
	// 版本命名规范，如10.0.0_db_release_20170713，可是可分为四个部分
	private static int length = 4;

	private final String VERSION = "depend.txt";

	private final String HAITU = "haitu";
	private final String HAITU_VALUE = "海图版本";
	private final String DITU = "ditu";
	private final String DITU_VALUE = "底图版本";

	private static ResourceBundle resource = ResourceBundle.getBundle("config/land");

	@Override
	public Map<String, Object> analysisVersion(String versionInfo) {
		// TODO Auto-generated method stub

		logger.debug("岸端 第二步 ：");
		// 解析各个版本，如你图，数据库，web 以及 想要更新的对应的版本

		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 先声明一个读取配置文件的工具
		ResouceBundleUtil bundleUtil = new ResouceBundleUtil();
		try {
			logger.debug("	将数据解析成Map集合");
			Map<String,Object> map;
			map = mapper.readValue(versionInfo, HashMap.class);

			
			// 先把要更新的组件的关键字存进 list 集合
			List<String> keyList = new ArrayList<String>();
			logger.debug("	将Map的Key值存在String[] 中");
			String[] keyStr = ((String) map.get("toUpdate")).split(",");
			for(String str:keyStr)
				keyList.add(str);

			int index = 0;
			boolean needDb = false;
			for (String key: keyList) {
				    logger.debug("	岸端对"+key+ "进行版本的分析");
					if (key.equals("db") && !needDb)
						continue;
					logger.debug("	" + key + "在船端的版本是：");
					// 获取此部分在船端的版本
					String moduleVersionOfShip = (String) map.get(key);
					logger.debug("	" + moduleVersionOfShip); 
					// 声明插入数据库更新日志的语句
					String sql = "insert into update_logs values(?,?,?,?,?,?,?)";
					ConnectUtil connectUtil = new ConnectUtil();
					Connection conn = connectUtil.getConn();
					try {
						PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
						ps.setString(1, "localhost");
						ps.setString(2, bundleUtil.getInfo("config/module", key));
						ps.setString(3, moduleVersionOfShip);
						SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = new Date();
						ps.setString(5, simpleFormat.format(date));
						
						// 非空，则获取对应在岸端的版本
						if (!moduleVersionOfShip.equals(null)) { 
							logger.debug("	获取岸端中，"+ key + "中的最新版本");
							// 获取在船端的最新版本
							String upToDateVersion = getVersionFromPath(resource.getString(key)).get(0).toString();
							ps.setString(4, upToDateVersion);
							logger.debug("	"+ upToDateVersion); 
							logger.debug("	版本的分析 看看存不存在依赖"); 
							// 版本的分析 看看存不存在依赖
							resultMap = versionFilter(key, keyList,map, moduleVersionOfShip, upToDateVersion);
							logger.debug("	分析结束");
							// 生成增量升级包
							if ((boolean) resultMap.get("result") || (boolean) resultMap.get("needDb")) {
								logger.debug("岸端第三步");
								needDb = (boolean) resultMap.get("needDb");
								resultMap.clear();
								logger.debug(" 生成对应的增量包");
								// 生成对应的增量包
								resultMap.put("result", generateIncrement(key, moduleVersionOfShip, upToDateVersion));
								resultMap.put(moduleVersionOfShip, "打包成功");
								list.add(resultMap);

								// 到此步骤已经可以算是更新成功的了，将更新语句的状态设置为 1，1 代表成功的意思
								ps.setInt(6, 1);

								String description = "打包成功" + (needDb ? "系统同时打包了最新版本的数据库" : "");
								ps.setString(7, description);
							} else {

								ps.setInt(6, 0);
								ps.setString(7, (String) resultMap.get("info"));
								list.add(resultMap);
							}

						} else {
							
							ps.setInt(6, 0);
							ps.setString(7, "在船端的版本是空的");
							list.add(resultMap);
							throw new MyException("" + key + "在船端的版本是空的！"); 
						}
						ps.execute();
						ps.close();
						connectUtil.closeConn(conn);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new MyException("数据解析错误 ：" + e);
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
		logger.debug("	列出在岸端的所有版本"); 
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
						logger.debug("	命名非法！:  " + name); 
						// 通知相关人员，版本库命名不规范
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			throw new MyException(e.toString());
		}
		Collections.sort(list);
		logger.debug("	" + list); 
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
		logger.debug("	开始生成增量包"); 
		boolean result = false;
		// 旧版本（即船端的版本）
		String oldVersionPath = resource.getString(key) + "/" + moduleVersionOfShip + "/";

		// 新版本（即岸端最新版本）
		String newVersionPath = resource.getString(key) + "/" + moduleVersionOfLand + "/";
		try {
			oldVersionPath = new String(oldVersionPath.getBytes("ISO-8859-1"), "utf-8");
			newVersionPath = new String(newVersionPath.getBytes("ISO-8859-1"), "utf-8");
			logger.debug("	两版本的位置：" + oldVersionPath + "," + newVersionPath); 
			Map<String, FileMd5> oldVersionMap;
			logger.debug("	用md5 标示旧版本的文件");
			// 用md5 标示旧版本的文件
			oldVersionMap = listDir(oldVersionPath);
			logger.debug("	用md5 标示新版本的文件");
			// 用md5 标示新版本的文件
			Map<String, FileMd5> newVersionMap = listDir(newVersionPath);
			logger.debug("	比较两版本的文件，将增量结果储存在 compareFile 的集合中");
			// 比较两版本的文件，将增量结果储存在 compareFile 的集合中
			List<FileMd5> compareFile = compareFile(newVersionMap, oldVersionMap);
			logger.debug("	复制增量到一个临时目录");
			// 复制增量到一个临时目录
			copyFile(key, moduleVersionOfShip, moduleVersionOfLand, compareFile, newVersionPath);
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
	 * 
	 * @param moduleVersionOfLand
	 * @param moduleVersionOfShip
	 */
	public static void copyFile(String key, String moduleVersionOfShip, String moduleVersionOfLand,
			List<FileMd5> fileMd5s, String startTag) {
		CopyFileUtil copyUtil = new CopyFileUtil();
		logger.debug("	获取指定的临时目录"); 
		// 获取指定的临时目录
		String tempPath = resource.getString("tempPath");
		
		try {
			tempPath = new String(tempPath.getBytes("ISO-8859-1"), "utf-8");
			logger.debug("		tempPath"); 
			boolean stateCopyResult = false;
			for (FileMd5 fileMd5 : fileMd5s) {
			

				String filePath = fileMd5.getFile().getAbsolutePath();
				filePath = filePath.replaceAll("\\\\", "/");
				// String startTag = "D:\\海图项目\\zip2";
				// D:\海图项目\reposities\应用\1.0.0.1_app_release_20170718\web
				// D:\海图项目\reposities\应用\1.0.0.1_app_release_20170718\web\hello.jsp
				String destDir = "";
				String sourceDir = fileMd5.getFile().getAbsolutePath();
				String temp = startTag;
				String notApp = "";
				// D:/海图项目/reposities/应用/1.0.0.1_app_release_20170718/config/3212.txt
				// D:/海图项目/reposities/应用/1.0.0.1_app_release_20170718/
				if (key.equals("app")) {
					temp = startTag + "web";
					sourceDir = sourceDir.replaceAll("\\\\", "/");
					temp = temp.replaceAll("\\\\", "/");
					if (sourceDir.indexOf(temp) < 0)
						temp = startTag;
					if (sourceDir.indexOf(startTag + "config") != -1) {
						temp = startTag + "config";
						notApp = "config";
					} // D:\海图项目\reposities\应用\1.0.0.0_app_release_20170713\config
						// D:\海图项目\reposities\应用\1.0.0.0_app_release_20170713\web\WEB-INF\classes
				} else {
					notApp = key;
				}
				int index = filePath.indexOf(temp);
				if (index != -1) {
					index = temp.length();
					filePath = filePath.substring(index, filePath.length());

					if (notApp.equals("config")) {
						destDir = tempPath + "/WEB-INF/classes/config" + filePath;
					} else if (notApp != "")
						destDir = tempPath + File.separator + notApp + File.separator + filePath;
					else
						destDir = tempPath + File.separator + filePath;
				}
				logger.debug("	复制增量文件的信息"); 
				logger.debug("		源文件"+ sourceDir); 
				logger.debug("		目标地址"+ destDir); 
				stateCopyResult = copyUtil.copyFile(sourceDir, destDir, true);
				// 如果复制文件出现差错，则写倒日志中去
				if (!stateCopyResult) {
					// 写到文件中
					throw new MyException("		文件复制失败");
				}
			}
			logger.debug("	创建一个文件 用来标明增量文件是存在于哪两个版本之间"); 
			// 创建一个文件 用来标明增量文件是存在于哪两个版本之间
			String divisionFilePath = tempPath + "/" + (key.equals("app") ? "division.txt" : key + "/division.txt");
			logger.debug("		文件是"+ divisionFilePath); 
			File file = new File(divisionFilePath);
			WriteFileUtil writeFileUtil = new WriteFileUtil();
			List list = new ArrayList();
			Map<String, String> map = new HashMap<String, String>();
			map.put("from", moduleVersionOfShip);
			map.put("to", moduleVersionOfLand);
			list.add(map);
			ObjectMapper mapper = new ObjectMapper();
			String json;
			try {
				json = mapper.writeValueAsString(list);
				writeFileUtil.writeInfoToFile(json, divisionFilePath);
				logger.debug("	文件内容是"+ json); 
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new MyException(e.toString());
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("岸端第四步");
		logger.debug("	打包文件，形成一个增量压缩包");
		// 打包文件，形成一个增量包
		Zip zip = new Zip();
		ResouceBundleUtil bundleUtil = new ResouceBundleUtil();
		String sourcePath = bundleUtil.getInfo("config/land","tempPath");
		String outPutZipPath = bundleUtil.getInfo("config/land","zipPath");
		logger.debug("	源文件路径：" + sourcePath);
		logger.debug("	增量包路径： " + outPutZipPath);
		zip.zip(sourcePath, outPutZipPath);

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
			File file = (File) _file;
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
	/*            船端的版本
	 * @param moduleVersionOfLand
	 *            岸端的版本
	 */
	public Map<String, Object> versionFilter(String key, List<String> keyList, Map<String, Object> allVersion,String shipVersion,
			String moduleVersionOfLand) {
		// TODO Auto-generated method stub
		logger.debug("	版本的分析开始。。。"); 
		Map<String, Object> map = new HashMap<String, Object>();
		if (!moduleVersionOfLand.equals(shipVersion)) {
			logger.debug("	检查是否存在依赖。。。"); 
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

			List<Map<String, Object>> versionDepentList = (List<Map<String, Object>>) map.get("depend");

			boolean validateState = false;

			outer: if (versionDepentList.size() > 0) {
				String returnKey = "";
				String dependKey = "";
				String dependValue = "";
				for (Map<String, Object> versionDependMap : versionDepentList) {
					Iterator it = versionDependMap.keySet().iterator();
					dependKey = (String) it.next();
					dependValue = (String) versionDependMap.get(dependKey);
					if (dependKey.equals("db")) {
						validateState = true;
						continue;
					}
					if (keyList.contains(dependKey)) {
						validateState = true;
						continue;
					} 
					if (dependValue.contains((String)allVersion.get(dependKey))) {
						validateState = true;
						continue;
					}
					
					//获取在岸端数据库保存的对应船端的版本
					String updateDateShipVersion =  SQLExcute.getVersionByKey(dependKey, "ship_version");
					if(dependValue.equals(updateDateShipVersion)){
						validateState = true;
						continue;
					}
					
					returnKey += dependKey + ",";
					validateState = false;
				}
				if (validateState) {
					break outer;
				} 
				map.clear(); 
				map.put("needDb", false);  
				String responseInfo = "版本存在依赖，需要把"; 
				if (returnKey.contains(HAITU))
					responseInfo += " " + HAITU_VALUE;
				if (returnKey.contains(DITU))
					responseInfo += " " + DITU_VALUE;
				responseInfo += " 更新";
				map.put("info", responseInfo);
				map.put("result", false);
				return map;
			}
			map.put("needDb", validateState);
			map.put("result", true);
		} else {
			logger.debug("	版本一致，无需更新"); 
			// 版本一致，无需更新
			String responseInfo = "版本一致，无需更新";
			map.put("needDb", false);
			map.put("info", responseInfo);
			map.put("result", false);
		}
		logger.debug("	检查结果：" + map); 
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
