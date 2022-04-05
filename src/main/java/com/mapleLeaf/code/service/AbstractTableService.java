package com.mapleLeaf.code.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	@Override
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
			 //获取 去掉前缀的表名
	    	 String mvPrefix = this.getMvpreName(tbConf.getTabName(), module.getBaseTabPrefix());
	    	
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
     		this.setColumnsProp(cols, module.getBaseColPrefix(), module.isColumnIsCamel());
     		
    		CacheUtil.addCache("code",tableName+"_column",cols);
    	}else{
    		cols = (List<Column>)cacheCol;
    	}
    	
    	//获取主键or唯一索引 字段集
    	
    	String[] idxCols = {};
    	String pks = "";
    	Object cachePk = CacheUtil.getValue("code", tableName+"_pk");
    	if(cachePk==null){
    		pks = this.getTablePrimaryKey(tableName, conn);
    		CacheUtil.addCache("code", tableName+"_pk", pks);
    	}else{
    		pks = (String)cachePk;
    	}
    	idxCols = this.getPkOrUni(pks,tableName, conn);
    	
    	List<Column> indexes = new ArrayList<>();//其中一组 唯一索引或主键的字段集合
    	Map<String, Column> searchMap = new TreeMap<>((s1,s2) -> s1.compareTo(s2));
    	Map<String, Column> listMap = new TreeMap<>((s1,s2) -> s1.compareTo(s2));
    	Map<String, Column> inputMap = new TreeMap<>((s1,s2) -> s1.compareTo(s2));
    	
    	for(Column col : cols){
    		
    		this.setColumnFormat(col);
    		
    		//字段属性 配置
    		ColumnGroupConf colGoup = tbConf.getColGroup();
    		if(colGoup!=null){
    			
        		//自定义 字段属性
    			col = renderColumn(colGoup, col,searchMap,listMap,inputMap);
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
        	if(CodeUtil.checkStrArray(pks.split(","), col.getColName())){
        		col.setPk(true);
        	}
        	//加入到 唯一索引或主键
        	if(CodeUtil.checkStrArray(idxCols, col.getColName())){
        		indexes.add(col);
        	}
        	
        	table.getColumns().add(col);
        		
    	}
    	//位置的字段集合
    	CodeUtil.<Column>mapvalToList(searchMap, table.getSearchColumns());
    	CodeUtil.<Column>mapvalToList(listMap, table.getListColumns());
    	CodeUtil.<Column>mapvalToList(inputMap, table.getInputColumns());
    	
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
    		//获取 去掉前缀的表名
	    	String mvPrefix = this.getMvpreName(refCof.getTabName(), module.getBaseTabPrefix());
	    	
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
     		this.setColumnsProp(cols, module.getBaseColPrefix(), module.isColumnIsCamel());
     		
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
    	
    
    	Map<String, Column> searchMap = new TreeMap<>((s1,s2) -> s1.compareTo(s2));
    	Map<String, Column> listMap = new TreeMap<>((s1,s2) -> s1.compareTo(s2));
    	Map<String, Column> inputMap = new TreeMap<>((s1,s2) -> s1.compareTo(s2));
    	
     	for(Column col : cols){
     		
     		this.setColumnFormat(col);
     		//字段集合 属性配置
    		ColumnGroupConf colGoup = refCof.getColGroup();
    		if(colGoup!=null){

        		//自定义字段属性
    			col = renderColumn(colGoup, col,searchMap,listMap,inputMap);
    			if(col==null){
    				continue;
    			}
    		}
     		//判断字段是否主键
        	if(CodeUtil.checkStrArray(pks.split(","),col.getColName())){
        		col.setPk(true);
        	}
        	ref.getColumns().add(col);
     	}
     	//位置的字段集合
    	CodeUtil.<Column>mapvalToList(searchMap, ref.getSearchColumns());
    	CodeUtil.<Column>mapvalToList(listMap, ref.getListColumns());
    	CodeUtil.<Column>mapvalToList(inputMap, ref.getInputColumns());
     	
    	ref.setRefType(refCof.getRefType());//关联 方式
     	//关联字段(多对多时，主表字段=中间表字段)
		String refColumn = refCof.getRefColumns();
		String tmpItem =  "";
		if(!CodeUtil.isEmpty(refColumn)){
			Map<String,String> refColumnMap = new HashMap<>();
			Map<String,String> refPropertyMap = new HashMap<>();
			String[] refColumns = refColumn.split(",");
			for(String item:refColumns){
				String[] itemMap = item.split("=");
				String item1 = itemMap[0].trim();
				String item2 = itemMap[1].trim();
				
				refColumnMap.put(item1, item2);
				
				if(!"ManyToMany".equals(refCof.getRefType())){
					item1 = this.getMvpreName(item1, module.getBaseColPrefix());
					item2 = this.getMvpreName(item2, module.getBaseColPrefix());
					if(module.isColumnIsCamel()){
						item1 = CodeUtil.convertToFstLowerCamelCase(item1);
						item2 = CodeUtil.convertToFstLowerCamelCase(item2);
					}
					refPropertyMap.put(item1, item2);
				}else{
					if(module.isColumnIsCamel()){
						item1 = CodeUtil.convertToFstLowerCamelCase(item1);
					}
					tmpItem = item1;
				}
				
				
			}
			ref.setRefColMap(refColumnMap);//关联的字段
			ref.setRefPropMap(refPropertyMap);//关联字段对应的属性
		}
		
		//多对多时，有中间表
		if("ManyToMany".equals(refCof.getRefType())){
			ref.setMidTabName(refCof.getMidTabName());
			String midRefColumn = refCof.getMidRefCol();
			if(!CodeUtil.isEmpty(midRefColumn)){
				//中间表字段=关联表字段
				Map<String,String> midRefColumnMap = new HashMap<>();
				String[] midRefColumns = midRefColumn.split("=");
				String item1 = midRefColumns[0].trim();
				String item2 = midRefColumns[1].trim();
				midRefColumnMap.put(item1, item2);
				ref.setMidRefColMap(midRefColumnMap);
			    
				item2 = this.getMvpreName(item2, module.getBaseColPrefix());
				if(module.isColumnIsCamel()){
					item2 = CodeUtil.convertToFstLowerCamelCase(item2);
				}
				ref.getRefPropMap().put(tmpItem, item2);
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
    protected Column renderColumn(ColumnGroupConf colGoup,Column col,Map<String,Column> searchMap,
    		Map<String,Column> listMap,Map<String,Column> inputMap){
    	//判断字段 是否被排除
		String exc = colGoup.getExclude();
		if(exc!=null){
			if(CodeUtil.checkStrArray(exc.replace("，", ",").split(","), 
					col.getColName())){
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
		
		//字段位置集合
		String serPos = colGoup.getSearchPos();
		if(serPos!=null){
			int tmpIdx = CodeUtil.checkStrArrayIdx(serPos.replace("，", ",").split(","), col.getColName());
			if(tmpIdx!=-1){
				searchMap.put(tmpIdx+"", col);
			}
		}
		String listPos = colGoup.getListPos();
		if(listPos!=null){
			int tmpIdx = CodeUtil.checkStrArrayIdx(listPos.replace("，", ",").split(","), 
					col.getColName());
			if(tmpIdx!=-1){
				listMap.put(tmpIdx+"", col);
			}
		}
		String inputPos = colGoup.getInputPos();
		if(inputPos!=null){
			int tmpIdx = CodeUtil.checkStrArrayIdx(inputPos.replace("，", ",").split(","),col.getColName());
			if(tmpIdx!=-1){
				inputMap.put(tmpIdx+"", col);
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
    @Override
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
    @Override
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
	/**
	 * 获取 去掉 前缀的 名字
	 * @param fullName
	 * @param prefixs
	 * @return
	 */
	private String getMvpreName(String fullName,String prefixs){
		 //全局 表名前缀
		String mvPrefix = fullName;
		if(!CodeUtil.isEmpty(prefixs)){
		 	String[] prefixArr = prefixs.replace("，", ",").split(",");
		 	for(int i=0;i<prefixArr.length;i++){
		 		if(fullName.toLowerCase().indexOf(prefixArr[i].trim().toLowerCase())==0){
		 			mvPrefix = fullName.toLowerCase().replaceFirst(prefixArr[i].trim().toLowerCase(), "");
		 			break;
		 		}
		 		
		 	}
		 }
	  return mvPrefix;
	}
	
	
	/**
	 * 字段 重新设置属性名
	 * @param cols
	 * @param prefixs
	 * @param isCamel
	 */
	private void setColumnsProp(List<Column> cols,String prefixs,Boolean isCamel){
		if(!CodeUtil.isEmpty(prefixs)){
 			String[] colPrefixs = prefixs.replace("，", ",").split(",");
 			for(Column col : cols){
 				for(int i=0;i<colPrefixs.length;i++){
 					if(col.getColName().indexOf(colPrefixs[i].trim().toLowerCase())==0){
 						String tmpColName = col.getColName().replaceFirst(colPrefixs[i].trim().toLowerCase(), "");
 						col.setPropName(isCamel?CodeUtil.convertToFstLowerCamelCase(tmpColName)
 			        			:tmpColName);// 类属性名
 						col.setUpperPropName(isCamel?CodeUtil.convertToCamelCase(tmpColName)
 			        			:CodeUtil.converFirstUpper(tmpColName));//类 属性名首字母大写
 						break;
     				}
 				}
 			}
 		}
	}
	/**
	 * 获取主键 or 一组唯一索引 （主键优先）
	 * @param tabName
	 * @param conn
	 * @return 字段数组
	 */
	private String[] getPkOrUni(String pks,String tabName,Connection conn){
		String[] idxCols = {};//主键或唯一索引
		try {
			idxCols = pks.split(",");
	    	if(CodeUtil.isEmpty(pks)){//如果主键为空，则去查唯一索引
	    		//得到 唯一索引,主键
		       	 Map<String,String> map = this.getTableUniqueIdx(tabName, conn);
		       	 //取出 其中一组
		       	 for(Map.Entry<String, String> entry:map.entrySet()){
		       		 String v = entry.getValue();
		       		 idxCols = v.split(",");
		       		 break;
		       	 }
	    	}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return idxCols;
	}
	/**
	 * 设置 字段的约定格式 数据
	 * @param col
	 */
	private void setColumnFormat(Column col){
		//通过字段类型推断表单类型
		if("text".equals(col.getColType()) || "clob".equals(col.getColType()) 
				|| col.getLength()>1000){
			col.setTagType("textarea");
		}else if("datetime".equals(col.getColType()) || "timestamp".equals(col.getColType())
				|| "date".equals(col.getColType())){
			col.setTagType("date");
		}
		if(!CodeUtil.isEmpty(col.getRemark())){
			//字段 文本,表单类型,字段标识 默认 COMMENT中取  
			//COMMENT约定形式:  字段文本(val1=text1,val2=text2)select
			String comment = col.getRemark().replace("（", "(").replace("）", ")").replace("：", ":").replace(":", "=");
			int startIdx = comment.indexOf("(");
			int endIdx = comment.indexOf(")");
			if(startIdx!=-1 && endIdx!=-1){
				col.setLabelName(comment.substring(0,startIdx).trim());
				String colValStr = comment.substring(startIdx+1,endIdx);
				Map<String,String> colValMap = CodeUtil.splitKeyVal(colValStr, "=");
				if(colValMap!=null){
					col.setColValueMap(colValMap);
				}
				if(endIdx+1 != comment.length()){
					String tagType = comment.substring(endIdx+1,comment.length()).trim();
					if(CodeUtil.checkStrArray(GlobalConst.TAG_TYPES, tagType)){
						col.setTagType(tagType);
					}
				}else{
					col.setTagType("select");
				}
			}else{
				col.setLabelName(col.getRemark());
			}
		}
		
	}

}
