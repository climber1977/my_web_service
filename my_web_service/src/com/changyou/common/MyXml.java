package com.changyou.common;

import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class MyXml {

	public static Iterator<Element> getRootIterator(String path) throws Exception{
		
		SAXReader reader = new SAXReader();  
        //读取文件 转换成Document  
        Document document = reader.read(path);  
        //获取根节点元素对象 
        Element root = document.getRootElement();  
        
        @SuppressWarnings("unchecked")
		Iterator<Element> iterator = root.elementIterator(); 
        
        return iterator;
	}
}
