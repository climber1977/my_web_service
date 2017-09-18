package com.changyou.core;  
  

import java.io.IOException;  
import java.net.Socket;
import org.apache.log4j.Logger;

import com.changyou.common.HttpRequest;
import com.changyou.common.HttpResponse;

public class Server extends Thread {
    private Socket client;
    private static Logger logger = Logger.getLogger(Server.class);
    
    public Server(Socket c) {
        this.client = c;
    }
    
	private boolean readHttpData() throws Exception{
    	HttpRequest request = new HttpRequest(client.getInputStream());
    	request.readData();
		
		if(request.isDisconnect()){
			logger.info("peer dissconnect");
			return false;
		}
		
		HttpResponse  response = new HttpResponse(client.getOutputStream());
		ServiceManager.dealRequest(request, response);   	
        return true;
    }  
    public void run() {
    	logger.info("run start");
    	try {
    		while(readHttpData());
    	}catch (Exception ex) {
    		logger.error("readHttpData expection", ex);
        	try {
				client.close();
			} catch (IOException e) {
				logger.error("close socket expection", e);
			}
        } 
    	logger.info("run end");
    }
}  