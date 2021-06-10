package com.mapleLeaf.code.service.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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



public class OracleTableService extends AbstractTableService {

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

		String sql = "SELECT USER_TAB_COLS.TABLE_NAME, USER_TAB_COLS.COLUMN_NAME , " + "USER_TAB_COLS.DATA_TYPE, "
				+ "USER_TAB_COLS.DATA_LENGTH , " + " USER_TAB_COLS.NULLABLE, " + " USER_TAB_COLS.COLUMN_ID, "
				+ " user_tab_cols.data_default," + "    user_col_comments.comments " + "FROM USER_TAB_COLS  "
				+ "inner join user_col_comments on " + " user_col_comments.TABLE_NAME=USER_TAB_COLS.TABLE_NAME "
				+ "and user_col_comments.COLUMN_NAME=USER_TAB_COLS.COLUMN_NAME "
				+ "where  USER_TAB_COLS.Table_Name=upper(?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, tableName);
			rs = ps.executeQuery();
			while (rs.next()) {
				Column col = new Column();
				String colName = rs.getString("column_name").toLowerCase();
				col.setColName(colName);
				String type = rs.getString("data_type");

				col.setColType(CodeUtil.convertJdbcType(type, module.getPersistence()));
				col.setPropName(isCamel ? CodeUtil.convertToFstLowerCamelCase(colName) : colName);

				col.setPropType(CodeUtil.convertType(type));
				col.setUpperPropName(
						isCamel ? CodeUtil.convertToCamelCase(colName) : CodeUtil.converFirstUpper(colName));
				col.setLength(rs.getLong("data_length"));
				col.setNullable(rs.getString("nullable").equals("YES") || rs.getString("nullable").equals("Y"));
				col.setDefaultValue(rs.getString("data_default"));
				col.setRemark(rs.getString("comments"));

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
		// DatabaseMetaData dbMeta = con.getMetaData();
		// ResultSet rs = dbMeta.getPrimaryKeys(null,null,tableName);
		String sql = "select a.constraint_name,a.column_name from user_cons_columns a, user_constraints b  "
				+ "where a.constraint_name = b.constraint_name  and b.constraint_type = 'P' and a.table_name = ?";
		String columnName = "";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, tableName.toUpperCase());
			rs = ps.executeQuery();

			while (rs.next()) {
				columnName += rs.getString("column_name") + ",";
			}
		} finally {
			if (null != rs) {
				rs.close();
			}
			if (null != ps) {
				ps.close();
			}
		}

		return columnName;
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
		// oracle表名 必须要大写 ，才能获取索引
		tableName = tableName.toUpperCase();
		Map<String, String> map = new HashMap<>();

		ResultSet rs = null;
		try {
			DatabaseMetaData dbMeta = con.getMetaData();
			rs = dbMeta.getIndexInfo(null, null, tableName, true, false);
			while (rs.next()) {
				String idxName = rs.getString("index_name");
				if (idxName == null) {
					continue;
				} else {
					idxName = idxName.toLowerCase();
				}
				String colName = rs.getString("column_name");
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
		String remark = "";
		String sql = "SELECT COMMENTS FROM USER_TAB_COMMENTS WHERE table_name=upper(?)";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, tableName);
			rs = ps.executeQuery();
			if (rs.next()) {
				remark = rs.getString("comments");
			}
		} finally {
			if (null != rs) {
				rs.close();
			}
			if (null != ps) {
				ps.close();
			}
		}
		return remark;
	}

}
