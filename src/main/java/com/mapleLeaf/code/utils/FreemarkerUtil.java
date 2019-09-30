package com.mapleLeaf.code.utils;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 模板代码生成工具类
 * @author 
 *
 */
public class FreemarkerUtil {
	
    
    
    public static void createDoc(Configuration configuration,Object obj
    		,String template,String saveFilePath) throws Exception {
    	@SuppressWarnings("unchecked")
		Map<String,Object> dataMap = (Map<String,Object>)JSON.toJSON(obj);
    	
    	createDoc(configuration, dataMap, template, saveFilePath);
    }
    
    public static void createDoc(Configuration configuration,Map<String,Object> dataMap,
    		String template,String saveFilePath) throws Exception{
    	Template t=configuration.getTemplate(template+".ftl");  
    	
    	//输出文档路径及名称  
        File outFile = new File(saveFilePath);  
       
        try(FileOutputStream fos=new FileOutputStream(outFile);
        	OutputStreamWriter oWriter = new OutputStreamWriter(fos,"UTF-8");	
        	Writer out = new BufferedWriter(oWriter); ){
        	t.process(dataMap, out);
        }
              
      
    }
    
    public static String createString(Configuration configuration,Object dataMap,String template)throws Exception{
    	Template t=null;  
        try {  
            //test.ftl为要装载的模板  
            t = configuration.getTemplate(template+".ftl");
        } catch (IOException e) {  
            e.printStackTrace();  
        }
      //输出文档路径及名称  
        StringWriter out = new StringWriter();  
           
        try {  
            t.process(dataMap, out);  
        } catch (TemplateException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        return out.getBuffer().toString();
    }
    
}
