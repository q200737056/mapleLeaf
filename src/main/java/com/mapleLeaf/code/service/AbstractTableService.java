package com.mapleLeaf.code.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mapleLeaf.code.model.Db;
import com.mapleLeaf.code.model.Module;
import com.mapleLeaf.code.model.Table;
import com.mapleLeaf.code.model.TableConf;
import com.mapleLeaf.code.utils.CodeUtil;

public abstract class AbstractTableService implements ITableService {
	
	protected Db db;
	public void setDb(Db db) {
		this.db = db;
	}
	
	
	/**
	 * 表注释
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public abstract String getTableRemark(String tableName, Connection con) throws SQLException;
	/**
     * 获取数据表的所有字段
     * @param table
     * @param conn
     * @throws SQLException
     */
    public abstract void getTableColumns(Table table,Module module,Connection conn,Map<String,String> map) throws SQLException;
	/**
	 * 统一实现
     * 获取指定表信息并封装成Table对象 
     * @param tbConf 
     * @param module
     * @param con 
     */  
    public Table getTable(TableConf tbConf,Module module, Connection con) throws SQLException {
    	String tableName =tbConf.getName();
        Table table = new Table(); 
      
        table.setExclude(tbConf.getExclude());
        table.setTableFullName(tableName);//表名
        table.setTableName(tableName);//如果去前缀,则去了前缀的 表名
        //自定义 表名前缀
        if (module.isDeleteTablePrefix() && !CodeUtil.isEmpty(tbConf.getPrefix())){
        	table.setTableName(tableName.toLowerCase().replaceFirst(tbConf.getPrefix().toLowerCase(), ""));
        //全局 表名前缀
        }else if(module.isDeleteTablePrefix() && !CodeUtil.isEmpty(module.getBaseTabPrefix())){
        	String[] prefixs = module.getBaseTabPrefix().split(",");
        	for(int i=0;i<prefixs.length;i++){
        		if(tableName.toLowerCase().indexOf(prefixs[i].trim().toLowerCase())==0){
        			table.setTableName(tableName.toLowerCase().replaceFirst(prefixs[i].trim().toLowerCase(), ""));
        			break;
        		}
        		
        	}
        }
        System.out.println("表名："+table.getTableFullName());
        
        Map<String,String> m = this.getTableUniqueIdx(tableName, con);
        //获取表各字段的信息
        getTableColumns(table,module,con,m);
        //去掉单主键配置
        
        /*table.setPrimaryKey(getTablePrimaryKey(tableName, con));
        table.setPrimaryKeyType(getColumnType(table, table.getPrimaryKey()));
        table.setPrimaryProperty(CodeUtil.convertToFirstLetterLowerCaseCamelCase(table.getPrimaryKey()));
        table.setPrimaryPropertyType(CodeUtil.convertType(table.getPrimaryKeyType()));
        table.setPrimaryCamelProperty(CodeUtil.convertToCamelCase(table.getPrimaryKey()));*/
        
        table.setRemark(getTableRemark(tableName, con));//表 注释
        //实体类名,如果没设置实体类名属性，则,首字母大写的驼峰命名 
        table.setEntityName(CodeUtil.isEmpty(tbConf.getEntityName())
        		?CodeUtil.convertToCamelCase(table.getTableName()):tbConf.getEntityName());
        //首字母小写的驼峰命名
        table.setFstLowEntityName(CodeUtil.convertToFstLowerCamelCase(table.getTableName()));
        
        //设置从表的entity属性
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
        	table.setSubTables(subTables);
        }
        return table;  
    }
    /**
	 * 统一实现
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
			String colName = rs.getString("column_name");
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
	 * 统一实现
	 * 获取表主键 字段
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public  String getTablePrimaryKey(String tableName, Connection con) throws SQLException{
		DatabaseMetaData dbMeta = con.getMetaData(); 
		ResultSet rs = dbMeta.getPrimaryKeys(null,null,tableName);
		String colName="";
		while (rs.next()){
			colName+=rs.getString("column_name")+",";
		}
		rs.close();
		return colName;
	}
}
