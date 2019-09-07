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

public class InformixTableService extends AbstractTableService {
	
	

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
            String showTablesSql = "select * from systables where owner=? and tabname like '"+pattern+"%'";  
           
            ps = con.prepareStatement(showTablesSql);  
            ps.setString(1, db.getDbName());
            rs = ps.executeQuery();  
              
            // 循环生成所有表的表信息
            while(rs.next()) {  
                if(rs.getString(1)==null) continue;  
                TableConf cf = new TableConf();
                cf.setName(rs.getString("tabname"));//设置 表名
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
    		
			String sql="SELECT c.* FROM syscolumns c, systables t WHERE c.tabid=t.tabid AND t.tabname=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,table.getTableFullName());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Column col = new Column();
	        	String colName = rs.getString("colname");//字段名
	        	col.setColumnName(colName);
	        	int type = rs.getInt("coltype");//字段类型
	        	String type_str=Informixconvert(type);
	        	col.setColumnType(CodeUtil.convertJdbcType(type_str, module.getPersistance()));
	        	col.setRemark(null);//没有
	        	col.setPropertyName(isCamel?CodeUtil.convertToFstLowerCamelCase(colName)
	        			:colName);//属性 就是 字段名
	        	col.setPropertyType(CodeUtil.convertType(type_str));//属性 类型
	        	col.setFstUpperProName(isCamel?CodeUtil.convertToCamelCase(colName)
	        			:CodeUtil.converFirstUpper(colName));//首字母大写
	        	col.setNullable(true);//默认 true
	        	col.setLength((long)rs.getInt("collength"));//字段长度
	        	col.setDefaultValue("");
	        	
	        	
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
	/*public String getTableRemark(String tableName, Connection con) throws SQLException {
		String remark="";
		String sql="show table status where name=?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, tableName);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			remark=rs.getString("comment");
		}
		rs.close();
		ps.close();
		return remark;
	}*/
	/**
	 * 字段类型转换
	 * @param type
	 * @return
	 */
	private String Informixconvert(int type){
		switch (type) {
		case 0:
			return "CHAR";
		case 2:
			return "INTEGER";
		case 5:
			return "DECIMAL";
		case 13:
			return "VARCHAR";
		case 256:
			return "CHAR";
		case 258:
			return "INTEGER";
		case 261:
			return "DECIMAL";
		case 7:
			return "DATE";
		case 269:
			return "VARCHAR";
		default:
			break;
		}
    	return "VARCHAR";
	}
	/*private String convertJavaType(String databaseType){
		  
        String javaType = "";  
          
        String databaseTypeStr = databaseType.trim().toLowerCase();
        if(databaseTypeStr.startsWith("int")) {  
            javaType = "int";  
        } else if(databaseTypeStr.equals("char")) {  
            javaType = "String";  
        } else if(databaseTypeStr.indexOf("varchar")!=-1) {  
            javaType = "String";  
        } else if(databaseTypeStr.equals("decimal")) {  
            javaType = "BigDecimal";  
        }  else if(databaseTypeStr.equals("date")) {  
            javaType = "String";  
        } else {
            javaType = "String";  
        }  
          
        return javaType;  
    
	}*/

	@Override
	public String getTableRemark(String tableName, Connection con) throws SQLException {
		// TODO Auto-generated method stub
		return "";
	}

}
