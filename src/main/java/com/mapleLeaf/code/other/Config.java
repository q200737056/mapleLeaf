package com.mapleLeaf.code.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.mapleLeaf.code.model.Db;
import com.mapleLeaf.code.model.Module;
import com.mapleLeaf.code.model.PackageSetting;
import com.mapleLeaf.code.model.TableConf;
import com.mapleLeaf.code.utils.XmlUtil;

/**
 * 读取配置文件
 * @author
 *
 */
public class Config {
	private String baseDir; //文件存放默认路径
	private String basePackage; //包路径的前缀，如com.test，后面则跟上模块名等
	private boolean columnIsCamel;//表字段是否驼峰命名
	private boolean isDeleteTablePrefix;//是否删除表名前缀
	private String persistance; //持久层框架
	private Db db; //连接数据库的配置信息
	private List<Module> modules; //要生成的代码模块列表
	
	private Map<String,List<String>> commonMap=new HashMap<>(); //公共类map
	
	
	
	public String getPersistance() {
		return persistance;
	}
	public void setPersistance(String persistance) {
		this.persistance = persistance;
	}
	public Map<String, List<String>> getCommonMap() {
		return commonMap;
	}
	public void setCommonMap(Map<String, List<String>> commonMap) {
		this.commonMap = commonMap;
	}
	public boolean isDeleteTablePrefix() {
		return isDeleteTablePrefix;
	}
	public void setDeleteTablePrefix(boolean isDeleteTablePrefix) {
		this.isDeleteTablePrefix = isDeleteTablePrefix;
	}
	public boolean isColumnIsCamel() {
		return columnIsCamel;
	}
	public void setColumnIsCamel(boolean columnIsCamel) {
		this.columnIsCamel = columnIsCamel;
	}
	public String getBaseDir() {
		return baseDir;
	}
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
	public String getBasePackage() {
		return basePackage;
	}
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
	public Db getDb() {
		return db;
	}
	public void setDb(Db db) {
		this.db = db;
	}
	public List<Module> getModules() {
		return modules;
	}
	public void setModules(List<Module> modules) {
		this.modules = modules;
	}
	
	
	@Override
	public String toString() {
		return "Config [baseDir=" + baseDir + ", basePackage=" + basePackage
				+ ", db=" + db + ", modules=" + modules + "]";
	}
	public static Config loadConfig(){
		Config cfg = new Config();
		String configFile = "config.xml";
		Document doc = XmlUtil.getDocument(Config.class.getClassLoader().getResourceAsStream(configFile));
		
		Element root = XmlUtil.getRootNode(doc);
		
		cfg.setBaseDir(XmlUtil.getChild(root, "baseDir").getTextTrim());//生成文件路径
		cfg.setBasePackage(XmlUtil.getChild(root, "basePackage").getTextTrim());//基础包名
		
		cfg.setColumnIsCamel(Boolean.valueOf(XmlUtil.getChild(root, "columnIsCamel").getTextTrim()));//表字段是否驼峰命名
		cfg.setDeleteTablePrefix(Boolean.valueOf(XmlUtil.getChild(root, "isDeleteTablePrefix").getTextTrim()));
		cfg.setPersistance(XmlUtil.getChild(root, "persistance").getTextTrim());//持久层 框架
		//公共类(包名+模板)非必需
		Element commonElm = XmlUtil.getChild(root, "common");
		if(commonElm!=null){
			String common = commonElm.getTextTrim();
			if(!common.equals("")){
				String[] commons = common.split(",");
				for(int i=0;i<commons.length;i++){
					String[] items = commons[i].split("=");
					List<String> v=cfg.getCommonMap().get(items[0].trim());
					if(v==null||v.isEmpty()){
						v = new ArrayList<>();
						v.add(items[1].trim());
						cfg.getCommonMap().put(items[0].trim(), v);
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
		cfg.setDb(db);
		
		//加载module
		List<Module> moduleList = new ArrayList<Module>();
		List<Element> modules = XmlUtil.getChildElements(root, "module");
		for (Element e : modules) {
			Module m = new Module();
			//全局参数 设置
			m.setColumnIsCamel(cfg.isColumnIsCamel());//字段是否驼峰命名，全局参数
			m.setDeleteTablePrefix(cfg.isDeleteTablePrefix());//是否删除表名前缀，全局参数
			m.setPersistance(cfg.getPersistance());//持久层框架 ，全局参数
			
			
			//模块名  如没配置table，则查 name前缀的所有表,必需
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
			//加载table
			m.setTables(readTableConfList(e));
			moduleList.add(m);
		}
		cfg.setModules(moduleList);
		return cfg;
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
	private static TableConf initTableConf(Element e){
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
