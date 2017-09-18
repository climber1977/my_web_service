package com.changyou.service;

import org.apache.log4j.Logger;
import com.changyou.common.FileUtils;
import com.changyou.common.HttpRequest;
import com.changyou.common.HttpResponse;
import com.changyou.core.Rest;
import com.changyou.core.ServiceBase;

@Rest(path="")
public class ResService extends ServiceBase {
	private static Logger logger = Logger.getLogger(ResService.class);
	
	@Rest(path="/")
	public String index(HttpRequest request, HttpResponse  response) throws Exception{
		return ResService.getFile("index.html").toString();
	}
	
	@Rest(path="/favicon.ico")
	public Object getFavicon(HttpRequest request, HttpResponse  response) throws Exception{
		return getFile("favicon.ico");
	}
	@Rest(path="/res/{file}")
	public Object getRes(HttpRequest request, HttpResponse  response) throws Exception{
		logger.info("getRes run");
		String fileName = request.getPathParams("file");
		if(fileName.isEmpty()){
			fileName = "index.html";
		}		
		return getFile(fileName);
	}
	
	public static Object getFile(String fileName) throws Exception{
		String path = "resources/web/" + fileName;
		if(!FileUtils.exists(path)){
			return "there is not " + fileName;
		}
		if(fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".ico")){
			return FileUtils.readToBtyes(path);
		}
		return FileUtils.readToStringByUtf8(path);
	}
}
