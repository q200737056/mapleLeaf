package com.mapleLeaf.code.other;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
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
import com.mapleLeaf.code.model.Module;
import com.mapleLeaf.code.model.Table;
import com.mapleLeaf.code.model.TableConf;
import com.mapleLeaf.code.service.ITableService;
import com.mapleLeaf.code.utils.FreemarkerUtil;




@Component
public class DataBase2File {
	
	private Config config=null;
	private ITableService tableService;
	private Logger log =  LoggerFactory.getLogger(DataBase2File.class);
	public void loadConfig(){
		this.config = Config.loadConfig();
	}
	
	 
    /** 
     * 主方法，生成相关文件 
     * @throws IOException  
     * @throws ClassNotFoundException 
     * @throws SQLException 
     */  
    public void generateFiles() throws IOException, ClassNotFoundException, SQLException {
        log.info("Generating...");
        Long start=System.currentTimeMillis();
        tableService = TableServiceFactory.getInstance(config.getDb().getDbType());
        tableService.setConfig(config);
        Class.forName(config.getDb().getDriver());  
        Connection con = DriverManager.getConnection(config.getDb().getUrl(), config.getDb().getUser()
        		,config.getDb().getPwd());  
        for (Module module : config.getModules()) {
        	log.info("module="+module.getName());
        	
        	//如果没有配置数据表，则默认加载模块名为前缀的所有数据表
        	if (module.getTables()==null || module.getTables().isEmpty()) {
        		module.setTables(tableService.getAllTables(module.getName()+"%"));
        	}
        	
        	if (module.getTables()!=null) {
	        	for (TableConf tbConf : module.getTables()) {
	        		Table table = tableService.getTable(tbConf,module, con);//获取一个表的相关信息
	        		genFile(table, module);
	        	}
        	}
        	
        }
        con.close();
        //公共类生成
        if(!config.getCommonMap().isEmpty()){
        	for(Entry<String, List<String>> entry:config.getCommonMap().entrySet()){
        		String pak = entry.getKey();
        		List<String> templs = entry.getValue();
        		for(String item:templs){
        			generateCommonFile(pak,item);
        		}
        	}
        }
        
        Long end = System.currentTimeMillis();
        log.info("Generate Success! total time = "+(end-start));
        log.info("Please check: " + config.getBaseDir());
    } 
    
    /**
     * 递归生成所有文件代码，子表可以重叠
     * @param tb
     * @param module
     * @return
     */
    private StringBuffer genFile(Table tb,Module module) {
    	JSONObject obj = (JSONObject)JSON.toJSON(tb);
    	setBaseInfo(obj,module);
    	//获取指定排除的模板
    	String exclude = tb.getExclude();
    	List<String> excludeList = new ArrayList<String>();
    	if(!StringUtils.isBlank(exclude)){
    		String[] arr = exclude.split(",");
    		excludeList=Arrays.asList(arr);
    	}
    	if(!excludeList.contains("entity") &&
    			!StringUtils.isBlank(module.getEntityPackage())){
    		generateEntityFile(obj,tb, module);//生成entity
    	}
    	if(!excludeList.contains("dao") &&
    			!StringUtils.isBlank(module.getDaoPackage())){
    		generateDaoFile(obj,tb, module);//生成dao
    	}
    	if(!excludeList.contains("service") &&
    			!StringUtils.isBlank(module.getServicePackage())){
    		generateServiceFile(obj,tb, module);//生成service
    	}
    	if(!excludeList.contains("controller") &&
    			!StringUtils.isBlank(module.getControllerPackage())){
    		generateActionFile(obj,tb,module);//生成controller
    	}
    	
        if (!excludeList.contains("view") &&
        		!StringUtils.isBlank(module.getViewPackage())
        		&&module.getAttrsMap().get("viewPackage_tpl")!=null
        		&&!module.getAttrsMap().get("viewPackage_tpl").trim().equals("")) {
        	generateViewFile(obj,tb,module);//生成 view
			
        }
        if (!excludeList.contains("custom") &&
        		!StringUtils.isBlank(module.getCustomPackage())
        		&&module.getAttrsMap().get("customPackage_tpl")!=null
        		&&!module.getAttrsMap().get("customPackage_tpl").trim().equals("")){
        	generateCustomFile(obj,tb,module);//生成 自定义 文件
        }
        
        
        //如果有从表,从表文件生成,递归
    	if(tb.getSubTables().size()>0){
    		for(Table subtable:tb.getSubTables()){
    			
    			tb.setSubTables(null);//去掉 从表列表 ，防止转成json时，死循环
    			subtable.setParentTable(tb);//设置主表 
    			JSONObject subobj = (JSONObject)JSON.toJSON(subtable);
    	    	setBaseInfo(subobj,module);
    	    	
    	    	genFile(subtable,module);
    	    	
    		}
    	}
        
        
        StringBuffer sb = new StringBuffer();
        return sb;
    }
      
  
     /**
     * 将模块信息转换为json结构
     * @param obj
     * @param module
     */
    private void setBaseInfo(JSONObject obj,Module module) {
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
    }
    
    /**
     * 生成指定表对象对应的类文件 
     * @param table 
     */  
    private void generateEntityFile(JSONObject obj,Table table,Module module) {
    	
    	File saveDir=getSaveFilePath(module.getEntityPackage());
    	
    	File saveFile = new File(saveDir,table.getEntityName()+".java");
    	
    	String savePath =saveFile.getAbsolutePath();
    	FreemarkerUtil.createDoc(obj, "Entity", savePath);
    	log.info("生成文件："+savePath);
    	
    }
    /**
     * 生成公共类文件 
     * @param table 
     */  
    private void generateCommonFile(String pak,String tmpl) {
    	
    	File saveDir=getSaveFilePath(pak);
    	File saveFile = new File(saveDir,tmpl+".java");

    	String savePath =saveFile.getAbsolutePath();
    	FreemarkerUtil.createDoc(null, tmpl, savePath);
    	log.info("生成文件："+savePath);
    }

    /**
     * 根据模块定义生成文件保存目录
     * @param module
     * @param packageName
     * @return
     */
    private File getSaveFilePath(String packageName){
    	
		File saveDir = new File(config.getBaseDir()+ File.separator + config.getBasePackage().replace(".", File.separator)
				+File.separator+packageName.replace(".", File.separator));
    	
    	if (!saveDir.exists()) {
    		saveDir.mkdirs();
    	}
    	return saveDir;
    }
    
    /**
     * 生成指定表映射的RowMapper类文件，用于jdbcTemplate的查询
     * @param table
     */
    /*private void generateMapperFile(Table table,Module module) {
    	JSONObject obj = (JSONObject)JSON.toJSON(table);
    	setBaseInfo(obj,module);
    	File saveDir=getSaveFilePath(module.getDaoPackage()+File.separator+module.getMapperPackage());
    	File saveFile = new File(saveDir,table.getEntityName()+"RowMapper.java");

    	String savePath =saveFile.getAbsolutePath();
    	FreemarkerUtil.createDoc(obj, "RowMapper", savePath);
    	log.info("生成文件："+savePath);
    }*/

    /**
     * 生成dao文件
     * @param table
     */
    private void generateDaoFile(JSONObject obj,Table table,Module module) {
    
    	File saveDir=getSaveFilePath(module.getDaoPackage());
    	
    	String fileName = table.getEntityName()+"Dao.java";
    	if("mybatis".equals(module.getPersistance())){
    		fileName = table.getEntityName()+"Mapper.java";
    	}
    	File saveFile = new File(saveDir,fileName);
    	String savePath =saveFile.getAbsolutePath();
    	
    	FreemarkerUtil.createDoc(obj, "Dao", savePath);
    	log.info("生成文件："+savePath);
    	//dao实现类
    	if (module.getPersistance().toLowerCase()
    			.equals("hibernate")){
        	File implDir=getSaveFilePath(module.getDaoPackage()+File.separator+module.getDaoImplPackage());
	    	
	    	//generateMapperFile(table, module);
	    	File implFile = new File(implDir,table.getEntityName()+"DaoImpl.java");
	    	String implPath =implFile.getAbsolutePath();
	    	FreemarkerUtil.createDoc(obj, "DaoImpl", implPath);
	    	log.info("生成文件："+implPath);
    	}else if (module.getPersistance().toLowerCase()
    			.equals("mybatis")) {
    		/**
        	 * 如果是mybatis，则生成mytabis的xml配置文件
        	 */
    		saveDir=getSaveFilePath(module.getMapperPackage());
    		File implFile = new File(saveDir,table.getEntityName()+"Mapper.xml");
	    	String implPath =implFile.getAbsolutePath();
	    	FreemarkerUtil.createDoc(obj, "Mapper", implPath);
	    	log.info("生成文件："+implPath);
    	}else{
    		//log.info("该ORM框架不支持或不存在！");
    		File implDir=getSaveFilePath(module.getDaoPackage()+File.separator+module.getDaoImplPackage());
	    	
	    	File implFile = new File(implDir,table.getEntityName()+"DaoImpl.java");
	    	String implPath =implFile.getAbsolutePath();
	    	FreemarkerUtil.createDoc(obj, "DaoImpl", implPath);
	    	log.info("生成文件："+implPath);
    	}
    }

    /**
     * 生成service文件
     * @param table
     */
    private void generateServiceFile(JSONObject obj,Table table,Module module) {
    	
    	File saveDir=getSaveFilePath(module.getServicePackage());
    	File saveFile = new File(saveDir,table.getEntityName()+"Service.java");
    	String savePath =saveFile.getAbsolutePath();
    	log.info("生成文件："+savePath);
    	FreemarkerUtil.createDoc(obj, "Service", savePath);
    	//实现类
    	File implDir=getSaveFilePath(module.getServicePackage()+File.separator+module.getServiceImplPackage());
    	File implFile = new File(implDir,table.getEntityName()+"ServiceImpl.java");
    	String implPath =implFile.getAbsolutePath();
    	
    	FreemarkerUtil.createDoc(obj, "ServiceImpl", implPath);
    	log.info("生成文件："+implPath);
    }

    /**
     * 生成Controller
     * @param table
     * @param module
     */
    private void generateActionFile(JSONObject obj,Table table,Module module) {
    	
    	File saveDir=getSaveFilePath(module.getControllerPackage());
    	File saveFile = new File(saveDir,table.getEntityName()+"Controller.java");

    	String savePath =saveFile.getAbsolutePath();
    	FreemarkerUtil.createDoc(obj, "Controller", savePath);
    	log.info("生成文件："+savePath);
    }

    /**
     * 生成指定表对象对应的视图文件
     * @param table
     */
    /*private void generateViewFile(Table table,Module module) {
    	JSONObject obj = (JSONObject)JSON.toJSON(table);
    	setBaseInfo(obj,module);
    	File saveDir=getSaveFilePath(module,module.getViewPackage());
    	File saveFile = new File(saveDir,table.getEntityName()+".view.xml");

    	String savePath =saveFile.getAbsolutePath();
    	log.info("生成文件："+savePath);
    	FreemarkerUtil.createDoc(obj, "View", savePath);
    }*/

    /**
     * 生成指定表对象对应的视图文件
     * @param table
     */
    private void generateViewFile(JSONObject obj,Table table,Module module) {
    	String tpls = module.getAttrsMap().get("viewPackage_tpl");
    	String[] actions = tpls.split(",");
    	String type = module.getAttrsMap().get("viewPackage_type");
    	String suffix = "jsp";
    	if(!StringUtils.isBlank(type)){
    		suffix = type.toLowerCase();
    	}
    	File saveDir=getSaveFilePath(module.getViewPackage()+File.separator+table.getFstLowEntityName());
    	for (String action : actions) {
	    	File saveFile = new File(saveDir,action+"_"+table.getEntityName()+"."+suffix);
	    	
	    	String savePath =saveFile.getAbsolutePath();
	    	log.info("生成文件："+savePath);
	    	FreemarkerUtil.createDoc(obj, action.trim(), savePath);
    	}
    }

	/**
	 * 生成自定义文件
	 * @param table
	 */
	private void generateCustomFile(JSONObject obj,Table table,Module module) {
		String tpls = module.getAttrsMap().get("customPackage_tpl");
    	String[] actions = tpls.split(",");
    	String type = module.getAttrsMap().get("customPackage_type");
    	String suffix = "ctm";
    	if(!StringUtils.isBlank(type)){
    		suffix = type.toLowerCase();
    	}
		File saveDir=getSaveFilePath(module.getCustomPackage()+File.separator+table.getFstLowEntityName());
		for (String action : actions) {
			
			File saveFile = new File(saveDir,action+"_"+table.getEntityName()+"."+suffix);
			
			String savePath =saveFile.getAbsolutePath();
			log.info("生成文件："+savePath);
			FreemarkerUtil.createDoc(obj, action.trim(), savePath);
		}
	}
    
    /**
     * 生成dorado的公用model文件
     * @param table
     * @param module
     * @return
     */
    /*private String generateDoradoModelString(Table table,Module module) {
    	JSONObject obj = (JSONObject)JSON.toJSON(table);
    	setBaseInfo(obj,module);
    	String str = FreemarkerUtil.createString(obj,"DoradoModel");
    	//log.info(str);
    	return str;
    }*/
    
    /**
     * 写入model文件，如果model已存在，则增加内容
     * @param fileName
     * @param content
     * @throws IOException
     */
    private void writeModelFile(String fileName,String content) throws IOException {
    	FileOutputStream fos=null;
    	Writer out = null;
        try {
        	File dir = new File(config.getBaseDir()+ File.separator + "models");
        	if (!dir.exists()) {
        		dir.mkdirs();
        	}
        	File modelFile = new File(dir,fileName);
        	if (modelFile.exists()) {
        		//如果文件存在，则读取出文件，再增加参数中的内容后，再写回文件
        		String cont = readFile(modelFile);
        		content = cont.replace("</Model>", content+"\n</Model>");
        	} else {
        		content="<?xml version=\"1.0\" encoding=\"UTF-8\"?><Model>"+content+"</Model>";
        	}
        	//log.info(content);
            fos = new FileOutputStream(modelFile);  
            OutputStreamWriter oWriter = new OutputStreamWriter(fos,"UTF-8");  
            //这个地方对流的编码不可或缺，使用main（）单独调用时，应该可以，但是如果是web请求导出时导出后word文档就会打不开，并且包XML文件错误。主要是编码格式不正确，无法解析。  
            //out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));  
             out = new BufferedWriter(oWriter);   
        } catch (FileNotFoundException e1) {  
            e1.printStackTrace();  
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        out.write(content);
        out.close();
        fos.close();
        log.info("生成文件："+config.getBaseDir()+ File.separator + "models"+File.separator +fileName);
    }
    /**
     * 读取文件内容到一个字符串中
     * @param file
     * @return
     */
	private static String readFile(File file) {
		StringBuffer result = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(s);
				result.append("\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
    
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {  
        DataBase2File reverser = new DataBase2File(); 
        reverser.loadConfig();
        reverser.generateFiles();
    }  
      
}  


