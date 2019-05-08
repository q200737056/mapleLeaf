package com.mapleLeaf.code.other;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.mapleLeaf.code.model.Config;
import com.mapleLeaf.code.model.Db;
import com.mapleLeaf.code.model.Module;
import com.mapleLeaf.code.model.TableConf;
import com.mapleLeaf.code.utils.XmlUtil;

import freemarker.template.Configuration;

public class ConfigFactory {
	public static Config createConfig(String configFile,String tplPath){
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);  
	    configuration.setDefaultEncoding("utf-8");
	    configuration.setClassForTemplateLoading(ConfigFactory.class,tplPath);
	    
		Config config = new Config();
		
		config.setFmkConf(configuration);
		
		Document doc = XmlUtil.getDocument(Config.class.getClassLoader().getResourceAsStream(configFile));
		
		Element root = XmlUtil.getRootNode(doc);
		
		config.setBaseDir(XmlUtil.getChild(root, "baseDir").getTextTrim());//生成文件路径
		config.setBasePackage(XmlUtil.getChild(root, "basePackage").getTextTrim());//基础包名
		
		config.setColumnIsCamel(Boolean.valueOf(XmlUtil.getChild(root, "columnIsCamel").getTextTrim()));//表字段是否驼峰命名
		config.setDeleteTablePrefix(Boolean.valueOf(XmlUtil.getChild(root, "isDeleteTablePrefix").getTextTrim()));
		config.setPersistance(XmlUtil.getChild(root, "persistance").getTextTrim());//持久层 框架
		//公共类(包名+模板)非必需
		Element commonElm = XmlUtil.getChild(root, "common");
		if(commonElm!=null){
			String common = commonElm.getTextTrim();
			if(!common.equals("")){
				String[] commons = common.split(",");
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
			
			
			//模块名
			m.setName(XmlUtil.getChild(e, "name").getTextTrim());
			//加载自定义包名
			Element elePkg = XmlUtil.getChild(e, "controllerPackage");
			m.setControllerPackage(elePkg==null?null:elePkg.getTextTrim());
			elePkg = XmlUtil.getChild(e, "servicePackage");
			m.setServicePackage(elePkg==null?null:elePkg.getTextTrim());
			m.setServiceImplPackage("impl");
			elePkg = XmlUtil.getChild(e, "daoPackage");
			m.setDaoPackage(elePkg==null?null:elePkg.getTextTrim());
			m.setDaoImplPackage("impl");
			
			elePkg = XmlUtil.getChild(e, "entityPackage");
			m.setEntityPackage(elePkg==null?null:elePkg.getTextTrim());
			elePkg = XmlUtil.getChild(e, "mapperPackage");
			m.setMapperPackage(elePkg==null?null:elePkg.getTextTrim());
		
			
			elePkg = XmlUtil.getChild(e, "viewPackage");
			if(elePkg==null){
				m.setViewPackage(null);
				
			}else{
				m.setViewPackage(elePkg.getTextTrim());
				m.getAttrsMap().put("viewPackage_tpl", XmlUtil.attrValue(elePkg, "tpl"));
				m.getAttrsMap().put("viewPackage_type", XmlUtil.attrValue(elePkg, "type"));
			}
			elePkg = XmlUtil.getChild(e, "customPackage");
			if(elePkg==null){
				m.setCustomPackage(null);
				
			}else{
				m.setCustomPackage(elePkg.getTextTrim());
				m.getAttrsMap().put("customPackage_tpl", XmlUtil.attrValue(elePkg, "tpl"));
				m.getAttrsMap().put("customPackage_type", XmlUtil.attrValue(elePkg, "type"));
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
			m.setTables(readTableConfList(e));
			moduleList.add(m);
		}
		config.setModules(moduleList);
		
		return config;
	}
	
	/**
	 * 以递归方式读取主从表关系
	 * @param module
	 * @return
	 */
	private static List<TableConf> readTableConfList(Element module){
		List<TableConf> tableList = new ArrayList<TableConf>();
		List<Element> tables = XmlUtil.getChildElements(module, "table");//module可以包含多个table,table中可包含多个table
		for (Element e : tables) {
			TableConf m = initTableConf(e);//读取 table 标签属性
			List<TableConf> subTables = readTableConfList(e);//读取table标签 的table子标签
			if (subTables!=null && !subTables.isEmpty()) {
				m.getSubTables().addAll(subTables);
			}
			tableList.add(m);
		}
		return tableList;
	}
	/**
	 * 初始化配置表信息
	 * @param e
	 * @return
	 */
	private static  TableConf initTableConf(Element e){
		TableConf m = new TableConf();
		Attribute attr = XmlUtil.getAttribute(e, "entityName");//实体类型
		if (attr!=null) {
			m.setEntityName(attr.getValue().trim());
		}
		Attribute name = XmlUtil.getAttribute(e, "name");//表名
		if (name!=null) {
			m.setName(name.getValue().trim());
		}
		Attribute prefix = XmlUtil.getAttribute(e, "prefix");//前缀
		if (prefix!=null) {
			m.setPrefix(prefix.getValue().trim());
		}
		Attribute refColumns = XmlUtil.getAttribute(e, "refColumns");//关联的字段，主表字段=从表字段 多个逗号分隔
		if (refColumns!=null) {
			m.setRefColumns(refColumns.getValue().trim());
		}
		
		Attribute refType =XmlUtil.getAttribute(e, "refType");//关联 关系
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
		Attribute exclude =XmlUtil.getAttribute(e, "exclude");//排除指定模板的文件生成
		if(exclude!=null){
			m.setExclude(exclude.getValue().trim());
		}
		return m;
	}
	
}
