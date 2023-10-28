package com.ruoyi.common.utils.spring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;

public class CommonProperties {

	private static Map<String, String> map = new HashMap<>();
	private static Properties pro = new Properties();
	
	static{
	    InputStream in = null;
	    try {
	        ClassPathResource resource = new ClassPathResource("common.properties");
	        in=resource.getInputStream();
	        //解决中文乱码
	        BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
	        pro.load(bf);
	        in.close();
	        bf.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static String getProperty(String key) {
	    return pro.getProperty(key);
	}
	
	public static Map<String, String> getPropertyMap() {
	    pro.keySet().forEach(item->{
	        map.put(item.toString(),pro.getProperty(item.toString()));
	    });
	    return map;
	}
}
