package com.changyou.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class FileUtils {
	public static String getFileCharset(String fileName) throws Exception{  
	    BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));  
	    int p = (bin.read() << 8) + bin.read();  
	    String code = null;  
	      
	    switch (p) {  
	        case 0xefbb:  
	            code = "UTF-8";  
	            break;  
	        case 0xfffe:  
	            code = "Unicode";  
	            break;  
	        case 0xfeff:  
	            code = "UTF-16BE";  
	            break;  
	        default:  
	            code = "GBK";  
	    }  
	    bin.close();
	    return code;  
	}  
	public static byte[] readToBtyes(File file) throws Exception {
		
        Long filelength = Long.valueOf(file.length());     //获取文件长度
        byte[] filecontent = new byte[filelength.intValue()];
       
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);
        in.close();
        return filecontent;
	}
	
	public static byte[] readToBtyes(String fileName) throws Exception {
		File file=new File(fileName);  
		byte[] bys = readToBtyes(file);
		
		file = null;
		return bys;
	}
	
	public static boolean writeToBtyes(File file, byte[] data) throws Exception {
		FileOutputStream in = new FileOutputStream(file);
        in.write(data);
        in.close();
        return true;
	}
	
	public static boolean writeToBtyes(String fileName, byte[] data) throws Exception {
		File file=new File(fileName);  
		boolean bret = writeToBtyes(file, data);
		
		file = null;
		return bret;
	}
	
	public static String readToString(File file, String charset) throws Exception {
		FileInputStream fInputStream = new FileInputStream(file);  
		InputStreamReader inputStreamReader = new InputStreamReader(fInputStream, charset);  
		BufferedReader in = new BufferedReader(inputStreamReader);  
       
		String strTmp = ""; 
		StringBuffer sBuffer = new StringBuffer();
		//按行读取  
		while (( strTmp = in.readLine()) != null) {  
		    sBuffer.append(strTmp + "\n");  
		}  
		in.close();
		inputStreamReader.close();
		fInputStream.close();
		return sBuffer.toString(); 
	}
	
	public static String readToString(String fileName, String charset) throws Exception {
		File file=new File(fileName);
		String content = readToString(file, charset);
		
		file = null;
		return content;
	}
	public static String readToStringByUtf8(String fileName) throws Exception {
		return readToString(fileName, "UTF-8");
	}
	public static String readToStringDefaultChartSet(String fileName) throws Exception {
		return readToStringByUtf8(fileName);
	}
	public static String readToString(String fileName) throws Exception {
		String charset = getFileCharset(fileName);
		File file=new File(fileName);
		String content = readToString(file, charset);
		
		file = null;
		return content;
	}
	
	public static void writeToString(File file, String content, String charset) throws Exception {       
        FileOutputStream fOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOutputStream, charset);
        BufferedWriter out = new BufferedWriter(outputStreamWriter);
        out.write(content);
        out.close();  
        outputStreamWriter.close();
        fOutputStream.close();
	}
	
	public static void writeToString(String path, String content, String charset) throws Exception {
		File file=new File(path);  
		writeToString(file, content, charset);
		file = null;
	}
	
	public static Properties getProperties(String path) throws Exception{
		Properties prop = new Properties();
			FileInputStream fInputStream = new FileInputStream(path);  
		InputStreamReader inputStreamReader = new InputStreamReader(fInputStream, "UTF-8");		
		prop.load(inputStreamReader);
		return prop;		
	}
	
	public static boolean isDirectory(String path){
		File file = new File(path);
		if (file.isDirectory()) {
			file = null;
			return true;
		}
		file = null;
		return false;
	}
	public static boolean exists(String path){
		File file = new File(path);
		if (file.exists()) {
			file = null;
			return true;
		}
		file = null;
		return false;
	}
	public static void moveFile(String srcFilePath, String dstFilePath) throws Exception{
		byte[] src;
		
		src = readToBtyes(srcFilePath);
		writeToBtyes(dstFilePath, src);
		File srcf = new File(srcFilePath);
		srcf.delete();
		srcf = null;
		
	}
	public static void copyFile(String srcFilePath, String dstFilePath) throws Exception{
		byte[] src;
		src = readToBtyes(srcFilePath);
		writeToBtyes(dstFilePath, src);			
			
	}
	public static boolean moveFile2(String srcFilePath, String dstFilePath){
		File srcf = new File(srcFilePath);
		File dstf = new File(dstFilePath);
		
		dstf.delete();
		return srcf.renameTo(dstf);
	}
	
	public static String conjPaths(String... args){
		String path = "";
		for (String v : args) {
			if(path.equals("")){
				path = v;
			}  else if((path.endsWith("/") || path.endsWith("\\")) && 
			   (v.startsWith("/") || v.startsWith("\\"))){
				path += v.substring(1);
			} else if((path.endsWith("/") || path.endsWith("\\")) || 
					   (v.startsWith("/") || v.startsWith("\\"))){
				path += v;
			} else {
				path +=  "/" + v;
			}
        }
		return path;
	}
}
