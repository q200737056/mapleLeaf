package com.mapleLeaf.code.service.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mapleLeaf.code.model.Column;
import com.mapleLeaf.code.model.Module;
import com.mapleLeaf.code.model.Table;
import com.mapleLeaf.code.model.TableConf;
import com.mapleLeaf.code.other.Config;
import com.mapleLeaf.code.service.ITableService;
import com.mapleLeaf.code.utils.CodeUtil;


public class PostgresqlTableService implements ITableService {
	
	private Config config;
	public void setConfig(Config config) {
		this.config = config;
	}

	
      
    /**
     * 获取指定表信息并封装成Table对象 
     * @param tbConf 
     * @param module
     * @param con 
     */  
    public Table getTable(TableConf tbConf,Module module, Connection con) throws SQLException {
    	String tableName =tbConf.getName();
        Table table = new Table(); 
        table.setModule(module);
        table.setExclude(tbConf.getExclude());
        table.setTableFullName(tableName);//表名
        table.setTableName(tableName);//如果去前缀,则去了前缀的 表名
        if (module.isDeleteTablePrefix() && !CodeUtil.isEmpty(tbConf.getPrefix())){
        	table.setTableName(tableName.toLowerCase().replaceFirst(tbConf.getPrefix(), ""));  
        }
        System.out.println("表名："+table.getTableFullName());
        
        Map<String,String> m = this.getTableUniqueIdx(tableName, con);
        //获取表各字段的信息
        getTableColumns(table,con,m);
       
        
        table.setRemark(getTableRemark(tableName, con));//表 注释
        //实体类名,如果没设置实体类名属性，则,首字母大写的驼峰命名 
        table.setEntityName(CodeUtil.isEmpty(tbConf.getEntityName())
        		?CodeUtil.convertToCamelCase(table.getTableName()):tbConf.getEntityName());
        //首字母小写的驼峰命名
        table.setFstLowEntityName(CodeUtil.convertToFstLowerCamelCase(table.getTableName()));
        //设置子表的entity属性
        if (!tbConf.getSubTables().isEmpty()) {
        	List<Table> subTables = new ArrayList<Table>();
        	for (TableConf tc : tbConf.getSubTables()) {
        		Table tb = getTable(tc,module,con);
        		
        		//主从表关联字段
        		String refColumn = tc.getRefColumns();
        		if(!StringUtils.isBlank(refColumn)){
        			Map<String,String> refColumnMap = new HashMap<>();
        			Map<String,String> refPropertyMap = new HashMap<>();
        			String[] refColumns = refColumn.split(",");
        			for(String item:refColumns){
        				String[] itemMap = item.split("=");
        				refColumnMap.put(itemMap[0].trim(), itemMap[1].trim());
        				if(module.isColumnIsCamel()){//是否驼峰命名
        					refPropertyMap.put(CodeUtil.convertToCamelCase(itemMap[0].trim()),
        							CodeUtil.convertToCamelCase(itemMap[1].trim()));
        				}else{
        					refPropertyMap.put(itemMap[0].trim(), itemMap[1].trim());
        				}
        			}
        			tb.setRefColumnMap(refColumnMap);
        			tb.setRefPropertyMap(refPropertyMap);
        		}
        		
        		tb.setRefType(tc.getRefType());//关联 方式
        		subTables.add(tb);
        	}
        	table.setSubTables(subTables);//子表
        }
        return table;  
    } 
    
    /**
     * 获取数据表的所有字段
     * @param table
     * @param conn
     * @throws SQLException
     */
    public void getTableColumns(Table table,Connection conn,Map<String,String> map) throws SQLException {
	    	String pks = getTablePrimaryKey(table.getTableFullName(),conn);
	    	List<String> pkCols = Arrays.asList(pks.split(","));
	    	
    		boolean isCamel = table.getModule().isColumnIsCamel();
    	
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
	        
	        	col.setColumnType(CodeUtil.convertJdbcType(type,table.getModule().getPersistance()));//字段类型
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
		DatabaseMetaData dbMeta = con.getMetaData(); 
		ResultSet rs = dbMeta.getPrimaryKeys(null,null,tableName);
		String colName="";
		while (rs.next()){
			colName+=(rs.getString("column_name"))+",";
		}
		rs.close();
		return colName;
	}
    /**
     * 获取 表中 所有 唯一索引(包含主键)
     * @param tableName
     * @param con
     * @return
     * @throws SQLException
     */
    public Map<String,String> getTableUniqueIdx(String tableName, Connection con) throws SQLException{
    	Map<String,String> map = new HashMap<>();
		DatabaseMetaData dbMeta = con.getMetaData(); 
		ResultSet rs = dbMeta.getIndexInfo(null, null, tableName, true, false);
		while (rs.next()){
			String idxName = rs.getString("index_name");
			String colName = (rs.getString("column_name"));
			String v = map.get(idxName);
			if(v==null){
				map.put(idxName, colName+",");
			}else{
				map.put(idxName, v+colName+",");
			}
		}
		rs.close();
		return map;
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
