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

import com.mapleLeaf.code.confbean.ColumnConf;
import com.mapleLeaf.code.confbean.ColumnGroupConf;
import com.mapleLeaf.code.confbean.Config;
import com.mapleLeaf.code.confbean.CodeFileConf;
import com.mapleLeaf.code.confbean.Db;
import com.mapleLeaf.code.confbean.Module;
import com.mapleLeaf.code.confbean.RefConf;
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
	    //configuration.setClassForTemplateLoading(ConfigFactory.class,tplPath);
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
		    config.setColumnIsCamel(Boolean.valueOf(propMap.get("columnCamel")));
		    //是否去掉表前缀  默认false
		  	config.setDeleteTablePrefix(Boolean.valueOf(propMap.get("deleteTabPrefix")));
		    //表名前缀 默认""
		    config.setBaseTabPrefix(propMap.get("baseTabPrefix"));
		    //持久层框架 默认"mybatis"
		    if(CodeUtil.checkStrArray(GlobalConst.PERSISTENCE,propMap.get("persistence"))){
		    	config.setPersistence(propMap.get("persistence").toLowerCase());	
		    }else{
		    	config.setPersistence("mybatis");
		    }
		  	
		  	
			//公共类(包名+模板)非必需
			Element commonElm = XmlUtil.getChild(global, "common");
			if(commonElm!=null){
				String common = commonElm.getTextTrim();
				if(!common.equals("")){
					String[] commons = common.replace("，", ",").split(",");
					for(int i=0;i<commons.length;i++){
						String[] items = commons[i].split("=");
						List<String> v=config.getCommonMap().get(items[0].trim());
						if(v==null||v.isEmpty()){
							v = new ArrayList<>();
							v.add(items[1].trim());
							config.getCommonMap().put(items[0].trim(), v);
						}else{
							v.add(items[1].trim());
						}
					}
				}
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
			m.setDeleteTablePrefix(config.isDeleteTablePrefix());//是否删除表名前缀，全局参数
			m.setPersistence(config.getPersistence());//持久层框架 ，全局参数
			m.setBaseTabPrefix(config.getBaseTabPrefix());
			
			//模块名 ,默认 空字符
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
							if(ref.getRefName().equalsIgnoreCase(tab.getName())){
								ref.setEntityName(tab.getEntityName());
								ref.setPrefix(tab.getPrefix());
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
		db.setDbName(XmlUtil.getChildValue(dbNode, "dbName", null));
		db.setUser(XmlUtil.getChildValue(dbNode, "user", null));
		db.setPwd(XmlUtil.getChildValue(dbNode, "pwd", null));
		db.setUrl(XmlUtil.getChildValue(dbNode, "url", null));
		
		if(CodeUtil.isEmpty(db.getDbType())||CodeUtil.isEmpty(db.getDriver())
				||CodeUtil.isEmpty(db.getDbName())||CodeUtil.isEmpty(db.getUser())
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
		List<Element> tables = XmlUtil.getChildElements(module, "table");//module可以包含多个table
		for (Element e : tables) {
			TableConf m = initTableConf(e);//读取 table 标签属性
			//排除表名空的
			if(CodeUtil.isEmpty(m.getName())){
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
		
		m.setEntityName(XmlUtil.getAttrValue(e, "entName", null));//实体类型
		m.setName(XmlUtil.getAttrValue(e, "tabName", "").toLowerCase());//表名 转小写
		m.setPrefix(XmlUtil.getAttrValue(e, "prefix", null));//前缀
		m.setExclude(XmlUtil.getAttrValue(e, "exclude", null));//实排除指定模板的文件生成
		
		//读取 ref 标签
		List<Element> refs = XmlUtil.getChildElements(e, "ref");
		if(refs!=null && refs.size()>0){
			for(Element ref : refs){
				RefConf refConf = initRefConf(ref);
				//排除 表名空的
				if(CodeUtil.isEmpty(refConf.getRefName())){
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
		
		m.setRefName(XmlUtil.getAttrValue(e, "tabName", null));//关联表的表名
		
		Attribute refType =XmlUtil.getAttribute(e, "type");//关联 关系
		if (refType!=null) {
			String type = refType.getValue().trim();
			
			if(type.length()>=8 && "OneToOne`ManyToMany`ManyToOne`OneToMany"
					.contains(type)){
				m.setRefType(type);
				if(type.equals("ManyToMany")){//多对多有 中间表
					m.setMidName(XmlUtil.getAttrValue(e, "midTabName", null));
					m.setMidRefCol(XmlUtil.getAttrValue(e, "midRefCol", null));
				}
			}else{
				m.setRefType("OneToMany");//其它的都默认OneToMany
			}
		} else {
			m.setRefType("OneToMany"); //从表必需配置,否则默认OneToMany
		}
		String  refColumns = e.getTextTrim();
		//关联的字段，主表字段=从表字段 多个逗号分隔
		m.setRefColumns(refColumns);
		
		return m;
	}
	/**
	 * columnGroup标签
	 */
	private static ColumnGroupConf initColumnGroupConf(Element e){
		ColumnGroupConf cnf = new ColumnGroupConf();
		
		cnf.setExclude(XmlUtil.getAttrValue(e, "exclude", null));
		
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
		
		//形式 k1=v1,k2=v2  k为字段值，v为值的描述
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
		
		//模板 多个逗号分隔
		cnf.setTpl(e.getTextTrim());
		
		return cnf;
	}
	
}
