package com.changyou.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.changyou.common.HttpRequest;
import com.changyou.common.HttpResponse;

public class ServiceManager {
	 
	 private static class MethodInfo{
		String url;
		Object obj;
		Method method;
	 }
	 private static HashMap<String, MethodInfo> services = new HashMap<String, MethodInfo>();
	 
	 public static void register(ServiceBase obj){
		 if(!obj.getClass().isAnnotationPresent(Rest.class)){
			 return;
		 }
		 Rest p = obj.getClass().getAnnotation(Rest.class);
		 String rootPath = p.path();
		 String url = null;
		 MethodInfo info = null;
		 Method methods[] = obj.getClass().getMethods();
		 for(int i=0; i<methods.length; i++){
			 if(!methods[i].isAnnotationPresent(Rest.class)){
				 continue;
			 }
			 p = methods[i].getAnnotation(Rest.class);
			 url = rootPath + p.path();
			 info = new MethodInfo();
			 info.method = methods[i];
			 info.obj = obj;
			 info.url = url;
			 services.put(url, info);
		 }
	 }
	public static MethodInfo matchUrl(HttpRequest request){	
		MethodInfo info = null;
		Iterator<Map.Entry<String, MethodInfo>> entries = services.entrySet().iterator();  
		while (entries.hasNext()) {
			info = entries.next().getValue();			
			if(request.matchUrl(info.url)){
				return info;
			}
		}
		return null;
	}
	public static MethodInfo startWithUrl(HttpRequest request){	
		MethodInfo info = null;
		Iterator<Map.Entry<String, MethodInfo>> entries = services.entrySet().iterator();  
		while (entries.hasNext()) {
			info = entries.next().getValue();
			if(info.url.equals("/")){
				continue;
			}
			if(request.getUrlNoParam().startsWith(info.url)){
				return info;
			}
		}
		return null;
	 }
	 public static void dealRequest(HttpRequest request, HttpResponse  response) throws Exception{
		 MethodInfo info = null;
		 String url = request.getUrlNoParam();
		 if(services.containsKey(url)){
			info = services.get(url);
		 } else {
			 info = startWithUrl(request);
			 if(info == null) {
				info = matchUrl(request);
			 }
		 }		 
		 if(info == null){
			 response.sendData("cann't find request address!");
			 return;
		 }
		
		 Object result = info.method.invoke(info.obj, request, response);
		 if(result != null){
			 if(result instanceof String){
				 response.sendData(result.toString());
			 } else {
				 response.sendImage((byte[])result);
			 }
		 } 
	 }
}
