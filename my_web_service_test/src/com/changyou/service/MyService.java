package com.changyou.service;

import java.util.Date;

import org.apache.log4j.Logger;

import com.changyou.common.HttpRequest;
import com.changyou.common.HttpResponse;
import com.changyou.core.Rest;
import com.changyou.core.ServiceBase;

@Rest(path="")
public class MyService extends ServiceBase {
	private static Logger logger = Logger.getLogger(MyService.class);
	
	@SuppressWarnings("deprecation")
	private String getDateTime(){
		Date timeTest=new Date();
		return timeTest.toLocaleString();
	}
	
	@Rest(path="/hello")
	public String hello(HttpRequest request, HttpResponse  response){
		logger.info("hello run");
		String line = "<h1>" + getDateTime() + " hello world!" + "</h1>";
		return line;
	}
	
	@Rest(path="/test")
	public String test(HttpRequest request, HttpResponse  response){
		logger.info("test run");
		String line = "<h1>" + getDateTime() + " " + "</h1>";
		line += "<h1>" + request.getUrl() + " " + "</h1>";
		line += "<h1>name=" + request.getUrlParam("name") + " " + "</h1>";
		return line;
	}
	@Rest(path="/task/{user}/{id}")
	public String createTask(HttpRequest request, HttpResponse  response){
		logger.info("createTask run");
		String line = "<h1>" + getDateTime() + " " + "</h1>";
		line += "<h1>" + request.getUrl() + " " + "</h1>";
		line += "<h1>user=" + request.getPathParams("user") + " " + "</h1>";
		line += "<h1>id=" + request.getPathParams("id") + " " + "</h1>";
		line += "<h1>uuid=" + request.getUrlParam("uuid") + " " + "</h1>";
		return line;
	}
	@Rest(path="/login")
	public String login(HttpRequest request, HttpResponse  response){
		logger.info("login run");
		String line = "<h1>" + getDateTime() + " " + "</h1>";
		line += "<h1>" + request.getUrl() + " " + "</h1>";
		line += "<h1>name=" + request.getContentParams("name") + " " + "</h1>";
		line += "<h1>password=" + request.getContentParams("password") + " " + "</h1>";
		return line;
	}
	
	@Rest(path="/ajaxlogin")
	public String ajaxLogin(HttpRequest request, HttpResponse  response){
		logger.info("ajaxlogin run");
		String line = "<h1>" + getDateTime() + " " + "</h1>";
		line += "<h1>" + request.getUrl() + " " + "</h1>";
		line += "<h1>name=" + request.getContentParams("name") + " " + "</h1>";
		line += "<h1>password=" + request.getContentParams("password") + " " + "</h1>";
		return line;
	}
	
	@Rest(path="/ajaxlogin_json")
	public String ajaxLoginJsong(HttpRequest request, HttpResponse  response){
		logger.info("ajaxLoginJsong run");
		String line = "{";
		line += "\"date\":\"" + getDateTime() + "\",";
		line += "\"url\":\"" + request.getUrl() + "\",";
		line += "\"name\":\"" + request.getContentParams("name") + " " + "\",";
		line += "\"password\":\"" + request.getContentParams("password") + " " + "\"";
		line += "}";
		return line;
	}
}
