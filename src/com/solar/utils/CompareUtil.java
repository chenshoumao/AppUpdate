package com.solar.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import difflib.Delta;
import difflib.DiffRow;
import difflib.DiffRowGenerator;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.DiffRow.Tag;

public class CompareUtil{
	
	public static void main(String[] args) {
		CompareUtil compareUtil = new CompareUtil();
		try {
			compareUtil.compareFile("D:/海图项目/reposities/应用/配置/config/etl_org.sql", "D:/海图项目/reposities/应用/配置/config/etl_change.sql");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void compareFile(String orgPath,String changePath) throws IOException {
		List<String> original = FileUtils.readLines(new File(orgPath));
		List<String> revised = FileUtils.readLines(new File(changePath));

		Patch patch = DiffUtils.diff(original, revised);

		for(Delta delta : patch.getDeltas()) {
			List<?> list = delta.getRevised().getLines();
			for(Object object : list) {
				System.out.println(object);
			}
		}
		
		DiffRowGenerator.Builder builder = new DiffRowGenerator.Builder();
		builder.showInlineDiffs(false);
		DiffRowGenerator generator = builder.build();
		for (Delta delta :  patch.getDeltas()) {
			List<DiffRow> generateDiffRows = generator.generateDiffRows((List<String>) delta.getOriginal().getLines(), (List<String>) delta
					.getRevised().getLines());
			int leftPos = delta.getOriginal().getPosition();
			int rightPos = delta.getRevised().getPosition();
			for (DiffRow row : generateDiffRows) {
				Tag tag = row.getTag();
				if (tag == Tag.INSERT) {
					System.out.println("Insert: ");
					System.out.println("new-> " + row.getNewLine());
					System.out.println("");
				} else if (tag == Tag.CHANGE) {
					System.out.println("change: ");
					System.out.println("old-> " + row.getOldLine());
					System.out.println("new-> " + row.getNewLine());
					System.out.println("");
				} else if (tag == Tag.DELETE) {
					System.out.println("delete: ");
					System.out.println("old-> " + row.getOldLine());
					System.out.println("");
				} else if (tag == Tag.EQUAL) {
					System.out.println("equal: ");
					System.out.println("old-> " +  row.getOldLine());
					System.out.println("new-> " +  row.getNewLine());
					System.out.println("");
				} else {
					throw new IllegalStateException("Unknown pattern tag: " + tag);
				}
			}
		}
	}
}
