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

	// �汾�����淶����10.0.0_db_release_20170713�����ǿɷ�Ϊ�ĸ�����
	private static int length = 4;

	private final String VERSION = "version.txt";

	private static ResourceBundle resource = ResourceBundle.getBundle("config/path");

	
	
	@Override
	public Map<String, Object> analysisVersion(String versionInfo) {
		// TODO Auto-generated method stub
		// ���������汾������ͼ�����ݿ⣬web �Լ� ��Ҫ���µĶ�Ӧ�İ汾
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			Map<String, Map<String, String>> map;
			map = mapper.readValue(versionInfo, HashMap.class);

			// ��List ���Ϸ�װ ���˵İ汾��Ϣ
			Map<String, String> shipVersionMap = new HashMap<String, String>();
			shipVersionMap = map.get("ship");

			Set<String> set = shipVersionMap.keySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				// ��ȡ�˲����ڴ��˵İ汾
				String moduleVersionOfShip = shipVersionMap.get(key);
				// �ǿգ����ȡ��Ӧ�ڰ��˵İ汾
				if (!moduleVersionOfShip.equals(null)) {
					// ��ȡ�ڴ��˵����°汾
					List<Version> keyList = getVersionFromPath(resource.getString(key));
					String upToDateVersion = keyList.get(0).toString();
					resultMap = versionFilter(key, shipVersionMap, upToDateVersion);
					// ��������������
					if ((boolean) resultMap.get("result")) {
						resultMap.clear();
						// ���ɶ�Ӧ��������
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
	 * @author ����ò
	 * @time 2017-07-13
	 * @function ��ȡ�汾�ļ�����Ϣ
	 * @param path
	 *            �汾��·��
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
				// �жϰ汾�������Ƿ�淶�����ǲ��淶��Ҫ��ʱ֪ͨ������Ա
				if (validateName.length == length)
					list.add(new Version(name));
				else {
					System.out.println("�����Ƿ���:  " + name);
					// ֪ͨ�����Ա���汾���������淶
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
	 * @author ����ò
	 * @time 2017-07-13
	 * @function ���ɶ�Ӧ�İ汾���������������ҷ���ָ��Ŀ¼֮��
	 * @param key
	 *            ��Ӧ�ĸ��²�������
	 * @param moduleVersionOfShip
	 *            ���˵İ汾
	 * @param moduleVersionOfLand
	 *            ���˵İ汾
	 */
	public boolean generateIncrement(String key, String moduleVersionOfShip, String moduleVersionOfLand) {
		// TODO Auto-generated method stub
		boolean result = false;
		// �ɰ汾�������˵İ汾��
		String oldVersionPath = resource.getString(key) + "/" + moduleVersionOfShip + "/";
		
		// �°汾�����������°汾��
		String newVersionPath = resource.getString(key) +"/"+ moduleVersionOfLand + "/";
		try {
			oldVersionPath = new String(oldVersionPath.getBytes("ISO-8859-1"), "utf-8");
			newVersionPath = new String(newVersionPath.getBytes("ISO-8859-1"), "utf-8");
		
		
		    Map<String, FileMd5> oldVersionMap;
		
			// ��md5 ��ʾ�ɰ汾���ļ�
			oldVersionMap = listDir(oldVersionPath);
			// ��md5 ��ʾ�°汾���ļ�
			Map<String, FileMd5> newVersionMap = listDir(newVersionPath);

			// �Ƚ����汾���ļ������������������ compareFile �ļ�����
			List<FileMd5> compareFile = compareFile(newVersionMap, oldVersionMap);

			// ����������һ����ʱĿ¼
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
	 * ��ӡ��� + �����ļ�
	 */
	public static void copyFile(String key,List<FileMd5> fileMd5s, String startTag) {
		CopyFileUtil copyUtil = new CopyFileUtil();

		// ��ȡָ������ʱĿ¼
		String tempPath = resource.getString("tempPath");
		try {
			tempPath = new String(tempPath.getBytes("ISO-8859-1"), "utf-8");

			boolean stateCopyResult = false;
			for (FileMd5 fileMd5 : fileMd5s) {
				System.out.println(fileMd5.getFile().getAbsolutePath() + " " + fileMd5.getMd5());

				String filePath = fileMd5.getFile().getAbsolutePath();
				filePath = filePath.replaceAll("\\\\", "/");
				// String startTag = "D:\\��ͼ��Ŀ\\zip2";
				int index = filePath.indexOf(startTag);
				if (index != -1) {
					index = startTag.length();
					filePath = filePath.substring(index, filePath.length());
				}
				stateCopyResult = copyUtil.copyFile(fileMd5.getFile().getAbsolutePath(),
						tempPath + File.separator + key + File.separator + filePath, true);
				// ��������ļ����ֲ����д����־��ȥ
				if (!stateCopyResult) {
					// д���ļ���
				}
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ����ļ����γ�һ��������
		Zip zip = new Zip();
		String sourcePath = resource.getString("tempPath"); 
		String outPutZipPath = resource.getString("zipPath");
		//ת��
		try {
			sourcePath =  new String(sourcePath.getBytes("ISO-8859-1"), "utf-8");
			outPutZipPath =  new String(outPutZipPath.getBytes("ISO-8859-1"), "utf-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
		zip.zip(sourcePath, outPutZipPath);

		// if (stateCopyResult) {
		// // ����Ŀ¼��ȡ�ļ�С
		// File preZip = new File("D:\\��ͼ��Ŀ\\zip3");
		// FileSize fileSize = new FileSize();
		// long size = fileSize.getFileSize(preZip);
		//
		//
		// // ѹ���ļ�Ŀ¼
		// boolean stateResult = zipFile(size);
		//
		//
		//
		//
		// }

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
			// System.out.println(zipFile.getName() + "," + zipFile.length());
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			stateResult = false;
		}
		return stateResult;
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
	 * ��ȡָ���ļ����µ��ļ���·�����ļ�md5ֵ
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
			} else
				list.add(file);
			System.out.println(file.getAbsolutePath());

		}
		return list;
	}

	/**
	 * @author ����ò
	 * @time 2017-07-13
	 * @function �жϰ汾��Ĳ��죬�Ƿ��������������Ƿ���ڰ汾������
	 * @param key
	 *            ��Ӧ�ĸ��²�������
	 * @param moduleVersionOfShip
	 *            ���˵İ汾
	 * @param moduleVersionOfLand
	 *            ���˵İ汾
	 */
	public Map<String, Object> versionFilter(String key, Map<String, String> shipVerionMap,
			String moduleVersionOfLand) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		String moduleVersionOfShip = shipVerionMap.get(key);
		if (!moduleVersionOfLand.equals(moduleVersionOfShip)) {
			// ����Ƿ��������
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
				// ��������
				String localDep = (String) map.get(mapKey);
				String shipVersion = shipVerionMap.get(mapKey);
				if (localDep.contains(shipVersion)) {
					continue;
				}
				map.clear();
				String responseInfo = "�汾������������Ҫ��" + mapKey + "����";
				map.put("info", responseInfo);
				map.put("result", false);
				return map;
			}

			map.put("result", true);
		} else {
			// �汾һ�£��������
			String responseInfo = "�汾һ�£��������";
			map.put("info", responseInfo);
			map.put("result", false);
		}

		return map;
	}

	public static void sfdsd() {
		String path = "D://��ͼ//�汾��//���ݿ�//10.0.0_db_release_20170713//dep.txt";
		File file = new File(path);
		String result = "";
		BufferedReader reader = null;
		try {
			System.out.println("����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У�");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// һ�ζ���һ�У�ֱ������nullΪ�ļ�����
			while ((tempString = reader.readLine()) != null) {
				// ��ʾ�к�
				System.out.println("line " + line + ": " + tempString);
				String[] str = tempString.split(":");
				if (str.length == 2)
					result += tempString;
				else {
					// ֪ͨ������Ա �������淶
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
