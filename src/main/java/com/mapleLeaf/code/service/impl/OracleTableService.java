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



public class OracleTableService implements ITableService {
	
	private Config config;
	public void setConfig(Config config) {
		this.config = config;
	}

	/* 
     * 连接数据库获取所有表信息 
     */  
    public List<TableConf> getAllTables(String pattern) {  
        if (CodeUtil.isEmpty(pattern)) {
        	pattern="*";
        }
        List<TableConf> tbConfList = new ArrayList<TableConf>();
        Connection con = null;  
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {  
            Class.forName(config.getDb().getDriver());  
            con = DriverManager.getConnection(config.getDb().getUrl(), config.getDb().getUser(),config.getDb().getPwd());  
            // 获取所有表名  
            String showTablesSql = "";  
            showTablesSql = "select table_name from user_tables where table_name like ? and owner=upper(?)"; // ORACLE查询所有表格名称命令  
            ps = con.prepareStatement(showTablesSql);
            ps.setString(1, pattern);
            ps.setString(2, config.getDb().getUser());
            rs = ps.executeQuery();  
              
            // 循环生成所有表的表信息
            while(rs.next()) {  
                if(rs.getString(1)==null) continue;  
                TableConf cf = new TableConf();
                cf.setName(rs.getString(1));
                tbConfList.add(cf);
            }  
              
            rs.close();  
            ps.close(); 
            con.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
          
        return tbConfList;  
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
        table.setTableFullName(tableName);
        table.setTableName(tableName);
        if (module.isDeleteTablePrefix() && !CodeUtil.isEmpty(tbConf.getPrefix())){
        	table.setTableName(tableName.toLowerCase().replaceFirst(tbConf.getPrefix().toLowerCase(), ""));  
        }
        System.out.println("表名："+table.getTableFullName());
        
        Map<String,String> m = this.getTableUniqueIdx(tableName, con);
        //获取表各字段的信息
        getTableColumns(table,con,m);
        
       /* table.setPrimaryKey(getTablePrimaryKey(tableName, con));
        table.setPrimaryProperty(CodeUtil.convertToFirstLetterLowerCaseCamelCase(table.getPrimaryKey())); 
        table.setPrimaryKeyType(getColumnType(table, table.getPrimaryKey()));
        table.setPrimaryPropertyType(CodeUtil.convertType(table.getPrimaryKeyType()));
        table.setPrimaryCamelProperty(CodeUtil.convertToCamelCase(table.getPrimaryKey()));*/
        
        table.setRemark(getTableRemark(tableName, con));
        table.setEntityName(CodeUtil.isEmpty(tbConf.getEntityName())?CodeUtil.convertToCamelCase(table.getTableName()):tbConf.getEntityName());
        table.setFstLowEntityName(CodeUtil.convertToFirstLetterLowerCaseCamelCase(table.getTableName()));
        
        //设置子表的entity属性
        if (!tbConf.getSubTables().isEmpty()) {
        	List<Table> subTables = new ArrayList<Table>();
        	for (TableConf tc : tbConf.getSubTables()) {
        		Table tb = getTable(tc,module,con);
        		//tb.setParentProperty(CodeUtil.convertToFirstLetterLowerCaseCamelCase(tc.getParentField()));
        		//tb.setParentProperty(tc.getParentField());
        		
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
        		
        		tb.setRefType(tc.getRefType());
        		subTables.add(tb);
        	}
        	table.setSubTables(subTables);
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
    	Map<String,List<Column>> index = new HashMap<>();//索引
    	
		
		//查询所有字段
		String sql="SELECT USER_TAB_COLS.TABLE_NAME, USER_TAB_COLS.COLUMN_NAME , "
			+"USER_TAB_COLS.DATA_TYPE, "
			+"USER_TAB_COLS.DATA_LENGTH , "
			+" USER_TAB_COLS.NULLABLE, "
			+" USER_TAB_COLS.COLUMN_ID, "
			+" user_tab_cols.data_default,"
			+"    user_col_comments.comments " 
			+"FROM USER_TAB_COLS  "
			+"inner join user_col_comments on "
			+" user_col_comments.TABLE_NAME=USER_TAB_COLS.TABLE_NAME " 
			+"and user_col_comments.COLUMN_NAME=USER_TAB_COLS.COLUMN_NAME " 
			+"where  USER_TAB_COLS.Table_Name=upper(?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1,table.getTableFullName());
		ResultSet	rs = ps.executeQuery();
			while (rs.next()) {
				Column col = new Column();
	        	String colName = rs.getString("column_name");
	        	col.setColumnName(colName);
	        	String type = rs.getString("data_type").toUpperCase();
	            type=CodeUtil.convertJdbcType(type);
	        	col.setColumnType(type);
	        	col.setPropertyName(isCamel?CodeUtil.convertToFirstLetterLowerCaseCamelCase(colName)
	        			:colName);
	        	
	        	col.setPropertyType(CodeUtil.convertType(col.getColumnType()));
	        	col.setFstUpperProName(isCamel?CodeUtil.convertToCamelCase(colName):
	        		CodeUtil.converFirstUpper(colName));
	        	col.setLength(rs.getLong("data_length"));
	        	col.setNullable(rs.getString("nullable").equals("YES") || rs.getString("nullable").equals("Y"));
	        	col.setDefaultValue(rs.getString("data_default"));
	        	col.setRemark(rs.getString("comments"));
	        	
	       
	        	/*if (col.getPropertyType().indexOf(".")!=-1 && !CodeUtil.existsType(table.getImportClassList(),col.getPropertyType())) {
	        		table.getImportClassList().add(col.getPropertyType());
	        	}*/
	        	if ("Date,BigDecimal".contains(col.getPropertyType())
	        			&& !CodeUtil.existsType(table.getImportClassList(),col.getPropertyType())) {
	        		table.getImportClassList().add(CodeUtil.convertClassType(col.getPropertyType()));//需要导入的类 的集合
	        	}
	        	
	        	//判断字段是否主键
	        	if(this.isPrimaryKey(pkCols, colName)){
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
    /**
     * 判断是否是主键
     * @param priCols 主键列表
     * @param columnName 要判断的列名
     * @return
     */
    private boolean isPrimaryKey(List<String> priCols,String columnName){
    	for (String pri : priCols) {
    		if (pri.equalsIgnoreCase(columnName)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public String getTablePrimaryKey(String tableName, Connection con) throws SQLException{
		//DatabaseMetaData dbMeta = con.getMetaData(); 
		//ResultSet rs = dbMeta.getPrimaryKeys(null,null,tableName);
		String sql="select a.constraint_name,a.column_name from user_cons_columns a, user_constraints b  where a.constraint_name = b.constraint_name  and b.constraint_type = 'P' and a.table_name = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, tableName.toUpperCase());
		ResultSet rs = stmt.executeQuery();
		String columnName="";
		while (rs.next()){
			columnName +=(rs.getString("COLUMN_NAME"))+",";
		}
		rs.close();
		return columnName;
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
	 * 主键类型
	 * @param tableName
	 * @param column 指定列名
	 * @return
	 * @throws SQLException
	 */
	/*public String getColumnType(Table table,String column) throws SQLException{
		String colType="";
		for (Column col : table.getColumns()) {
			if (col.getColumnName().equalsIgnoreCase(column)) {
				return col.getColumnType();
			}
		}
		return colType;
	}*/
	/**
	 * 表注释
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public String getTableRemark(String tableName, Connection con) throws SQLException {
		String remark="";
		String sql="SELECT COMMENTS FROM USER_TAB_COMMENTS WHERE table_name=upper(?)";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, tableName);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			remark=rs.getString("comments");
		}
		rs.close();
		ps.close();
		return remark;
	}

}
