package com.mapleLeaf.code.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.mapleLeaf.code.confbean.ColumnConf;
import com.mapleLeaf.code.confbean.ColumnGroupConf;
import com.mapleLeaf.code.confbean.Config;
import com.mapleLeaf.code.confbean.Db;
import com.mapleLeaf.code.confbean.Module;
import com.mapleLeaf.code.confbean.RefConf;
import com.mapleLeaf.code.confbean.TableConf;
import com.mapleLeaf.code.utils.XmlUtil;
import com.mapleLeaf.common.util.FileTool;

import freemarker.template.Configuration;

public class ConfigFactory {
	public static Config createConfig(String configFile,String tplPath) throws Exception{
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);  
	    configuration.setDefaultEncoding("utf-8");
	   
	    tplPath = FileTool.getRealPath(tplPath);
	    //configuration.setClassForTemplateLoading(ConfigFactory.class,tplPath);
	    try {
			configuration.setDirectoryForTemplateLoading(new File(tplPath));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		Config config = new Config();
		
		config.setFmkConf(configuration);
		
		String realPath = FileTool.getRealPath(configFile);
		Document doc=null;
		try {
			doc = XmlUtil.getDocument(new FileInputStream(realPath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		Element root = XmlUtil.getRootNode(doc);
		
		config.setBaseDir(XmlUtil.getChild(root, "baseDir").getTextTrim());//生成文件路径
		config.setBasePackage(XmlUtil.getChild(root, "basePackage").getTextTrim());//基础包名
		//表字段是否驼峰命名  默认false
		config.setColumnIsCamel(Boolean.valueOf(XmlUtil.getChildValue(root, "columnIsCamel","false")));
		//是否去掉表前缀  默认false
		config.setDeleteTablePrefix(Boolean.valueOf(XmlUtil.getChildValue(root, "isDeleteTablePrefix","false")));
		//表名前缀 默认""
		config.setBaseTabPrefix(XmlUtil.getChildValue(root, "baseTabPrefix",""));
		//持久层 框架
		config.setPersistance(XmlUtil.getChild(root, "persistance").getTextTrim());
		//公共类(包名+模板)非必需
		Element commonElm = XmlUtil.getChild(root, "common");
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
		//加载数据库配置
		Element dbNode = XmlUtil.getChild(root, "db");
		Db db = new Db();
		//db类型:mysql,oracle
		db.setDbType(XmlUtil.getChild(dbNode, "dbType").getTextTrim());
		db.setDriver(XmlUtil.getChild(dbNode, "driver").getTextTrim());
		db.setPwd(XmlUtil.getChild(dbNode, "pwd").getTextTrim());
		db.setUrl(XmlUtil.getChild(dbNode, "url").getTextTrim());
		db.setUser(XmlUtil.getChild(dbNode, "user").getTextTrim());
		db.setDbName(XmlUtil.getChild(dbNode, "dbName").getTextTrim());
		config.setDb(db);
		
		//加载module
		List<Module> moduleList = new ArrayList<Module>();
		List<Element> modules = XmlUtil.getChildElements(root, "module");
		for (Element e : modules) {
			Module m = new Module();
			//全局参数 设置
			m.setColumnIsCamel(config.isColumnIsCamel());//字段是否驼峰命名，全局参数
			m.setDeleteTablePrefix(config.isDeleteTablePrefix());//是否删除表名前缀，全局参数
			m.setPersistance(config.getPersistance());//持久层框架 ，全局参数
			m.setBaseTabPrefix(config.getBaseTabPrefix());
			
			//模块名 ,默认 空字符
			m.setName(XmlUtil.getChildValue(e, "name", ""));
			
			//加载模板中的 包名
			m.setControllerPackage(XmlUtil.getChildValue(e, "controllerPackage",null));
		
			m.setServicePackage(XmlUtil.getChildValue(e, "servicePackage",null));
			m.setServiceImplPackage("impl");
		
			m.setDaoPackage(XmlUtil.getChildValue(e, "daoPackage",null));
			m.setDaoImplPackage("impl");
			
			m.setEntityPackage(XmlUtil.getChildValue(e, "entityPackage",null));
		
			m.setMapperPackage(XmlUtil.getChildValue(e, "mapperPackage",null));
		
			
			Element elePkg = XmlUtil.getChild(e, "viewPackage");
			if(elePkg==null){
				m.setViewPackage(null);
				
			}else{
				m.setViewPackage(elePkg.getTextTrim());
				m.getAttrsMap().put("viewPackage_tpl", XmlUtil.attrValue(elePkg, "tpl"));
				m.getAttrsMap().put("viewPackage_suffix", XmlUtil.attrValue(elePkg, "suffix"));
			}
			elePkg = XmlUtil.getChild(e, "customPackage");
			if(elePkg==null){
				m.setCustomPackage(null);
				
			}else{
				m.setCustomPackage(elePkg.getTextTrim());
				m.getAttrsMap().put("customPackage_tpl", XmlUtil.attrValue(elePkg, "tpl"));
				m.getAttrsMap().put("customPackage_suffix", XmlUtil.attrValue(elePkg, "suffix"));
			}
			//加载自定义参数
			List<Element> paramEle = XmlUtil.getChildElements(e, "param");
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
	 * 读取table标签 及 子标签
	 * @param module
	 * @return
	 */
	private static List<TableConf> readTableConfList(Element module){
		List<TableConf> tableList = new ArrayList<TableConf>();
		List<Element> tables = XmlUtil.getChildElements(module, "table");//module可以包含多个table
		for (Element e : tables) {
			TableConf m = initTableConf(e);//读取 table 标签属性
			
			
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
		
		m.setEntityName(XmlUtil.getAttrValue(e, "entityName", null));//实体类型
		m.setName(XmlUtil.getAttrValue(e, "name", null));//表名
		m.setPrefix(XmlUtil.getAttrValue(e, "prefix", null));//前缀
		m.setExclude(XmlUtil.getAttrValue(e, "exclude", null));//实排除指定模板的文件生成
		
		//读取 ref 标签
		List<Element> refs = XmlUtil.getChildElements(e, "ref");
		if(refs!=null && refs.size()>0){
			for(Element ref : refs){
				RefConf refConf = initRefConf(ref);
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
		
		m.setRefName(XmlUtil.getAttrValue(e, "name", null));//关联表的表名
		
		Attribute refType =XmlUtil.getAttribute(e, "type");//关联 关系
		if (refType!=null) {
			String type = refType.getValue().trim();
			
			if(type.length()>=8 && "OneToOne`ManyToMany`ManyToOne`OneToMany"
					.contains(type)){
				m.setRefType(type);
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
		//如果  配置exclude了，就exclude生效
		String exclude = XmlUtil.getAttrValue(e, "exclude", null);
		if(exclude!=null){
			cnf.setExclude(exclude.toLowerCase());
		}else{
			String include = XmlUtil.getAttrValue(e, "include", null);
			cnf.setInclude(include==null?null:include.toLowerCase());
		}
		//读取 column 标签
		List<Element> columns = XmlUtil.getChildElements(e, "column");
		if(columns!=null&&columns.size()>0){
			for(Element ele : columns){
				ColumnConf tmp = initColumnConf(ele);
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
	
}
