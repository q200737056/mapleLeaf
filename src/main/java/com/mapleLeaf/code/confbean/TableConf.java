package com.mapleLeaf.code.confbean;

import java.util.ArrayList;
import java.util.List;
/**
 * 模块中表的定义
 * @author mars.liu
 *
 */
public class TableConf {
	private String name; //表名
	private String prefix;//表前缀
	//private String entityName;//配置的实体类名
	
	
	private String exclude;//排除指定模板的文件生成(dao,service,controller,view,custom,entity)
	
	private List<RefConf> refConfs = new ArrayList<>(); //关联表的信息
	
	private ColumnGroupConf colGroup;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getExclude() {
		return exclude;
	}
	public void setExclude(String exclude) {
		this.exclude = exclude;
	}
	public List<RefConf> getRefConfs() {
		return refConfs;
	}
	public void setRefConfs(List<RefConf> refConfs) {
		this.refConfs = refConfs;
	}
	public ColumnGroupConf getColGroup() {
		return colGroup;
	}
	public void setColGroup(ColumnGroupConf colGroup) {
		this.colGroup = colGroup;
	}
	
}
