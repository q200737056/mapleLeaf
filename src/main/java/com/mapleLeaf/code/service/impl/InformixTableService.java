package com.mapleLeaf.code.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mapleLeaf.code.confbean.Module;
import com.mapleLeaf.code.confbean.TableConf;
import com.mapleLeaf.code.model.Column;
import com.mapleLeaf.code.model.Table;
import com.mapleLeaf.code.service.AbstractTableService;
import com.mapleLeaf.code.utils.CodeUtil;

public class InformixTableService extends AbstractTableService {

	/**
	 * 获取指定表信息并封装成Table对象
	 * 
	 * @param tbConf
	 * @param module
	 * @param con
	 */
	@Override
	public Table getTable(TableConf tbConf, Module module, Connection con) throws SQLException {
		return super.getTable(tbConf, module, con);
	}

	/**
	 * 获取数据表的所有字段
	 * 
	 * @param table
	 * @param conn
	 * @throws SQLException
	 */
	@Override
	public List<Column> getTableColumns(String tableName, Module module, Connection conn) throws SQLException {

		boolean isCamel = module.isColumnIsCamel();

		List<Column> cols = new ArrayList<>();

		String sql = "SELECT c.* FROM syscolumns c, systables t WHERE c.tabid=t.tabid AND t.tabname=?";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, tableName);
			rs = ps.executeQuery();
			while (rs.next()) {
				Column col = new Column();
				String colName = rs.getString("colname");// 字段名
				col.setColName(colName);
				int type = rs.getInt("coltype");// 字段类型
				String type_str = informixConvert(type);
				col.setColType(type_str.toLowerCase());// 数据库字段类型
				col.setJdbcType(CodeUtil.convertJdbcType(type_str, module.getPersistence()));//jdbc类型
				col.setRemark("");// 没有
				
				col.setPropName(isCamel ? CodeUtil.convertToFstLowerCamelCase(colName) : colName);// 属性
				col.setPropType(CodeUtil.convertType(type_str,module.isWrapperClass()));// 属性 类型
				
				col.setNullable(true);// 默认 允许为空
				col.setLength((long) rs.getInt("collength"));// 字段长度
				col.setDefaultValue("");

				cols.add(col);

			}
		} finally {
			if (null != rs) {
				rs.close();
			}
			if (null != ps) {
				ps.close();
			}
		}

		return cols;
	}

	@Override
	public String getTablePrimaryKey(String tableName, Connection con) throws SQLException {
		return super.getTablePrimaryKey(tableName, con);
	}

	/**
	 * 获取 表中 所有 唯一索引(包含主键)
	 * 
	 * @param tableName
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	@Override
	public Map<String, String> getTableUniqueIdx(String tableName, Connection con) throws SQLException {
		Map<String, String> map = new HashMap<>();

		String sql = "select b.idxname, a.colname,a.colno from  syscolumns  a ,"
				+ "sysindexes b ,systables c where  (a.colno=b.part1 or a.colno=b.part2 or a.colno=b.part3"
				+ " or a.colno=b.part4 or a.colno=b.part5) and a.tabid =b.tabid and a.tabid = c.tabid"
				+ " and b.idxtype='U' and c.tabname=?";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, tableName);
			rs = ps.executeQuery();
			while (rs.next()) {
				String idxName = rs.getString("idxname");
				if (idxName == null) {
					continue;
				} else {
					idxName = idxName.toLowerCase();
				}
				String colName = rs.getString("colname");
				if (colName != null) {
					colName = colName.toLowerCase();
				}
				String v = map.get(idxName);
				if (v == null) {
					map.put(idxName, colName + ",");
				} else {
					map.put(idxName, v + colName + ",");
				}
			}
		} finally {
			if (null != rs) {
				rs.close();
			}
			if (null != ps) {
				ps.close();
			}
		}
		return map;
	}

	/**
	 * 表注释
	 * 
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	@Override
	public String getTableRemark(String tableName, Connection con) throws SQLException {
		// TODO Auto-generated method stub
		return "";
	}

	/**
	 * 字段类型转换
	 * 
	 * @param type
	 * @return
	 */
	private String informixConvert(int type) {
		switch (type) {
		case 0:
			return "CHAR";
		case 2:
			return "INTEGER";
		case 3:
			return "FLOAT";
		case 5:
			return "DECIMAL";
		case 13:
			return "VARCHAR";
		case 256:
			return "CHAR";
		case 258:
			return "INTEGER";
		case 259:
			return "FLOAT";
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
	/*
	 * private String convertJavaType(String databaseType){
	 * 
	 * String javaType = "";
	 * 
	 * String databaseTypeStr = databaseType.trim().toLowerCase();
	 * if(databaseTypeStr.startsWith("int")) { javaType = "int"; } else
	 * if(databaseTypeStr.equals("char")) { javaType = "String"; } else
	 * if(databaseTypeStr.indexOf("varchar")!=-1) { javaType = "String"; } else
	 * if(databaseTypeStr.equals("decimal")) { javaType = "BigDecimal"; } else
	 * if(databaseTypeStr.equals("date")) { javaType = "String"; } else {
	 * javaType = "String"; }
	 * 
	 * return javaType;
	 * 
	 * }
	 */

}
