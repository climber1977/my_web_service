package com.changyou.core;  
  
import java.net.ServerSocket;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.changyou.common.LogUtil;
import com.changyou.common.MyXml;
import com.changyou.service.ResService;

import org.dom4j.Element;

public class Listener{
	private static ServerSocket server = null;
    private static Logger logger = Logger.getLogger(Listener.class);
    private static boolean activity = true;
    private static String cfgPath = "resources/my_web_service.xml";
    private static int port = 8088;
    
    public static void setCfgPath(String path){
    	cfgPath = path;
    }
    public static void setPort(int port){
    	if(port ==0){
    		return;
    	}
    	Listener.port = port;
    }
    private static void setPort(Element e){
    	String strPort = e.attributeValue("port");
    	if(strPort == null || strPort.isEmpty()){
    		return;
    	}
    	setPort(Integer.valueOf(strPort));
    }
    
	@SuppressWarnings("unchecked")
	private static void registerCls(Element e) throws Exception{
    	Iterator<Element> iterator = e.elementIterator("bean");
    	String clsName = null;
    	while(iterator.hasNext()){
            e = iterator.next();
            if(!e.getName().equals("bean")){
            	continue;
            }
            clsName = e.attributeValue("class");
            ServiceManager.register((ServiceBase)Class.forName(clsName).newInstance());
            logger.info("register " + clsName);
        }
    }
	public static void main(String[] args) throws Exception {
		LogUtil.init("resources/log4j.properties");
		logger.info("main start");
		
		Iterator<Element> iterator = MyXml.getRootIterator(cfgPath);
        Element e = null;
        while(iterator.hasNext()){
            e = iterator.next();
            if(e.getName().equals("server")){
            	setPort(e);
            	continue;
            } 
            if(e.getName().equals("beans")){
            	registerCls(e);
            	continue;
            }
        }
        ServiceManager.register(new ResService());
        start();
	}
	public static void start() throws Exception{
		logger.info("home:http://localhost:" + port);
		
		server = new ServerSocket(port);  
    	logger.info("linsten on " + port + " port");
    	
		while (activity) {
			try{
				Server mc = new Server(server.accept());  
		        mc.start();  
			}catch (Exception e) {
				logger.error("main", e);
		    } 
		}  
	}
}  