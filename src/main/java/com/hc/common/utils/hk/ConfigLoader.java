package com.hc.common.utils.hk;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigLoader {
	private final  static Map<String,Document> documentMap=new ConcurrentHashMap ();//XML配置文件集合
	private final static ConfigLoader configLoader=new ConfigLoader();
	public static ConfigLoader getInstance(){
		return configLoader;
	}
	
	{
		loadConfigXMLs();
		}
	/**
	 * 加载conf目录下所有XML配置文件至内存
	 */
	private void loadConfigXMLs(){
		HKLogUtils.info("*****************加载command映射文件*****************");
		HKLogUtils.info("path is:"+ClassLoader.getSystemResource("confighk").getPath());
		//找出conf目录下XML文件
		File directory=new File(ClassLoader.getSystemResource("confighk").getPath());
		if(directory.isDirectory()){
			File[] xmlFiles=directory.listFiles( new FilenameFilter()
            {
                public boolean accept(File file,String name)
                {
                	return name.endsWith(".xml");
                }                     
            });
			
			//xml文件加入到Map
			for(File f:xmlFiles){
				String key=f.getName().substring(0, f.getName().length()-4);
				SAXReader reader = new SAXReader(); 
				Document document=null;
				try {
					document = reader.read(f);
					documentMap.put(key, document);
				} catch (DocumentException e) {
					HKLogUtils.error(e.getMessage(), e);
				}
				 
			}
			
		}		
	}
	
	/**
	 * 根据数据报文功能码获取对应XML document对象
	 * @param code
	 * @return
	 */
	public Document getXMLDocumentsByCode(String code){
		
		return documentMap.get(code);
	}
	


}
