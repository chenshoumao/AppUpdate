package com.solar.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class tEST {
	public static void main(String[] args) throws IOException {
		List<String> original = FileUtils.readLines(new File("../../../../../db/version.txt"));
	}
}
