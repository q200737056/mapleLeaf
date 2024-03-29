package com.mapleLeaf.code.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.mapleLeaf.code.confbean.CodeFileConf;
import com.mapleLeaf.code.confbean.ColumnConf;
import com.mapleLeaf.code.confbean.ColumnGroupConf;
import com.mapleLeaf.code.confbean.Config;
import com.mapleLeaf.code.confbean.Db;
import com.mapleLeaf.code.confbean.Module;
import com.mapleLeaf.code.confbean.RefConf;
import com.mapleLeaf.code.confbean.ReportConf;
import com.mapleLeaf.code.confbean.TableConf;
import com.mapleLeaf.code.utils.CodeUtil;
import com.mapleLeaf.code.utils.XmlUtil;
import com.mapleLeaf.common.util.FileTool;
import com.mapleLeaf.common.util.GlobalConst;

import freemarker.template.Configuration;

public class ConfigFactory {
	public static Config createConfig(String configFile,String tplPath) throws Exception{
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);  
	    configuration.setDefaultEncoding("utf-8");
	   
	    tplPath = FileTool.getRealPath(tplPath);
	    if(tplPath.replace("\\", "/").endsWith("/")){
	    	tplPath = tplPath.substring(0,tplPath.length()-1);
	    }
	    String basePath = tplPath.substring(0,tplPath.lastIndexOf("/")+1);
	    
	    try {
			configuration.setDirectoryForTemplateLoading(new File(basePath));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		Config config = new Config();
		
		config.setTplName(tplPath.substring(tplPath.lastIndexOf("/")+1,tplPath.length()));
		config.setFmkConf(configuration);
		
		String realPath = FileTool.getRealPath(configFile);
		Document doc=null;
		try {
			doc = XmlUtil.getDocument(new FileInputStream(realPath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		Element root = XmlUtil.getRootNode(doc);
		
		//加载  global标签
		Element global = XmlUtil.getChild(root, "global");
		
		if(global!=null){
			// property标签
			List<Element> props = XmlUtil.getChildElements(global,"property");
			Map<String,String> propMap = new HashMap<>();
			for(Element prop : props){
				String name = XmlUtil.getAttrValue(prop, "name", "");
				if(CodeUtil.isEmpty(name)){
					continue;
				}
				String value = XmlUtil.getAttrValue(prop, "value", "");
				propMap.put(name, value);
			}
			//生成文件路径 ,默认项目所在 目录
			String baseDir = CodeUtil.isEmpty(propMap.get("baseDir"))?
					System.getProperty("user.dir"):propMap.get("baseDir");
			config.setBaseDir(baseDir);
			//基础包名，默认 com.code
			String basePackage = CodeUtil.isEmpty(propMap.get("basePackage"))?
					"mapleLeaf.code":propMap.get("basePackage");
			config.setBasePackage(basePackage);
			//表字段是否下划线转驼峰命名  默认false
			if(!"".equals(propMap.get("columnCamel"))){
				config.setColumnIsCamel(Boolean.valueOf(propMap.get("columnCamel")));
			}
		    if(!"".equals(propMap.get("wrapperClass"))){
		    	config.setWrapperClass(Boolean.valueOf(propMap.get("wrapperClass")));
		    }
		    //字段名前缀 默认""
		    config.setBaseColPrefix(propMap.get("baseColPrefix"));
		    //表名前缀 默认""
		    config.setBaseTabPrefix(propMap.get("baseTabPrefix"));
		    //持久层框架 默认"mybatis"
		    if(CodeUtil.checkStrArray(GlobalConst.PERSISTENCE,propMap.get("persistence"))){
		    	config.setPersistence(propMap.get("persistence").toLowerCase());	
		    }else{
		    	config.setPersistence("mybatis");
		    }
		  	
		  	
		}
		
		//加载db配置
		initDb(config, root);
		
		//加载module
		List<Module> moduleList = new ArrayList<Module>();
		List<Element> modules = XmlUtil.getChildElements(root, "module");
		for (Element e : modules) {
			Module m = new Module();
			//全局参数 设置
			m.setColumnIsCamel(config.isColumnIsCamel());//字段是否驼峰命名，全局参数
			m.setWrapperClass(config.isWrapperClass());//java基本类型是否为包装类
			m.setPersistence(config.getPersistence());//持久层框架 ，全局参数
			m.setBaseTabPrefix(config.getBaseTabPrefix());
			m.setBaseColPrefix(config.getBaseColPrefix());
			
			m.setName(XmlUtil.getAttrValue(e, "name", ""));
			
			//加载模板中的包名
			initPackage(m, e);
			
			//加载自定义
			List<CodeFileConf> codeFileList = readCustomConfList(e);
			m.setCodeFiles(codeFileList);
			
			//加载自定义参数
			Element cust = XmlUtil.getChild(e, "customArea");
			if(cust!=null){
				
				List<Element> paramEle = XmlUtil.getChildElements(cust, "param");
				if(paramEle!=null&&paramEle.size()>0){
					for(int i=0;i<paramEle.size();i++){
						Attribute keyAttr = XmlUtil.getAttribute(paramEle.get(i), "key");//键
						Attribute valAttr = XmlUtil.getAttribute(paramEle.get(i), "value");//值
						
						if (keyAttr!=null&&valAttr!=null) {
						
							m.getParamMap().put(keyAttr.getValue().trim(), 
									valAttr.getValue().trim());
						}
					}
					
				}
			}
			
			
			//加载table
			
			List<TableConf> tableConfs = readTableConfList(e);
			for(int i=0;i<tableConfs.size();i++){
				TableConf item = tableConfs.get(i);
				if(item.getRefConfs().size()>0){
					for(RefConf ref : item.getRefConfs()){
						boolean errboo = true;
						for(TableConf tab :tableConfs){
							if(ref.getTabName().equalsIgnoreCase(tab.getTabName())){
								ref.setEntName(tab.getEntName());
								ref.setColGroup(tab.getColGroup());
								errboo = false;
								break;
							}
						}
						//不存在 ref 对应的 table的配置
						if(errboo){
							System.out.println("出错...................");
							throw new Exception("ref标签name属性，对应的table没配置");
						}
					}
				}
			}
			m.setTables(tableConfs);
			//加载report
			m.setReportConf(readReportConf(e));
			moduleList.add(m);
		}
		config.setModules(moduleList);
		
		return config;
	}
	/**
	 * 加载 db配置
	 * @throws Exception 
	 */
	private static void initDb(Config config,Element root) throws Exception{
		Element dbNode = XmlUtil.getChild(root, "db");
		if(dbNode==null){
			throw new Exception("未配置db");
		}
	
		Db db = new Db();
	
		db.setDbType(XmlUtil.getChildValue(dbNode, "dbType", null));
		db.setDriver(XmlUtil.getChildValue(dbNode, "driver", null));
		db.setCatalog(XmlUtil.getChildValue(dbNode, "catalog", null));
		db.setSchema(XmlUtil.getChildValue(dbNode, "schema", null));
		db.setUser(XmlUtil.getChildValue(dbNode, "user", null));
		db.setPwd(XmlUtil.getChildValue(dbNode, "pwd", null));
		db.setUrl(XmlUtil.getChildValue(dbNode, "url", null));
		
		if(CodeUtil.isEmpty(db.getDbType())||CodeUtil.isEmpty(db.getDriver())
				||CodeUtil.isEmpty(db.getUser())
				||CodeUtil.isEmpty(db.getPwd())||CodeUtil.isEmpty(db.getUrl())){
			throw new Exception("未配置db所有项");
		}
		
		config.setDb(db);
	}
	/**
	 * 加载 包名配置
	 * @throws Exception 
	 */
	private static void initPackage(Module m,Element e){
		Element node = XmlUtil.getChild(e, "package");
		if(node!=null){
			// property标签
			List<Element> props = XmlUtil.getChildElements(node,"property");
			Map<String,String> propMap = new HashMap<>();
			for(Element prop : props){
				String name = XmlUtil.getAttrValue(prop, "name", null);
				if(CodeUtil.isEmpty(name)){
					continue;
				}
				String value = XmlUtil.getAttrValue(prop, "value", null);
				propMap.put(name, value);
			}
			m.setControllerPackage(propMap.get("controllerPkg"));
			
			m.setServicePackage(propMap.get("servicePkg"));
			m.setServiceImplPackage("impl");
		
			m.setDaoPackage(propMap.get("daoPkg"));
			m.setDaoImplPackage("impl");
			
			m.setEntityPackage(propMap.get("entityPkg"));
			m.setMapperPackage(propMap.get("mapperPkg"));
		}
			
		
	}
	/**
	 * 读取customArea
	 */
	private static List<CodeFileConf> readCustomConfList(Element module){
		List<CodeFileConf> codeFiles = new ArrayList<>();
		Element e = XmlUtil.getChild(module, "customArea");
		if(e==null){
			return null;
		}
		List<Element> customs = XmlUtil.getChildElements(e, "codeFile");
		for(Element ele : customs){
			CodeFileConf codeFile = initCustomConf(ele);
			if(CodeUtil.isEmpty(codeFile.getCustomPackage())||
					CodeUtil.isEmpty(codeFile.getTpl())){
				continue;
			}
			codeFiles.add(codeFile);
		}
		return codeFiles;
	}
	/**
	 * 读取table标签 及 子标签
	 * @param module
	 * @return
	 */
	private static List<TableConf> readTableConfList(Element module){
		List<TableConf> tableList = new ArrayList<TableConf>();
		List<Element> tables = XmlUtil.getChildElements(module, "table");
		for (Element e : tables) {
			TableConf m = initTableConf(e);
			//排除表名空的
			if(CodeUtil.isEmpty(m.getTabName())){
				continue;
			}
			
			tableList.add(m);
		}
		return tableList;
	}
	
	
	/**
	 * table标签
	 * @param e
	 * @return
	 */
	private static  TableConf initTableConf(Element e){
		TableConf m = new TableConf();
		//表名 转小写
		m.setTabName(XmlUtil.getAttrValue(e, "tabName", "").toLowerCase());
		m.setExclude(XmlUtil.getAttrValue(e, "exclude", null));
		m.setEntName(XmlUtil.getAttrValue(e, "entName", ""));
		//读取 ref 标签
		List<Element> refs = XmlUtil.getChildElements(e, "ref");
		if(refs!=null && refs.size()>0){
			for(Element ref : refs){
				RefConf refConf = initRefConf(ref);
				//排除 表名空的
				if(CodeUtil.isEmpty(refConf.getTabName())){
					continue;
				}
				m.getRefConfs().add(refConf);
			}
		}
		//读取 columnGroup 标签
		Element colGroup = XmlUtil.getChild(e, "columnGroup");
		if(colGroup!=null){
			m.setColGroup(initColumnGroupConf(colGroup));
		}
		
		return m;
	}
	/**
	 * ref 标签
	 * @param e
	 * @return
	 */
	private static  RefConf initRefConf(Element e){
		RefConf m = new RefConf();
		
		m.setTabName(XmlUtil.getAttrValue(e, "tabName", null));
		
		Attribute refType =XmlUtil.getAttribute(e, "type");
		if (refType!=null) {
			String type = refType.getValue().trim();
			
			if(type.length()>=8 && "OneToOne`ManyToMany`ManyToOne`OneToMany"
					.contains(type)){
				m.setRefType(type);
				if(type.equals("ManyToMany")){//多对多有 中间表
					m.setMidTabName(XmlUtil.getAttrValue(e, "midTabName", null));
					m.setMidRefCol(XmlUtil.getAttrValue(e, "midRefCol", null));
				}else if(type.equals("OneToOne")||type.equals("ManyToOne")){
					m.setForKey(XmlUtil.getAttrValue(e, "forKey", null));//关联字段（外键）
				}
			}else{
				//其它的都默认OneToMany
				m.setRefType("OneToMany");
			}
		} else {
			//从表必需配置,否则默认OneToMany
			m.setRefType("OneToMany"); 
		}
		String  refColumns = e.getTextTrim();

		m.setRefColumns(refColumns);
		
		return m;
	}
	/**
	 * columnGroup标签
	 */
	private static ColumnGroupConf initColumnGroupConf(Element e){
		ColumnGroupConf cnf = new ColumnGroupConf();
		
		cnf.setExclude(XmlUtil.getAttrValue(e, "exclude", null));
		cnf.setSearchPos(XmlUtil.getAttrValue(e, "searchPos", null));
		cnf.setListPos(XmlUtil.getAttrValue(e, "listPos", null));
		cnf.setInputPos(XmlUtil.getAttrValue(e, "inputPos", null));
		
		//读取 column 标签
		List<Element> columns = XmlUtil.getChildElements(e, "column");
		if(columns!=null&&columns.size()>0){
			for(Element ele : columns){
				ColumnConf tmp = initColumnConf(ele);
				//排除字段名空的
				if(CodeUtil.isEmpty(tmp.getColName())){
					continue;
				}
				cnf.getColConfMap().put(tmp.getColName().toLowerCase(), tmp);
			}
		}
		
		return cnf;
	}
	/**
	 * column 标签
	 */
	private static ColumnConf initColumnConf(Element e){
		ColumnConf cnf = new ColumnConf();
		cnf.setColName(XmlUtil.getAttrValue(e, "colName", null));
		cnf.setLabelName(XmlUtil.getAttrValue(e, "labelName", null));
		cnf.setTagType(XmlUtil.getAttrValue(e, "tagType", null));
		cnf.setPropName(XmlUtil.getAttrValue(e, "propName", null));
		cnf.setMetaData(XmlUtil.getAttrValue(e, "metaData", null));
		cnf.setColValue(e.getTextTrim());
		
		return cnf;
	}
	/**
	 * codeFile标签
	 */
	private static CodeFileConf initCustomConf(Element e){
		CodeFileConf cnf = new CodeFileConf();
		cnf.setSuffix(XmlUtil.getAttrValue(e, "suffix", null));
		cnf.setCustomPackage(XmlUtil.getAttrValue(e, "customPkg", null));
		
		cnf.setTpl(e.getTextTrim());
		
		return cnf;
	}
	/**
	 * report标签
	 */
	private static ReportConf readReportConf(Element module){
		Element reportEle = XmlUtil.getChild(module, "report");
		ReportConf cnf = null;
		if(reportEle!=null) {
			cnf = new ReportConf();
			cnf.setSuffix(XmlUtil.getAttrValue(reportEle, "suffix", "doc"));
			cnf.setName(XmlUtil.getAttrValue(reportEle, "name", "清单"));
			cnf.setTpl(reportEle.getTextTrim());
		}
		return cnf;
	}
}
