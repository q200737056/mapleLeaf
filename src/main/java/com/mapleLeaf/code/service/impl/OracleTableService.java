package com.mapleLeaf.code.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mapleLeaf.code.confbean.Module;
import com.mapleLeaf.code.confbean.TableConf;
import com.mapleLeaf.code.model.Column;
import com.mapleLeaf.code.model.Table;
import com.mapleLeaf.code.service.AbstractTableService;
import com.mapleLeaf.code.utils.CodeUtil;



public class OracleTableService extends AbstractTableService {
	
	

	/* 
     * 连接数据库获取所有表信息 
     */  
    /*public List<TableConf> getAllTables(String pattern) {  
        if (CodeUtil.isEmpty(pattern)) {
        	pattern="*";
        }
        List<TableConf> tbConfList = new ArrayList<TableConf>();
        Connection con = null;  
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {  
            Class.forName(db.getDriver());  
            con = DriverManager.getConnection(db.getUrl(), db.getUser(),db.getPwd());  
            // 获取所有表名  
            String showTablesSql = "";  
            showTablesSql = "select table_name from user_tables where table_name like ? and owner=upper(?)"; // ORACLE查询所有表格名称命令  
            ps = con.prepareStatement(showTablesSql);
            ps.setString(1, pattern);
            ps.setString(2, db.getUser());
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
    } */ 
      
    /**
     * 获取指定表信息并封装成Table对象 
     * @param tbConf 
     * @param module
     * @param con 
     */ 
	@Override
    public Table getTable(TableConf tbConf,Module module, Connection con) throws SQLException {
    	return super.getTable(tbConf, module, con);
    } 
    
    /**
     * 获取数据表的所有字段
     * @param table
     * @param conn
     * @throws SQLException
     */
   /* public void getTableColumns(Table table,Module module,Connection conn,Map<String,String> map) throws SQLException {
    	String pks = getTablePrimaryKey(table.getTableFullName(),conn);
    	List<String> pkCols = Arrays.asList(pks.split(","));
    	
    	boolean isCamel = module.isColumnIsCamel();
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
	        	String type = rs.getString("data_type");
	         
	        	col.setColumnType(CodeUtil.convertJdbcType(type,module.getPersistance()));
	        	col.setPropertyName(isCamel?CodeUtil.convertToFstLowerCamelCase(colName)
	        			:colName);
	        	
	        	col.setPropertyType(CodeUtil.convertType(type));
	        	col.setFstUpperProName(isCamel?CodeUtil.convertToCamelCase(colName):
	        		CodeUtil.converFirstUpper(colName));
	        	col.setLength(rs.getLong("data_length"));
	        	col.setNullable(rs.getString("nullable").equals("YES") || rs.getString("nullable").equals("Y"));
	        	col.setDefaultValue(rs.getString("data_default"));
	        	col.setRemark(rs.getString("comments"));
	        	
	       
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
				
    }*/
    /**
     * 获取数据表的所有字段
     * @param table
     * @param conn
     * @throws SQLException
     */
    @Override
    public List<Column> getTableColumns(String tableName,Module module,Connection conn) throws SQLException {
	    	
    		boolean isCamel = module.isColumnIsCamel();
    		
    		List<Column> cols = new ArrayList<>();
    		
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
			ps.setString(1,tableName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Column col = new Column();
	        	String colName = rs.getString("column_name");
	        	col.setColumnName(colName);
	        	String type = rs.getString("data_type");
	         
	        	col.setColumnType(CodeUtil.convertJdbcType(type,module.getPersistance()));
	        	col.setPropertyName(isCamel?CodeUtil.convertToFstLowerCamelCase(colName)
	        			:colName);
	        	
	        	col.setPropertyType(CodeUtil.convertType(type));
	        	col.setFstUpperProName(isCamel?CodeUtil.convertToCamelCase(colName):
	        		CodeUtil.converFirstUpper(colName));
	        	col.setLength(rs.getLong("data_length"));
	        	col.setNullable(rs.getString("nullable").equals("YES") || rs.getString("nullable").equals("Y"));
	        	col.setDefaultValue(rs.getString("data_default"));
	        	col.setRemark(rs.getString("comments"));
	        	
	        	cols.add(col);
	        	
			}
			rs.close();
			ps.close();
		
		return cols;
    }
    @Override
    public String getTablePrimaryKey(String tableName, Connection con) throws SQLException{
		//DatabaseMetaData dbMeta = con.getMetaData(); 
		//ResultSet rs = dbMeta.getPrimaryKeys(null,null,tableName);
		String sql="select a.constraint_name,a.column_name from user_cons_columns a, user_constraints b  "
				+ "where a.constraint_name = b.constraint_name  and b.constraint_type = 'P' and a.table_name = ?";
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
    @Override
    public Map<String,String> getTableUniqueIdx(String tableName, Connection con) throws SQLException{
    	return super.getTableUniqueIdx(tableName, con);
	}
	
	/**
	 * 表注释
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
    @Override
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
