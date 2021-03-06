package com.mapleLeaf.code.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.mapleLeaf.common.util.CacheUtil;
import com.mapleLeaf.common.util.GlobalConst;



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
        	table.setRefTables(refTables);
        }
        return table;  
    }
    /**
     * 封装 主表数据
     */
    @SuppressWarnings("unchecked")
	protected Table renderTable(TableConf tbConf,Module module,Connection conn) 
    		throws SQLException{
    	 //表名
    	 String tableName =tbConf.getTabName();
    	 
    	 Table table = new Table(); 
    	 
		 table.setExclude(tbConf.getExclude());
		 table.setTabName(tableName);//表名
		
		 if(!CodeUtil.isEmpty(tbConf.getEntName())){
			 table.setEntName(CodeUtil.converFirstUpper(tbConf.getEntName()));
			 table.setLowEntName(CodeUtil.converFirstLower(tbConf.getEntName()));
		 }else{
			//对应的实体类名
	    	 String mvPrefix=tbConf.getTabName();
			 //全局 表名前缀
			if(!CodeUtil.isEmpty(module.getBaseTabPrefix())){
			 	String[] prefixs = module.getBaseTabPrefix().replace("，", ",").split(",");
			 	for(int i=0;i<prefixs.length;i++){
			 		if(tableName.toLowerCase().indexOf(prefixs[i].trim().toLowerCase())==0){
			 			mvPrefix = tableName.toLowerCase().replaceFirst(prefixs[i].trim().toLowerCase(), "");
			 			break;
			 		}
			 		
			 	}
			 }
			//实体类名,首字母大写的驼峰命名 
	         table.setEntName(CodeUtil.convertToCamelCase(mvPrefix));
	         //首字母小写的驼峰命名
	         table.setLowEntName(CodeUtil.convertToFstLowerCamelCase(mvPrefix));
		 }
		
         
         //获取  表 注释
		 Object cacheComm = CacheUtil.getValue("code",tableName+"_commet");
		 if(cacheComm==null){
			 String tabRemark = getTableRemark(tableName, conn);
			 table.setRemark(tabRemark);
			 CacheUtil.addCache("code", tableName+"_commet", tabRemark);
		 }else{
			 table.setRemark((String)cacheComm);
		 }
		 
	     
    	
    	//获取字段
    	List<Column> cols = null;
    	Object cacheCol = CacheUtil.getValue("code",tableName+"_column");
    	if(cacheCol==null){
    		cols = getTableColumns(tableName, module, conn);
    		
    		//如果有去掉字段名前缀，重新赋值属性值
     		if(!CodeUtil.isEmpty(module.getBaseColPrefix())){
     			String[] colPrefixs = module.getBaseColPrefix().replace("，", ",").split(",");
     			for(Column col : cols){
     				for(int i=0;i<colPrefixs.length;i++){
     					if(col.getColName().indexOf(colPrefixs[i].trim().toLowerCase())==0){
     						String tmpColName = col.getColName().replaceFirst(colPrefixs[i].trim().toLowerCase(), "");
     						col.setPropName(module.isColumnIsCamel()?CodeUtil.convertToFstLowerCamelCase(tmpColName)
     			        			:tmpColName);// 类属性名
     						col.setUpperPropName(module.isColumnIsCamel()?CodeUtil.convertToCamelCase(tmpColName)
     			        			:CodeUtil.converFirstUpper(tmpColName));//类 属性名首字母大写
     						break;
         				}
     				}
     				
     			}
     		}
    		CacheUtil.addCache("code",tableName+"_column",cols);
    	}else{
    		cols = (List<Column>)cacheCol;
    	}
    	
    	//获取主键字段集
    	Object cachePk = CacheUtil.getValue("code", tableName+"_pk");
    	String pks = "";
    	if(cachePk==null){
    		pks = getTablePrimaryKey(tableName,conn);
    		CacheUtil.addCache("code", tableName+"_pk", pks);
    	}else{
    		pks = (String)cachePk;
    	}
    	
    	String[] idxCols = pks.split(",");//主键或唯一索引
    	if(CodeUtil.isEmpty(pks)){//如果主键为空，则去查唯一索引
    		//得到 唯一索引,主键
	       	 Map<String,String> map = this.getTableUniqueIdx(tableName, conn);
	       	 //取出 其中一组
	       	 for(Map.Entry<String, String> entry:map.entrySet()){
	       		 String v = entry.getValue();
	       		 idxCols = v.split(",");
	       		 break;
	       	 }
    	}
    	
    	List<Column> indexes = new ArrayList<>();//其中一组 唯一索引或主键的字段集合
    	
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
							arrTemp[2].replace("：", ":"), ":");
					if(colValMap!=null){
						col.setColValueMap(colValMap);
					}
				}
			}
    		
    		//字段属性 配置
    		ColumnGroupConf colGoup = tbConf.getColGroup();
    		if(colGoup!=null){
    			if(!CodeUtil.isEmpty(colGoup.getSearchPos())){
        			table.getEnableColPos().add("searchPos");
        		}
        		if(!CodeUtil.isEmpty(colGoup.getListPos())){
        			table.getEnableColPos().add("listPos");
        		}
        		if(!CodeUtil.isEmpty(colGoup.getInputPos())){
        			table.getEnableColPos().add("inputPos");
        		}
        		//自定义 字段属性
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
        	if(!CodeUtil.isEmpty(pks) &&
        			CodeUtil.checkStrArray(idxCols, col.getColName())){
        		col.setPk(true);
        	}
        	//加入到 唯一索引或主键
        	if(CodeUtil.checkStrArray(idxCols, col.getColName())){
        		indexes.add(col);
        	}
        	
        	table.getColumns().add(col);
        		
    	}
    	
    	if(!indexes.isEmpty()){
			table.setUniIdxCols(indexes);
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
    @SuppressWarnings("unchecked")
	protected RefTable renderRefTable(RefConf refCof,Module module,Connection conn) throws SQLException{
    	RefTable ref = new RefTable();
    	//表名
    	String tableName = refCof.getTabName();
    	
    	ref.setTabName(tableName);//表名
    	
    	if(!CodeUtil.isEmpty(refCof.getEntName())){
    		ref.setEntName(CodeUtil.converFirstUpper(refCof.getEntName()));
    		ref.setLowEntName(CodeUtil.converFirstLower(refCof.getEntName()));
    	}else{
    		//对应的实体类名
       	 	String mvPrefix=refCof.getTabName();
    		//全局 表名前缀
    		if(!CodeUtil.isEmpty(module.getBaseTabPrefix())){
    		 	String[] prefixs = module.getBaseTabPrefix().replace("，", ",").split(",");
    		 	for(int i=0;i<prefixs.length;i++){
    		 		if(tableName.toLowerCase().indexOf(prefixs[i].trim().toLowerCase())==0){
    		 			mvPrefix=tableName.toLowerCase().replaceFirst(prefixs[i].trim().toLowerCase(), "");
    		 			break;
    		 		}
    		 		
    		 	}
    		 }
    		//实体类名,首字母大写的驼峰命名 
            ref.setEntName(CodeUtil.convertToCamelCase(mvPrefix));
           //首字母小写的驼峰命名
            ref.setLowEntName(CodeUtil.convertToFstLowerCamelCase(mvPrefix)); 
    	}
    	
         
         
		 //获取表 注释
		 Object cacheComm = CacheUtil.getValue("code",tableName+"_commet");
		 if(cacheComm==null){
			 String tabRemark = getTableRemark(tableName, conn);
			 ref.setRemark(tabRemark);
			 CacheUtil.addCache("code", tableName+"_commet", tabRemark);
		 }else{
			 ref.setRemark((String)cacheComm);
		 }
		
        //获取字段 
         Object cacheCol = CacheUtil.getValue("code",tableName+"_column");
         List<Column> cols = null;
     	if(cacheCol==null){
     		cols = getTableColumns(tableName, module, conn);
     		//如果有去掉字段名前缀，重新赋值属性值
     		if(!CodeUtil.isEmpty(module.getBaseColPrefix())){
     			String[] colPrefixs = module.getBaseColPrefix().replace("，", ",").split(",");
     			for(Column col : cols){
     				for(int i=0;i<colPrefixs.length;i++){
     					if(col.getColName().indexOf(colPrefixs[i].trim().toLowerCase())==0){
     						String tmpColName = col.getColName().replaceFirst(colPrefixs[i].trim().toLowerCase(), "");
     						col.setPropName(module.isColumnIsCamel()?CodeUtil.convertToFstLowerCamelCase(tmpColName)
     			        			:tmpColName);// 类属性名
     						col.setUpperPropName(module.isColumnIsCamel()?CodeUtil.convertToCamelCase(tmpColName)
     			        			:CodeUtil.converFirstUpper(tmpColName));//类 属性名首字母大写
     						break;
         				}
     				}
     				
     			}
     		}
     		
     		CacheUtil.addCache("code",tableName+"_column",cols);
     	}else{
     		cols = (List<Column>)cacheCol;
     	}
     	
     	//获取主键
     	Object cachePk = CacheUtil.getValue("code", tableName+"_pk");
    	String pks = "";
    	if(cachePk==null){
    		pks = getTablePrimaryKey(tableName,conn);
    		CacheUtil.addCache("code", tableName+"_pk", pks);
    	}else{
    		pks = (String)cachePk;
    	}
    	
    	String[] pkCols = pks.split(",");
    	
    	
     	for(Column col : cols){
     		
     		if(!CodeUtil.isEmpty(col.getRemark())){
				//字段 文本 默认 COMMENT中取   COMMENT约定格式(字段文本;表单类型;val1:text1,val2:text2;)
     			String[] arrTemp = col.getRemark().replace("；", ";").split(";");
				col.setLabelName(arrTemp[0].trim());
				if(arrTemp.length>1){
					col.setTagType(arrTemp[1].trim());
				}
				if(arrTemp.length>2){
					Map<String,String> colValMap = CodeUtil.splitKeyVal(
							arrTemp[2].replace("：", ":"), ":");
					if(colValMap!=null){
						col.setColValueMap(colValMap);
					}
				}
			}
     		//字段集合 属性配置
    		ColumnGroupConf colGoup = refCof.getColGroup();
    		if(colGoup!=null){
    			if(!CodeUtil.isEmpty(colGoup.getSearchPos())){
        			ref.getEnableColPos().add("searchPos");
        		}
        		if(!CodeUtil.isEmpty(colGoup.getListPos())){
        			ref.getEnableColPos().add("listPos");
        		}
        		if(!CodeUtil.isEmpty(colGoup.getInputPos())){
        			ref.getEnableColPos().add("inputPos");
        		}
        		//自定义字段属性
    			col = renderColumn(colGoup, col);
    			if(col==null){
    				continue;
    			}
    		}
     		//判断字段是否主键
        	if(CodeUtil.checkStrArray(pkCols, col.getColName())){
        		col.setPk(true);
        	}
        	ref.getColumns().add(col);
     	}
     	
     	//关联字段(多对多时，主表字段=中间表字段)
		String refColumn = refCof.getRefColumns();
		if(!CodeUtil.isEmpty(refColumn)){
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
     	
		//多对多时，有中间表
		if("ManyToMany".equals(refCof.getRefType())){
			ref.setMidTabName(refCof.getMidTabName());
			String midRefColumn = refCof.getMidRefCol();
			if(!CodeUtil.isEmpty(midRefColumn)){
				//中间表字段=关联表字段
				Map<String,String> midRefColumnMap = new HashMap<>();
				String[] midRefColumns = midRefColumn.split("=");
				midRefColumnMap.put(midRefColumns[0].trim(), midRefColumns[1].trim());
				ref.setMidRefColMap(midRefColumnMap);
			}
		//多对一有关联字段（即外键）；一对一时 可能有关联字段。
		}else if("OneToOne".equals(refCof.getRefType())||
					"ManyToOne".equals(refCof.getRefType())){
			ref.setForKey(refCof.getForKey());
		}
		
     	
    	return ref;
    }
   
    /**
     * 过滤及 封装  字段
     * @param groupConf
     * @param col
     * @return 返回null 说明被过滤
     */
    protected Column renderColumn(ColumnGroupConf colGoup,Column col){
    	//判断字段 是否被排除
		String exc = colGoup.getExclude();
		if(exc!=null){
			if(CodeUtil.checkStrArray(exc.replace("，", ",").split(","), 
					col.getColName())){
				return null;
			}
		}
		//字段位置标识判断
		String serPos = colGoup.getSearchPos();
		if(serPos!=null){
			if(CodeUtil.checkStrArray(serPos.replace("，", ",").split(","), 
					col.getColName())){
				col.getPositions().add("searchPos");
			}
		}
		String listPos = colGoup.getListPos();
		if(listPos!=null){
			if(CodeUtil.checkStrArray(listPos.replace("，", ",").split(","), 
					col.getColName())){
				col.getPositions().add("listPos");
			}
		}
		String inputPos = colGoup.getInputPos();
		if(inputPos!=null){
			if(CodeUtil.checkStrArray(inputPos.replace("，", ",").split(","), 
					col.getColName())){
				col.getPositions().add("inputPos");
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
			if(!CodeUtil.isEmpty(tagType)&&
					CodeUtil.checkStrArray(GlobalConst.TAG_TYPES, tagType)){
				col.setTagType(tagType.toLowerCase());//默认 text
			}
			String propName = colConf.getPropName();
			if(!CodeUtil.isEmpty(propName)){
				col.setPropName(propName);
				col.setUpperPropName(CodeUtil.converFirstUpper(propName));
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
		ResultSet rs = null;
		try {
			DatabaseMetaData dbMeta = con.getMetaData(); 
			rs = dbMeta.getIndexInfo(null, null, tableName, true, false);
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
		} finally {
			if(null!=rs){
				rs.close();
			}
		}
		
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
		String colName="";
		ResultSet rs = null;
		try {
			DatabaseMetaData dbMeta = con.getMetaData(); 
			rs = dbMeta.getPrimaryKeys(null,null,tableName);
			while (rs.next()){
				colName+=rs.getString("column_name")+",";
			}
		} finally {
			if(null!=rs){
				rs.close();
			}
		}
		return colName;
	}
}
