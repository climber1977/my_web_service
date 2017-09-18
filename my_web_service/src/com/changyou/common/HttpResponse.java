package com.changyou.common;  
  
import java.io.IOException;  
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//import org.apache.log4j.Logger;

public class HttpResponse{	
	private static HashMap<String, String> defaultHeads = new HashMap<String, String>();
	static {
    	Date timeTest=new Date();
    	defaultHeads.put("Connection", "Keep-Alive");
    	defaultHeads.put("Date", timeTest.toString());		
    	defaultHeads.put("Content-Type", "text/html");
    	defaultHeads.put("Keep-Alive", "timeout=5, max=98");
    	defaultHeads.put("Server", "my_web_service");
    }
	
	private HashMap<String, String> heads = new HashMap<String, String>();
	
    private OutputStream out = null;
    //private static Logger logger = Logger.getLogger(HttpResponse.class);	
    public HttpResponse(OutputStream out){
    	this.out = out;
    }
   
    public void sendHead(String key, String value) throws IOException{
    	heads.put(key, value);
    }
    private boolean sendHeads(long length) throws IOException{    		
    	String line = "HTTP/1.1 200 OK\r\n";
    	out.write(line.getBytes());
    	for (Map.Entry<String, String> entry : defaultHeads.entrySet()) {  
    		if(heads.containsKey(entry.getKey())){
    			continue;
    		}
    		line = entry.getKey() + ":" + entry.getValue() + "\r\n";
    		out.write(line.getBytes());
    	}
    	for (Map.Entry<String, String> entry : heads.entrySet()) {    		
    		line = entry.getKey() + ":" + entry.getValue() + "\r\n";
    		out.write(line.getBytes());
    	}
    	line = "Content-Length:" + length + "\r\n";
    	out.write(line.getBytes());
    	out.write("\r\n".getBytes());
    	return true;
    }

    public boolean sendData(String content) throws IOException{
    	byte[] bytes = content.getBytes("UTF-8");
		sendHeads(bytes.length);
		out.write(bytes);
    	return true;
    }
    public boolean sendImage(byte[] bytes) throws IOException{
    	sendHead("Content-Type", "image/jpeg");
    	sendHeads(bytes.length);
    	out.write(bytes);
    	return true;
    }
}  