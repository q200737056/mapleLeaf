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


public class MysqlTableService extends AbstractTableService {
	
	

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
            showTablesSql = "show tables like '"+pattern+"'";  // MySQL查询所有表格名称命令  
            ps = con.prepareStatement(showTablesSql);  
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
	@Override
    public List<Column> getTableColumns(String tableName,Module module,Connection conn) throws SQLException {
	    	
    		boolean isCamel = module.isColumnIsCamel();
    		
    		List<Column> cols = new ArrayList<>();
    		
			String sql="select * from information_schema.COLUMNS where TABLE_SCHEMA=? and TABLE_NAME=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,db.getDbName());
			ps.setString(2,tableName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Column col = new Column();
	        	String colName = rs.getString("column_name");
	        	col.setColName(colName);//字段名
	        	String type = rs.getString("data_type");
	        	
	        	col.setColType(CodeUtil.convertJdbcType(type,module.getPersistance()));//字段类型
	        	col.setRemark(rs.getString("column_comment"));//字段注释
	        	
	        	col.setPropName(isCamel?CodeUtil.convertToFstLowerCamelCase(colName)
	        			:colName);// 类属性名
	        	col.setPropType(CodeUtil.convertType(type));//属性类型
	        	col.setUpperPropName(isCamel?CodeUtil.convertToCamelCase(colName)
	        			:CodeUtil.converFirstUpper(colName));//类 属性名首字母大写
	        	col.setNullable(rs.getString("is_nullable").equals("YES"));//字段是否为空
	        	col.setLength(rs.getLong("character_maximum_length"));//字段长度
	        	col.setDefaultValue(rs.getString("column_default"));//字段默认值
	        	
	        	cols.add(col);
	        	
			}
			rs.close();
			ps.close();
		
		return cols;
    }
    
	@Override
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
	}

	

}
