package com.mapleLeaf.code.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mapleLeaf.code.confbean.ColumnConf;
import com.mapleLeaf.code.confbean.ColumnGroupConf;
import com.mapleLeaf.code.confbean.Db;
import com.mapleLeaf.code.confbean.Module;
import com.mapleLeaf.code.confbean.RefConf;
import com.mapleLeaf.code.confbean.TableConf;
import com.mapleLeaf.code.model.Column;
import com.mapleLeaf.code.model.RefTable;
import com.mapleLeaf.code.model.Table;
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
	public abstract List<Column> getTableColumns(String tableName,Module module,Connection conn) throws SQLException;
	/**
	 * 统一实现
     * 获取指定表信息并封装成Table对象 
     * @param tbConf 
     * @param module
     * @param con 
     */  
    public Table getTable(TableConf tbConf,Module module, Connection con) throws SQLException {
    	
    	//封装 table 数据
        Table table = renderTable(tbConf, module,con);
      
        System.out.println("表名："+table.getTabName());
        
        //是否有关联表
        if (!tbConf.getRefConfs().isEmpty()) {
        	List<RefTable> refTables = new ArrayList<RefTable>();
        	
        	for (RefConf rc : tbConf.getRefConfs()) {
        		//封装 关联表 数据
        		RefTable refTable = renderRefTable(rc, module, con);
        		
        		
        		refTables.add(refTable);
        	}
        	table.setRefTables(refTables);//设置 关联表
        }
        return table;  
    }
    /**
     * 封装 表数据
     */
    protected Table renderTable(TableConf tbConf,Module module,Connection conn) 
    		throws SQLException{
    	 //表名
    	 String tableName =tbConf.getName();
    	 //去掉前缀 的表名
    	 String mvPrefix=tbConf.getName();
    	 //得到 唯一主键 ，索引
    	 Map<String,String> map = this.getTableUniqueIdx(tableName, conn);
    	 
    	 Table table = new Table(); 
    	 
		 table.setExclude(tbConf.getExclude());
		 table.setTabName(tableName);//表名
		
		 //自定义 表名前缀
		 if (module.isDeleteTablePrefix() && !CodeUtil.isEmpty(tbConf.getPrefix())){
		 	mvPrefix = tableName.toLowerCase().replaceFirst(tbConf.getPrefix().toLowerCase(), "");
		 //全局 表名前缀
		 }else if(module.isDeleteTablePrefix() && !CodeUtil.isEmpty(module.getBaseTabPrefix())){
		 	String[] prefixs = module.getBaseTabPrefix().replace("，", ",").split(",");
		 	for(int i=0;i<prefixs.length;i++){
		 		if(tableName.toLowerCase().indexOf(prefixs[i].trim().toLowerCase())==0){
		 			mvPrefix = tableName.toLowerCase().replaceFirst(prefixs[i].trim().toLowerCase(), "");
		 			break;
		 		}
		 		
		 	}
		 }
		 table.setRemark(getTableRemark(tableName, conn));//表 注释
	      //实体类名,如果没设置实体类名属性，则,首字母大写的驼峰命名 
         table.setEntName(CodeUtil.isEmpty(tbConf.getEntityName())
        		?CodeUtil.convertToCamelCase(mvPrefix):tbConf.getEntityName());
        //首字母小写的驼峰命名
         table.setLowEntName(CodeUtil.convertToFstLowerCamelCase(mvPrefix));
    	
    	Map<String,List<Column>> index = new HashMap<>();//唯一索引，主键
    	//获取字段
    	List<Column> cols = getTableColumns(tableName, module, conn);
    	
    	//主键字段集
    	String pks = getTablePrimaryKey(table.getTabName(),conn);
    	List<String> pkCols = Arrays.asList(pks.split(","));
    	
    	for(Column col : cols){
    		
    		if(!CodeUtil.isEmpty(col.getRemark())){
				//字段 文本,表单类型,字段标识 默认 COMMENT中取  
    			//COMMENT约定形式(字段文本;表单类型;val1:text1,val2:text2;)
    			String[] arrTemp = col.getRemark().replace("；", ";").split(";");
				col.setLabelName(arrTemp[0].trim());
				if(arrTemp.length>1){
					col.setTagType(arrTemp[1].trim());
				}
				if(arrTemp.length>2){
					Map<String,String> colValMap = CodeUtil.splitKeyVal(
							arrTemp[2].trim().replace("：", ":"), ":");
					if(colValMap!=null){
						col.setColValueMap(colValMap);
					}
				}
			}
    		
    		ColumnGroupConf colGoup = tbConf.getColGroup();
    		if(colGoup!=null){
    			col = renderColumn(colGoup, col);
    			if(col==null){
    				continue;
    			}
    		}
    		
    		
    		//需要导入的类 的集合
    		if ("Date,BigDecimal".contains(col.getPropType())
        			&& !CodeUtil.existsType(table.getImpClasses(),col.getPropType())) {
        		table.getImpClasses().add(CodeUtil.convertClassType(col.getPropType()));
        	}
    		//判断字段是否主键
        	if(CodeUtil.isPrimaryKey(pkCols, col.getColName())){
        		col.setPk(true);
        	}
        	
        	//加入集合
        	table.getColumns().add(col);
        	
        	//唯一索引,主键
        	if(!map.isEmpty()){
        		for(Map.Entry<String, String> entry:map.entrySet()){
        			String v = entry.getValue();
        			String k = entry.getKey();
        			if(v.contains(col.getColName())){
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
    	
    	return table;
    }
    /**
     * 封装 关联表数据
     * @param tbConf
     * @param module
     * @param conn
     * @return
     */
    protected RefTable renderRefTable(RefConf refCof,Module module,Connection conn) throws SQLException{
    	RefTable ref = new RefTable();
    	//表名
    	String tableName = refCof.getRefName();
    	//去掉前缀 的表名
   	 	String mvPrefix=refCof.getRefName();
   	 	
    	ref.setTabName(tableName);//表名
    	
		 //自定义 表名前缀
		 if (module.isDeleteTablePrefix() && !CodeUtil.isEmpty(refCof.getPrefix())){
			 mvPrefix=tableName.toLowerCase().replaceFirst(refCof.getPrefix().toLowerCase(), "");
		 //全局 表名前缀
		 }else if(module.isDeleteTablePrefix() && !CodeUtil.isEmpty(module.getBaseTabPrefix())){
		 	String[] prefixs = module.getBaseTabPrefix().replace("，", ",").split(",");
		 	for(int i=0;i<prefixs.length;i++){
		 		if(tableName.toLowerCase().indexOf(prefixs[i].trim().toLowerCase())==0){
		 			mvPrefix=tableName.toLowerCase().replaceFirst(prefixs[i].trim().toLowerCase(), "");
		 			break;
		 		}
		 		
		 	}
		 }
		//实体类名,如果没设置实体类名属性，则,首字母大写的驼峰命名 
         ref.setEntName(CodeUtil.isEmpty(refCof.getEntityName())
        		?CodeUtil.convertToCamelCase(mvPrefix):refCof.getEntityName());
        //首字母小写的驼峰命名
         ref.setLowEntName(CodeUtil.convertToFstLowerCamelCase(mvPrefix));
		 
        //获取字段  
     	List<Column> cols = getTableColumns(tableName, module, conn);
     	
     	String pks = getTablePrimaryKey(ref.getTabName(),conn);
    	List<String> pkCols = Arrays.asList(pks.split(","));
    	
     	for(Column col : cols){
     		
     		if(!CodeUtil.isEmpty(col.getRemark())){
				//字段 文本 默认 COMMENT中取   COMMENT形式(字段文本;val1:text1,val2:text2;)
     			String[] arrTemp = col.getRemark().replace("；", ";").split(";");
				col.setLabelName(arrTemp[0].trim());
				if(arrTemp.length>2){
					Map<String,String> colValMap = CodeUtil.splitKeyVal(
							arrTemp[1].trim().replace("：", ":"), ":");
					if(colValMap!=null){
						col.setColValueMap(colValMap);
					}
				}
			}
     		
    		ColumnGroupConf colGoup = refCof.getColGroup();
    		if(colGoup!=null){
    			col = renderColumn(colGoup, col);
    			if(col==null){
    				continue;
    			}
    		}
     		//判断字段是否主键
        	if(CodeUtil.isPrimaryKey(pkCols, col.getColName())){
        		col.setPk(true);
        	}
        	ref.getColumns().add(col);
     	}
     	
     	//关联字段
		String refColumn = refCof.getRefColumns();
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
			ref.setRefColMap(refColumnMap);//关联的字段
			ref.setRefPropMap(refPropertyMap);//关联字段对应的属性
		}
		ref.setRefType(refCof.getRefType());//关联 方式
     	
     	
    	return ref;
    }
    /**
     * 过滤及 封装  字段
     * @param groupConf
     * @param col
     * @return 返回null 说明被过滤
     */
    protected Column renderColumn(ColumnGroupConf colGoup,Column col){
    	//判断字段 是否 包含或排除
		String exc = colGoup.getExclude();
		if(exc!=null){
			if(Arrays.asList(exc.replace("，", ",").split(","))
					.contains(col.getColName())){
				return null;
			}
		}else{
			String inc = colGoup.getInclude();
			if(inc!=null && !Arrays.asList(inc.replace("，", ",").split(","))
					.contains(col.getColName())){
				return null;
			}
		}
		//字段属性 自定义 
		Map<String,ColumnConf> colConfMap = colGoup.getColConfMap();
		ColumnConf colConf = colConfMap.get(col.getColName());
		if(colConf!=null){
			String labelName = colConf.getLabelName();
			if(!CodeUtil.isEmpty(labelName)){
				col.setLabelName(labelName);
			}
			String tagType = colConf.getTagType();
			if(!CodeUtil.isEmpty(tagType)){
				col.setTagType(tagType);
			}
			String colValue = colConf.getColValue();
			if(!CodeUtil.isEmpty(colValue)){
				Map<String,String> colValMap = CodeUtil.splitKeyVal(colValue, "=");
				if(colValMap!=null){
					col.setColValueMap(colValMap);
				}
			}
		}
		return col;
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
			if(idxName==null){
				continue;
			}else{
				idxName = idxName.toLowerCase();
			}
			String colName = rs.getString("column_name");
			if(colName!=null){
				colName = colName.toLowerCase();
			}
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
