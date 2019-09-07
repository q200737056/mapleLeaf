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



public class SqlServerTableService extends AbstractTableService {
	
	

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
            showTablesSql = "SELECT [name] FROM sys.objects ds  where type='U' and [name] like '"+pattern+"'";
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
    	//查询表主键
    	StringBuffer sb = new StringBuffer();
    	sb.append(" SELECT  cast(CASE WHEN col.colorder = 1 THEN obj.name ELSE '' END as varchar(100)) AS table_name , ");
    	sb.append("         cast(col.colorder as int) AS column_id ,  ");
    	sb.append("         col.name AS column_name ,  ");
    	sb.append("         cast (ISNULL(ep.[value], '') as varchar(100)) AS comments ,  ");
    	sb.append("         t.name AS data_type ,  ");
    	sb.append("         cast (col.length as int) AS data_length ,  ");
    	sb.append("         cast(ISNULL(COLUMNPROPERTY(col.id, col.name, 'Scale'), 0) as int) AS precision ,  ");
    	sb.append("         cast (CASE WHEN COLUMNPROPERTY(col.id, col.name, 'IsIdentity') = 1 THEN 'Y' ELSE '' END as varchar(3)) AS seq ,  ");
    	sb.append("         cast ( CASE WHEN EXISTS ( SELECT   1 FROM     dbo.sysindexes si  ");
    	sb.append("         INNER JOIN dbo.sysindexkeys sik ON si.id = sik.id AND si.indid = sik.indid  ");
    	sb.append("         INNER JOIN dbo.syscolumns sc ON sc.id = sik.id AND sc.colid = sik.colid  ");
    	sb.append("         INNER JOIN dbo.sysobjects so ON so.name = si.name AND so.xtype = 'PK'  ");
    	sb.append("         WHERE sc.id = col.id AND sc.colid = col.colid ) THEN 'Y' ELSE '' END as varchar(3)) AS prim ,  ");
    	sb.append("         cast (CASE WHEN col.isnullable = 1 THEN 'Y' ELSE '' END as varchar(3)) AS nullable ,  ");
    	sb.append("         cast (ISNULL(comm.text, '') as varchar(30)) AS data_default  ");
    	sb.append(" FROM dbo.syscolumns col  ");
    	sb.append("         LEFT  JOIN dbo.systypes t ON col.xtype = t.xusertype  ");
    	sb.append("         inner JOIN dbo.sysobjects obj ON col.id = obj.id AND obj.xtype = 'U' AND obj.status >= 0  ");
    	sb.append("         LEFT  JOIN dbo.syscomments comm ON col.cdefault = comm.id  ");
    	sb.append("         LEFT  JOIN sys.extended_properties ep ON col.id = ep.major_id AND col.colid = ep.minor_id AND ep.name = 'MS_Description'  ");
    	sb.append("         LEFT  JOIN sys.extended_properties epTwo ON obj.id = epTwo.major_id AND epTwo.minor_id = 0 AND epTwo.name = 'MS_Description'  ");
    	sb.append(" WHERE   obj.name = ? ORDER BY col.colorder ; ");
		String sql=sb.toString();
		PreparedStatement ps;
		ResultSet rs;
		
			ps = conn.prepareStatement(sql);
			ps.setString(1,table.getTableFullName());
			rs = ps.executeQuery();
			while (rs.next()) {
				Column col = new Column();
	        	String colName = rs.getString("column_name");
	        	col.setColumnName(colName);
	        	String type = rs.getString("data_type");
	        	
	        	col.setColumnType(CodeUtil.convertJdbcType(type,module.getPersistance()));
	        	col.setRemark(rs.getString("comments"));
	        	
	        	col.setPropertyName(isCamel?CodeUtil.convertToFstLowerCamelCase(colName)
	        			:colName);
	        	col.setPropertyType(CodeUtil.convertType(type));
	        	col.setFstUpperProName(isCamel?CodeUtil.convertToCamelCase(colName)
	        			:CodeUtil.converFirstUpper(colName));
	        	col.setNullable(rs.getString("nullable").equals("Y"));
	        	col.setLength(rs.getLong("data_length"));
	        	col.setDefaultValue(rs.getString("data_default"));
	        	
	        	
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
		String sql="SELECT cast (ds.value as varchar(100)) comments FROM sys.extended_properties ds LEFT "
				+ "JOIN sysobjects tbs ON ds.major_id=tbs.id WHERE  ds.minor_id=0 and tbs.name=?";
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
