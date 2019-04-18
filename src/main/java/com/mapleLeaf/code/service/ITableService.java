/**
 * 
 */
package com.mapleLeaf.code.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.mapleLeaf.code.model.Db;
import com.mapleLeaf.code.model.Module;
import com.mapleLeaf.code.model.Table;
import com.mapleLeaf.code.model.TableConf;


/**
 * 获取数据表和数据字段信息接口
 * @author 
 *
 */
public interface ITableService {

	void setDb(Db db);
	
	
	 /**
     * 获取指定表信息并封装成Table对象 
     * @param tbConf 
     * @param module
     * @param con 
     */
	Table getTable(TableConf tbConf,Module module, Connection con) throws SQLException;
	
	/**
     * 获取数据表的所有字段
     * @param table
     * @param conn
     * @throws SQLException
     */
	//void getTableColumns(Table table,Connection conn) throws SQLException;
	
	/**
	 * 获取表主键
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	String getTablePrimaryKey(String tableName, Connection con) throws SQLException;
	/**
     * 获取 表中 所有 唯一索引(包含主键)
     * @param tableName
     * @param con
     * @return
     * @throws SQLException
     */
	Map<String,String> getTableUniqueIdx(String tableName, Connection con) throws SQLException;
	
	
	/**
	 * 表注释
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	//String getTableRemark(String tableName, Connection con) throws SQLException;
}
