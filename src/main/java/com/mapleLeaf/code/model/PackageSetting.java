package com.mapleLeaf.code.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义全局包名设置
 * @author 
 *
 */
public class PackageSetting {
	private String daoPackage;
	private String daoImplPackage;
	private String servicePackage;
	private String serviceImplPackage;
	private String entityPackage;
	private String mapperPackage;
	private String controllerPackage;
	private String viewPackage;
	private String customPackage;
	
	private Map<String,String> attrsMap=new HashMap<>();//标签属性集 key:标签名_属性名  value:属性值
	
	public String getDaoPackage() {
		return daoPackage;
	}
	public void setDaoPackage(String daoPackage) {
		this.daoPackage = daoPackage;
	}
	public String getDaoImplPackage() {
		return daoImplPackage;
	}
	public void setDaoImplPackage(String daoImplPackage) {
		this.daoImplPackage = daoImplPackage;
	}
	public String getServicePackage() {
		return servicePackage;
	}
	public void setServicePackage(String servicePackage) {
		this.servicePackage = servicePackage;
	}
	public String getServiceImplPackage() {
		return serviceImplPackage;
	}
	public void setServiceImplPackage(String serviceImplPackage) {
		this.serviceImplPackage = serviceImplPackage;
	}
	public String getEntityPackage() {
		return entityPackage;
	}
	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}
	public String getMapperPackage() {
		return mapperPackage;
	}
	public void setMapperPackage(String mapperPackage) {
		this.mapperPackage = mapperPackage;
	}
	public String getControllerPackage() {
		return controllerPackage;
	}
	public void setControllerPackage(String controllerPackage) {
		this.controllerPackage = controllerPackage;
	}
	public String getViewPackage() {
		return viewPackage;
	}
	public void setViewPackage(String viewPackage) {
		this.viewPackage = viewPackage;
	}

	public String getCustomPackage() {
		return customPackage;
	}
	public void setCustomPackage(String customPackage) {
		this.customPackage = customPackage;
	}
	public Map<String, String> getAttrsMap() {
		return attrsMap;
	}
	public void setAttrsMap(Map<String, String> attrsMap) {
		this.attrsMap = attrsMap;
	}
	
	
}
