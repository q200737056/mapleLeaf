package com.mapleLeaf.code.confbean;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import freemarker.template.Configuration;

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
	private String baseTabPrefix;//表名前缀，可以多个
	private String persistance; //持久层框架
	private Db db; //连接数据库的配置信息
	private List<Module> modules; //要生成的代码模块列表
	private String tplName;//选择生成代码的模板
	
	private Map<String,List<String>> commonMap=new HashMap<>(); //公共类map
	
	private Configuration fmkConf;
	
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
	public Configuration getFmkConf() {
		return fmkConf;
	}
	public void setFmkConf(Configuration fmkConf) {
		this.fmkConf = fmkConf;
	}
	public String getBaseTabPrefix() {
		return baseTabPrefix;
	}
	public void setBaseTabPrefix(String baseTabPrefix) {
		this.baseTabPrefix = baseTabPrefix;
	}
	public String getTplName() {
		return tplName;
	}
	public void setTplName(String tplName) {
		this.tplName = tplName;
	}
	
}
