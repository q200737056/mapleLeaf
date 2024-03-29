package com.mapleLeaf.code.confbean;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import freemarker.template.Configuration;

public class Config {
	//文件存放默认路径
	private String baseDir; 
	//包路径的前缀，如com.test，后面则跟上模块名等
	private String basePackage;
	//表字段是否驼峰命名
	private boolean columnIsCamel=true;
	//java基本类型是否为包装
	private boolean wrapperClass=true;
	//表名前缀，可以多个
	private String baseTabPrefix;
	//全部表字段名前缀，可以多个
	private String baseColPrefix;
	//持久层框架
	private String persistence; 
	//连接数据库的配置信息
	private Db db;
	//要生成的代码模块列表
	private List<Module> modules; 
	//选择生成代码的模板
	private String tplName;
	 //公共类map
	private Map<String,List<String>> commonMap=new HashMap<>();
	
	private Configuration fmkConf;
	
	
	public String getPersistence() {
		return persistence;
	}
	public void setPersistence(String persistence) {
		this.persistence = persistence;
	}
	public Map<String, List<String>> getCommonMap() {
		return commonMap;
	}
	public void setCommonMap(Map<String, List<String>> commonMap) {
		this.commonMap = commonMap;
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
	public String getBaseColPrefix() {
		return baseColPrefix;
	}
	public void setBaseColPrefix(String baseColPrefix) {
		this.baseColPrefix = baseColPrefix;
	}
	public boolean isWrapperClass() {
		return wrapperClass;
	}
	public void setWrapperClass(boolean wrapperClass) {
		this.wrapperClass = wrapperClass;
	}
	
}
