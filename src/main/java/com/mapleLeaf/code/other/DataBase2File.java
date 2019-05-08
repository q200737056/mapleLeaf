package com.mapleLeaf.code.other;

import java.io.File;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mapleLeaf.code.model.Config;
import com.mapleLeaf.code.model.Module;
import com.mapleLeaf.code.model.Table;
import com.mapleLeaf.code.model.TableConf;
import com.mapleLeaf.code.service.ITableService;
import com.mapleLeaf.code.utils.FreemarkerUtil;
import com.mapleLeaf.common.util.GlobalConst;




@Component
public class DataBase2File {
	
	
	private Logger log =  LoggerFactory.getLogger(DataBase2File.class);
	
	
    /** 
     * 主方法，生成相关文件 
     * @throws IOException  
     * @throws ClassNotFoundException 
     * @throws SQLException 
     */  
    public void generateFiles(String tplPath) throws Exception{
        log.info("Generating...");
        Long start=System.currentTimeMillis();
        
        try {
        	
        	 Config config= ConfigFactory.createConfig("config.xml",tplPath);
             
             ITableService tableService = TableServiceFactory.getInstance(config.getDb().getDbType());
             tableService.setDb(config.getDb());
             Class.forName(config.getDb().getDriver());  
             Connection con = DriverManager.getConnection(config.getDb().getUrl(), config.getDb().getUser()
             		,config.getDb().getPwd());  
             for (Module module : config.getModules()) {
             	log.info("module="+module.getName());
             	
             	
             	if (module.getTables()==null || module.getTables().isEmpty()) {
             		//module.setTables(tableService.getAllTables(module.getName()+"%"));
             		continue;
             	}
             	
             	
             	for (TableConf tbConf : module.getTables()) {
             		Table table = tableService.getTable(tbConf,module, con);//获取一个表的相关信息
             		genFile(table,config, module);
             	}
             	
             	
             }
             con.close();
             //公共类生成
             if(!config.getCommonMap().isEmpty()){
             	for(Entry<String, List<String>> entry:config.getCommonMap().entrySet()){
             		String pak = entry.getKey();
             		List<String> templs = entry.getValue();
             		for(String item:templs){
             			generateCommonFile(pak,item,config);
             		}
             	}
             }
             
             Long end = System.currentTimeMillis();
             log.info("Generate Success! total time = "+(end-start));
             log.info("Please check: " + config.getBaseDir());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
        
    } 
    
    /**
     * 递归生成所有文件代码
     * @param tb
     * @param module
     * @return
     */
    private void genFile(Table tb,Config config,Module module) throws Exception {
    	JSONObject obj = (JSONObject)JSON.toJSON(tb);
    	setBaseInfo(obj,config,module);
    	//获取指定排除的模板
    	String exclude = tb.getExclude();
    	List<String> excludeList = new ArrayList<String>();
    	if(!StringUtils.isBlank(exclude)){
    		String[] arr = exclude.split(",");
    		excludeList=Arrays.asList(arr);
    	}
    	if(!excludeList.contains("entity") &&
    			!StringUtils.isBlank(module.getEntityPackage())){
    		generateEntityFile(obj,tb,config, module);//生成entity
    	}
    	if(!excludeList.contains("dao") &&
    			!StringUtils.isBlank(module.getDaoPackage())){
    		generateDaoFile(obj,tb,config,module);//生成dao
    	}
    	if(!excludeList.contains("service") &&
    			!StringUtils.isBlank(module.getServicePackage())){
    		generateServiceFile(obj,tb, config,module);//生成service
    	}
    	if(!excludeList.contains("controller") &&
    			!StringUtils.isBlank(module.getControllerPackage())){
    		generateActionFile(obj,tb,config,module);//生成controller
    	}
    	
        if (!excludeList.contains("view") &&
        		!StringUtils.isBlank(module.getViewPackage())
        		&&module.getAttrsMap().get("viewPackage_tpl")!=null
        		&&!module.getAttrsMap().get("viewPackage_tpl").trim().equals("")) {
        	generateViewFile(obj,tb,config,module);//生成 view
			
        }
        if (!excludeList.contains("custom") &&
        		!StringUtils.isBlank(module.getCustomPackage())
        		&&module.getAttrsMap().get("customPackage_tpl")!=null
        		&&!module.getAttrsMap().get("customPackage_tpl").trim().equals("")){
        	generateCustomFile(obj,tb,config,module);//生成 自定义 文件
        }
        
        
        //如果有从表,从表文件生成,递归
    	if(tb.getSubTables().size()>0){
    		List<Table> subtabs = tb.getSubTables();
    		tb.setSubTables(null);//去掉 从表列表 ，防止转成json时，死循环
    		
    		for(Table subtable:subtabs){
    			
    			subtable.setParentTable(tb);//设置主表 
    			//JSONObject subobj = (JSONObject)JSON.toJSON(subtable);
    	    	//setBaseInfo(subobj,module);
    	    	
    	    	genFile(subtable,config,module);
    	    	
    		}
    	}
        
    }
      
  
     /**
     * 将模块信息转换为json结构
     * @param obj
     * @param module
     */
    private void setBaseInfo(JSONObject obj,Config config,Module module) {
    	obj.put("basePackage", config.getBasePackage());
    	obj.put("moduleName", module.getName());
    	obj.put("entityPackage", module.getEntityPackage());
    	obj.put("servicePackage", module.getServicePackage());
    	obj.put("serviceImplPackage", module.getServiceImplPackage());
    	obj.put("daoPackage", module.getDaoPackage());
    	obj.put("daoImplPackage", module.getDaoImplPackage());
    	obj.put("controllerPackage", module.getControllerPackage());
    	obj.put("viewPackage", module.getViewPackage());
		obj.put("customPackage", module.getCustomPackage());
    	obj.put("mapperPackage", module.getMapperPackage());
    	
    	obj.put("persistance", module.getPersistance());//持久层框架
    	obj.put("param", module.getParamMap());
    	
    }
    
    /**
     * 生成指定表对象对应的类文件 
     * @param table 
     */  
    private void generateEntityFile(JSONObject obj,Table table,Config config,Module module) throws Exception {
    	
    	File saveDir=getSaveFilePath(module.getEntityPackage(),config);
    	
    	File saveFile = new File(saveDir,table.getEntityName()+".java");
    	
    	String savePath =saveFile.getAbsolutePath();
    	FreemarkerUtil.createDoc(config.getFmkConf(),obj, "Entity", savePath);
    	log.info("生成文件："+savePath);
    	
    }
    /**
     * 生成公共类文件 
     * @param table 
     */  
    private void generateCommonFile(String pak,String tmpl,Config config) throws Exception {
    	
    	File saveDir=getSaveFilePath(pak,config);
    	File saveFile = new File(saveDir,tmpl+".java");

    	String savePath =saveFile.getAbsolutePath();
    	FreemarkerUtil.createDoc(config.getFmkConf(),null, tmpl, savePath);
    	log.info("生成文件："+savePath);
    }

    /**
     * 根据模块定义生成文件保存目录
     * @param module
     * @param packageName
     * @return
     */
    private File getSaveFilePath(String packageName,Config config){
    	
		File saveDir = new File(config.getBaseDir()+ File.separator 
				+ config.getBasePackage().replace(".", File.separator)
				+File.separator+packageName.replace(".", File.separator));
    	
    	if (!saveDir.exists()) {
    		saveDir.mkdirs();
    	}
    	return saveDir;
    }
    
    
    /**
     * 生成dao文件
     * @param table
     */
    private void generateDaoFile(JSONObject obj,Table table,Config config,Module module) throws Exception {
    
    	File saveDir=getSaveFilePath(module.getDaoPackage(),config);
    	
    	String fileName = table.getEntityName()+"Dao.java";
    	if("mybatis".equals(module.getPersistance())){
    		fileName = table.getEntityName()+"Mapper.java";
    	}
    	File saveFile = new File(saveDir,fileName);
    	String savePath =saveFile.getAbsolutePath();
    	
    	FreemarkerUtil.createDoc(config.getFmkConf(),obj, "Dao", savePath);
    	log.info("生成文件："+savePath);
    	//dao实现类
    	if (module.getPersistance().toLowerCase()
    			.equals("hibernate")){
        	File implDir=getSaveFilePath(module.getDaoPackage()+File.separator+module.getDaoImplPackage(),config);
	    	
	    	//generateMapperFile(table, module);
	    	File implFile = new File(implDir,table.getEntityName()+"DaoImpl.java");
	    	String implPath =implFile.getAbsolutePath();
	    	FreemarkerUtil.createDoc(config.getFmkConf(),obj, "DaoImpl", implPath);
	    	log.info("生成文件："+implPath);
    	}else if (module.getPersistance().toLowerCase()
    			.equals("mybatis")) {
    		/**
        	 * 如果是mybatis，则生成mytabis的xml配置文件
        	 */
    		saveDir=getSaveFilePath(module.getMapperPackage(),config);
    		File implFile = new File(saveDir,table.getEntityName()+"Mapper.xml");
	    	String implPath =implFile.getAbsolutePath();
	    	FreemarkerUtil.createDoc(config.getFmkConf(),obj, "Mapper", implPath);
	    	log.info("生成文件："+implPath);
    	}else{
    		//log.info("该ORM框架不支持或不存在！");
    		File implDir=getSaveFilePath(module.getDaoPackage()+File.separator+module.getDaoImplPackage(),config);
	    	
	    	File implFile = new File(implDir,table.getEntityName()+"DaoImpl.java");
	    	String implPath =implFile.getAbsolutePath();
	    	FreemarkerUtil.createDoc(config.getFmkConf(),obj, "DaoImpl", implPath);
	    	log.info("生成文件："+implPath);
    	}
    }

    /**
     * 生成service文件
     * @param table
     */
    private void generateServiceFile(JSONObject obj,Table table,Config config,Module module) throws Exception {
    	
    	File saveDir=getSaveFilePath(module.getServicePackage(),config);
    	File saveFile = new File(saveDir,table.getEntityName()+"Service.java");
    	String savePath =saveFile.getAbsolutePath();
    	log.info("生成文件："+savePath);
    	FreemarkerUtil.createDoc(config.getFmkConf(),obj, "Service", savePath);
    	//实现类
    	File implDir=getSaveFilePath(module.getServicePackage()+File.separator+module.getServiceImplPackage(),config);
    	File implFile = new File(implDir,table.getEntityName()+"ServiceImpl.java");
    	String implPath =implFile.getAbsolutePath();
    	
    	FreemarkerUtil.createDoc(config.getFmkConf(),obj, "ServiceImpl", implPath);
    	log.info("生成文件："+implPath);
    }

    /**
     * 生成Controller
     * @param table
     * @param module
     */
    private void generateActionFile(JSONObject obj,Table table,Config config,Module module) throws Exception {
    	
    	File saveDir=getSaveFilePath(module.getControllerPackage(),config);
    	File saveFile = new File(saveDir,table.getEntityName()+"Controller.java");

    	String savePath =saveFile.getAbsolutePath();
    	FreemarkerUtil.createDoc(config.getFmkConf(),obj, "Controller", savePath);
    	log.info("生成文件："+savePath);
    }

   
    /**
     * 生成指定表对象对应的视图文件
     * @param table
     */
    private void generateViewFile(JSONObject obj,Table table,Config config,Module module) throws Exception{
    	String tpls = module.getAttrsMap().get("viewPackage_tpl");
    	String[] actions = tpls.split(",");
    	String type = module.getAttrsMap().get("viewPackage_type");
    	String suffix = "jsp";
    	if(!StringUtils.isBlank(type)){
    		suffix = type.toLowerCase();
    	}
    	File saveDir=getSaveFilePath(module.getViewPackage()+File.separator+table.getFstLowEntityName(),config);
    	for (String action : actions) {
	    	File saveFile = new File(saveDir,action+"_"+table.getEntityName()+"."+suffix);
	    	
	    	String savePath =saveFile.getAbsolutePath();
	    	log.info("生成文件："+savePath);
	    	FreemarkerUtil.createDoc(config.getFmkConf(),obj, action.trim(), savePath);
    	}
    }

	/**
	 * 生成自定义文件
	 * @param table
	 */
	private void generateCustomFile(JSONObject obj,Table table,Config config,Module module) throws Exception {
		String tpls = module.getAttrsMap().get("customPackage_tpl");
    	String[] actions = tpls.split(",");
    	String type = module.getAttrsMap().get("customPackage_type");
    	String suffix = "ctm";
    	if(!StringUtils.isBlank(type)){
    		suffix = type.toLowerCase();
    	}
		File saveDir=getSaveFilePath(module.getCustomPackage()+File.separator+table.getFstLowEntityName(),config);
		for (String action : actions) {
			
			File saveFile = new File(saveDir,action+"_"+table.getEntityName()+"."+suffix);
			
			String savePath =saveFile.getAbsolutePath();
			log.info("生成文件："+savePath);
			FreemarkerUtil.createDoc(config.getFmkConf(),obj, action.trim(), savePath);
		}
	}
   
    public static void main(String[] args) {  
        DataBase2File reverser = new DataBase2File(); 
        try {
			reverser.generateFiles("/"+GlobalConst.TEMPLATE_PATH+"/tpl_default_mybatis");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }  
      
}  


