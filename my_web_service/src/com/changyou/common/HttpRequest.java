package com.changyou.common;  
  
import java.io.IOException;  
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class HttpRequest{  
	public List<String> heads = new ArrayList<String>(); 
	public int dataLen = 0;
	public boolean isChunk = false;
    private List<byte[]> datas = new ArrayList<byte[]>();
    private InputStream in = null;
    private static Logger logger = Logger.getLogger(HttpRequest.class);	
    
    private String requestUrl;
    private String urlNoParam;
	private String httMethod = "";
    private String httVersion = "";
    
    private Map<String, String> urlParams = new HashMap<String, String>(); 
    private Map<String, String> contentParams = new HashMap<String, String>(); 
    private List<String> pathParamsNames = new ArrayList<String>();
	private HashMap<String, String> pathParams = new HashMap<String, String>();
	
    public HttpRequest(InputStream in){
    	this.in = in;
    }
    protected String readLine() throws IOException {
		byte r[] = new byte[2048];
		int i = 0;
		int read =0;
		do{
			read=in.read();
			if(read==-1){
				return null;
			}
			r[i] = (byte)read;
			i++;
		}while(read!='\n');		
		
		String res =new String(r, 0, i-2); 
		return res; 
	}
    private void parseParams(Map<String, String> map, String paramStr){
    	String[] strs = paramStr.split("&");
        String[] param = null;
        for (String str : strs) {
        	param = str.split("=");
        	map.put(param[0], param[1]);
        }
    }
    private void parseParamsFromUrl() {
        int pos = requestUrl.indexOf("?");
        if(pos < 0){
        	setUrlNoParam(requestUrl);
        	return;
        }
        setUrlNoParam(requestUrl.substring(0, pos));
        String paramStr = requestUrl.substring(pos + 1);
        parseParams(urlParams, paramStr);
        
    }
    
	private String getUrlRegex(String matchedUrl){
		Pattern pattern = Pattern.compile("\\{.*?\\}");
		Matcher matcher = pattern.matcher(matchedUrl);
		
		StringBuffer result = new StringBuffer();
		String paraName = null;
		boolean match = false;
		while (matcher.find()) {
			paraName = matcher.group().replace("{", "");
			paraName = paraName.replace("}", "");
			pathParamsNames.add(paraName);
			matcher.appendReplacement(result, "(.*)");
			match = true;
		}
		matcher.appendTail(result);
		return match ? result.toString() : "";
	}
	public boolean matchUrl(String matchedUrl){		
		pathParamsNames.clear();
		pathParams.clear();
		
		String urlRegex = getUrlRegex(matchedUrl);
		if(urlRegex.isEmpty()){
			return false;
		}

		Pattern pattern = Pattern.compile(urlRegex);
		Matcher matcher = pattern.matcher(urlNoParam);
		
		if(!matcher.find()) {
			return false;
		}
		for(int i=1; i<=matcher.groupCount(); i++){
			pathParams.put(pathParamsNames.get(i-1), matcher.group(i));
		}
		return true;
	}
	public String getUrlParam(String key){
		if(urlParams.containsKey(key)){
    		return urlParams.get(key);
    	}
		return "";
	}
	public String getContentParams(String key){
		if(contentParams.containsKey(key)){
    		return contentParams.get(key);
    	}
		return "";
	}
	
	public String getPathParams(String key){
		if(pathParams.containsKey(key)){
    		return pathParams.get(key);
    	}
		return "";
	}
    public String getParam(String key){
    	if(urlParams.containsKey(key)){
    		return urlParams.get(key);
    	}
    	if(contentParams.containsKey(key)){
    		return contentParams.get(key);
    	}
    	if(pathParams.containsKey(key)){
    		return pathParams.get(key);
    	}
    	return "";
    }
    private boolean readHttpHead() throws IOException{    	
    	String line = "";
    	String strs[] = null;
        while ((line = readLine()) != null && !"".equals(line)) {
        	if(line.startsWith("GET ") || line.startsWith("POST")){
        		strs = line.split(" ");
        		setHttMethod(strs[0]);
        		requestUrl = strs[1];
        		setHttVersion(strs[2]);
        		parseParamsFromUrl();
        	} else if(line.startsWith("Content-Length:")){
            	String[] ss = line.split(":");
            	dataLen = Integer.valueOf(ss[1].trim());
            } else if(line.startsWith("Transfer-Encoding: chunked")){
            	isChunk = true;
            }  else if(line.startsWith("Transfer-Encoding: chunked")){
            	isChunk = true;
            } 
            heads.add(line);        
        }
        return true;
    }
    private boolean readData(InputStream  in, byte[] bs, int pos, int len) throws IOException{
    	if(len <= 0){
    		return true;
    	}
    	
    	int read=pos;
		while(read<len){
			//因为
			int r = in.read(bs, read, bs.length-read);
			if(r==-1 || r+read == bs.length){
				break;
			}
			read+=r;
		}
		//MyDebug.println(new String(body));
        return true;
    }
    private boolean readHttpCommonBody(InputStream  in) throws IOException{
    	if(dataLen <= 0){
    		return true;
    	}
    	byte[] data = new byte[dataLen];
    	readData(in, data, 0, dataLen);    
    	datas.add(data);
    	return true;
    }
    
    private boolean readHttpChunk(InputStream  in) throws IOException{
    	int CRLFLen = 2;
    	String lengthOfHex = readLine();  //chunk-size and CRLF 
    	logger.debug("readHttpChunk=" + lengthOfHex); 
    	int length = Integer.valueOf(lengthOfHex, 16); 
    	
    	int allLen = 0;
    	String footer = "";
    	if(length == 0){
    		footer = readLine();  
    		allLen = lengthOfHex.getBytes().length + CRLFLen + footer.getBytes().length + CRLFLen;
    	} else {
    		allLen = lengthOfHex.getBytes().length + CRLFLen + length + CRLFLen;
    	}
    	byte[] data = new byte[allLen];
    	int index = 0;
    	//长度
    	System.arraycopy(lengthOfHex.getBytes(),0,data,index,lengthOfHex.getBytes().length);
    	index += lengthOfHex.getBytes().length;
    	data[index++] = 13;
    	data[index++] = 10;   	
    	if(length == 0){
    		//footer
    		System.arraycopy(footer.getBytes(),0,data,index,footer.getBytes().length);
    		index += footer.getBytes().length;
    		data[index++] = 13;
        	data[index++] = 10;   
    	} else {
    		//数据
    		readData(in, data, index, length+CRLFLen);
    	}
    	
    	datas.add(data);
    	return !(length == 0);
    }
   
    private boolean readHttpBody() throws IOException{
    	if(isChunk){
    		while(readHttpChunk(in));
    	}
    	readHttpCommonBody(in);

    	return false;
    }
    public boolean readData() throws Exception {
    	readHttpHead();
    	readHttpBody();
    	logger.debug(requestUrl);
    	if(!isChunk){
    		String line = null;
    		for(int i=0; i<datas.size(); i++){
    			line = new String(datas.get(i), "UTF-8");
    			logger.debug(line);
    			parseParams(contentParams, line);
    		}
    	}
    	return true;
	}
    
    public boolean isDisconnect(){
    	return heads.size() == 0;
    }
	public String getUrl() {
		return requestUrl;
	}
	public void setUrl(String url) {
		this.requestUrl = url;
	}
	public String getHttVersion() {
		return httVersion;
	}
	public void setHttVersion(String httVersion) {
		this.httVersion = httVersion;
	}
	public String getHttMethod() {
		return httMethod;
	}
	public void setHttMethod(String httMethod) {
		this.httMethod = httMethod;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getUrlNoParam() {
		return urlNoParam;
	}
	public void setUrlNoParam(String urlNoParam) {
		this.urlNoParam = urlNoParam;
	}
}  