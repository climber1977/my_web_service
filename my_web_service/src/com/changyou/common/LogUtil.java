package com.changyou.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;

public class LogUtil {

	public static void init(String path) throws Exception{
		File  is = new File(path); 
		InputStream in = new FileInputStream(is);  
		Reader reader=new BufferedReader(new InputStreamReader(in)); 	
		
		Properties pro = new Properties();
		pro.load(reader);
		PropertyConfigurator.configure(pro);
	}
}
