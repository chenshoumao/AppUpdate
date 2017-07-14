package com.solar.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipUtil {

	/**
	 * @author ����ò
	 * @Time 2017-07-14
	 * @Funtion ��ѹ�Ĵ���
	 * @param sourcePath
	 *            ѹ�������ڵ�·��
	 * @param outPath
	 *            ��ѹ�ĺ��ļ��Ĵ��·��
	 */
	public static boolean unzip(String sourcePath, String outPath) {
		boolean stateResult = false;

		long zipFileSize = 0;
		try {
			// �ļ�������
			FileInputStream fin = null;

			int runCount = 0;
			while (!stateResult && runCount < 20) {
				try {
					fin = new FileInputStream(sourcePath);
					File fileJudge = new File(sourcePath);
					String name = "";
					name = fileJudge.getName();
					//long zipSize = Long.valueOf(name.substring(0, name.indexOf("_")));
					//zipFileSize = Long.valueOf(name.substring(name.indexOf("_") + 1, name.indexOf(".")));
				//	long itSize = fileJudge.length();
				//	if (itSize == zipSize)
				//		break;
					stateResult = true;
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e);
					stateResult = false;
					runCount++;
					Thread.sleep(4000);
				}
			}

			if (stateResult) {
				// ��Ҫά������ȡ����У��͵���������У��Ϳ�������֤�������ݵ�������
				CheckedInputStream checkIn = new CheckedInputStream(fin, new CRC32());
				// ָ������ �������������ļ���ѹ����
				Charset gbk = Charset.forName("GBK");
				// zip��ʽ��������
				ZipInputStream zin = new ZipInputStream(checkIn, gbk);

				// ����ѹ���ļ��е�����ѹ����Ŀ
				ZipEntry zinEntry;

				while ((zinEntry = zin.getNextEntry()) != null) {
					System.out.println(zinEntry);
					File targetFile = new File(outPath + File.separator + zinEntry.getName());

					System.out.println("..." + targetFile + "   " + targetFile.getParentFile());

					// String sourceUpdatePath = targetFile.toString();
					if (!targetFile.getParentFile().exists()) {
						System.out.println("..." + targetFile + "   " + targetFile.getParentFile());
						targetFile.getParentFile().mkdirs();
					}
					if (zinEntry.isDirectory()) {
						targetFile.mkdirs();
					} else {
						FileOutputStream fout = new FileOutputStream(targetFile);
						byte[] buff = new byte[1024];
						int length;
						while ((length = zin.read(buff)) > 0) {
							fout.write(buff, 0, length);
						}
						fout.close();
					}
				}

				zin.close();
				fin.close();
				System.out.println(checkIn.getChecksum().getValue());
				checkIn.close();

				Thread.sleep(4000);
				File file = new File(sourcePath);

				// ɾ����ѹ����
				// if (file.exists())
				// file.delete();
				// File afterUnZip5 = new File("D:\\��ͼ��Ŀ\\zip5");

				// long afterUnZip5Size = fileSize.getFileSize(afterUnZip5);
				// long preUnZip5Size = zipFileSize;

				// boolean resultState = preUnZip5Size == afterUnZip5Size;
			}

		} catch (Exception e) {
			// TODO: handle exception
			stateResult = false;
			System.out.println(e);
		}
		if (stateResult) {
			System.out.println("��ѹ���ݳɹ�");
			stateResult = true;
		} else {
			System.out.println("��ѹ����ʧ��");
			stateResult = false;
		}
		return stateResult;
		// System.exit(0);
	}

	public Map<String, Object> updateDir() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//���´���
			overideUpdate("");
			//���º�ͼ
			overideUpdate("haitu");
			//���µ�ͼ
			overideUpdate("ditu");
			//��������
			//...
			//�������ݿ�
			//...
		} catch (Exception e) {
			// TODO: handle exception
		}
		return map;
	}

	// �Ը��ǵ���ʽ���� �˲��ְ������룬ͼ
	public Map<String, Object> overideUpdate(String key) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean result = false;
		try {

			ResourceBundle resouce = ResourceBundle.getBundle("config/ship");

			// ��ȡ��Ŀ��·��
			String sourcePath = null;
			if(!key.equals(""))
				sourcePath = resouce.getString("web") + File.separator + key;
			else
				sourcePath = resouce.getString("web");
			//��ȡ���½�ѹ���ļ���
			ReadFile readFile = new ReadFile();
			String filePath = resouce.getString("informUnzipFilePath");
			String unzipFileName = readFile.readLastLine(new File(filePath), "utf-8");
			// ��ȡ��ѹ·����Ҫ��ȡ�����ݵ�·��
			String unzipPath = resouce.getString("unzipPath") + File.separator + unzipFileName + File.separator + resouce.getString(key);

			// ֱ�Ӹ���
			CopyFileUtil copyUtil = new CopyFileUtil();
			result = copyUtil.copyDirectory(unzipPath, sourcePath, true);

		} catch (Exception e) {
			// TODO: handle exception
			result = false;
		}
		map.put("result", result);
		return map;
	}

	

	public static void main(String[] args) {
		File file = new File("D:/��ͼ��Ŀ/֪ͨ�ļ�/��ѹ�ļ�/inform.txt");

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
			long len;

			len = raf.length();

			if (len == 0L) {

			} else {
				long pos = len - 1;
				while (pos > 0) {
					pos--;
					raf.seek(pos);
					if (raf.readByte() == '\n') {
						break;
					}
				}
				if (pos == 0) {
					raf.seek(0);
				}
				byte[] bytes = new byte[(int) (len - pos)];
				raf.read(bytes);
				System.out.println(new String(bytes, "gbk"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (Exception e2) {
				}
			}
		}

	}

}
