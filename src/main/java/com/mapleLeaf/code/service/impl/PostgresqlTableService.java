package com.mapleLeaf.code.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mapleLeaf.code.model.Column;
import com.mapleLeaf.code.model.Module;
import com.mapleLeaf.code.model.Table;
import com.mapleLeaf.code.model.TableConf;
import com.mapleLeaf.code.service.AbstractTableService;
import com.mapleLeaf.code.utils.CodeUtil;


public class PostgresqlTableService extends AbstractTableService {
	
	
 
    /**
     * 获取指定表信息并封装成Table对象 
     * @param tbConf 
     * @param module
     * @param con 
     */  
    public Table getTable(TableConf tbConf,Module module, Connection con) throws SQLException {
    	return super.getTable(tbConf, module, con);
    } 
    
    /**
     * 获取数据表的所有字段
     * @param table
     * @param conn
     * @throws SQLException
     */
    public void getTableColumns(Table table,Module module,Connection conn,Map<String,String> map) throws SQLException {
	    	String pks = getTablePrimaryKey(table.getTableFullName(),conn);
	    	List<String> pkCols = Arrays.asList(pks.split(","));
	    	
    		boolean isCamel = module.isColumnIsCamel();
    	
    		Map<String,List<Column>> index = new HashMap<>();//唯一索引，主键
    		
			String sql="select A.*,B.column_comment from information_schema.COLUMNS A left join"+
					" (SELECT col_description(a.attrelid,a.attnum) as column_comment,a.attname as name"+
					" FROM pg_class as c,pg_attribute as a where c.relname=? and a.attrelid=c.oid"+
					" and a.attnum>0) B on A.column_name=B.name"+
					" where A.TABLE_NAME=?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,table.getTableFullName());
			ps.setString(2,table.getTableFullName());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Column col = new Column();
	        	String colName = rs.getString("column_name");
	        	col.setColumnName(colName);//字段名
	        	String type = rs.getString("data_type");
	        
	        	col.setColumnType(CodeUtil.convertJdbcType(type,module.getPersistance()));//字段类型
	        	col.setRemark(rs.getString("column_comment"));//字段注释
	        	
	        	col.setPropertyName(isCamel?CodeUtil.convertToFstLowerCamelCase(colName)
	        			:colName);// 类属性名
	        	col.setPropertyType(CodeUtil.convertType(type));//属性类型
	        	col.setFstUpperProName(isCamel?CodeUtil.convertToCamelCase(colName)
	        			:CodeUtil.converFirstUpper(colName));//类 属性名首字母大写
	        	col.setNullable(rs.getString("is_nullable").equals("YES"));//字段是否为空
	        	col.setLength(rs.getLong("character_maximum_length"));//字段长度
	        	col.setDefaultValue(rs.getString("column_default"));//字段默认值
	        	
	        	
	        	if ("Date,BigDecimal".contains(col.getPropertyType())
	        			&& !CodeUtil.existsType(table.getImportClassList(),col.getPropertyType())) {
	        		table.getImportClassList().add(CodeUtil.convertClassType(col.getPropertyType()));//需要导入的类 的集合
	        	}
	        	
	        	//判断字段是否主键
	        	if(CodeUtil.isPrimaryKey(pkCols, colName)){
	        		col.setPk(true);
	        	}
	        	table.getColumns().add(col);
	        	
	        	//唯一索引,主键
	        	if(!map.isEmpty()){
	        		for(Map.Entry<String, String> entry:map.entrySet()){
	        			String v = entry.getValue();
	        			String k = entry.getKey();
	        			if(v.contains(colName)){
	        				if(index.get(k)!=null){
	        					index.get(k).add(col);
	        				}else{
	        					List<Column> columns = new ArrayList<>();
	        					columns.add(col);
	        					index.put(k, columns);
	        				}
	        				
	        			}
	        			
	        			
	        		}
	        	}
			}
			if(!index.isEmpty()){
				table.setUniIdxMap(index);//唯一索引集
			}
			
			rs.close();
			ps.close();
		
    }
   
    public String getTablePrimaryKey(String tableName, Connection con) throws SQLException{
    	return super.getTablePrimaryKey(tableName, con);
	}
    /**
     * 获取 表中 所有 唯一索引(包含主键)
     * @param tableName
     * @param con
     * @return
     * @throws SQLException
     */
    public Map<String,String> getTableUniqueIdx(String tableName, Connection con) throws SQLException{
    	return super.getTableUniqueIdx(tableName, con);
	}
	
	/**
	 * 表注释
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public String getTableRemark(String tableName, Connection con) throws SQLException {
		String remark="";
		String sql="select cast(obj_description(relfilenode,'pg_class') as varchar) as comment "
				+ "from pg_class where relkind = 'r' and relname=?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, tableName);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			remark=rs.getString("comment");
		}
		rs.close();
		ps.close();
		return remark;
	}

}
