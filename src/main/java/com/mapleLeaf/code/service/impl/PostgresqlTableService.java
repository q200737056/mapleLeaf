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


public class PostgresqlTableService extends AbstractTableService {

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

		String sql = "select A.*,B.column_comment from information_schema.COLUMNS A left join"
				+ " (SELECT col_description(a.attrelid,a.attnum) as column_comment,a.attname as name"
				+ " FROM pg_class as c,pg_attribute as a where c.relname=? and a.attrelid=c.oid"
				+ " and a.attnum>0) B on A.column_name=B.name" + " where A.TABLE_NAME=?";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, tableName);
			ps.setString(2, tableName);
			rs = ps.executeQuery();
			while (rs.next()) {
				Column col = new Column();
				String colName = rs.getString("column_name");
				col.setColName(colName);// 字段名
				String type = rs.getString("data_type");
				col.setColType(type.toLowerCase());// 数据库字段类型
				col.setJdbcType(CodeUtil.convertJdbcType(type, module.getPersistence()));//jdbc类型
				col.setRemark(rs.getString("column_comment"));// 字段注释

				col.setPropName(isCamel ? CodeUtil.convertToFstLowerCamelCase(colName) : colName);// 类属性名
				col.setPropType(CodeUtil.convertType(type,module.isWrapperClass()));// 属性类型
				col.setUpperPropName(
						isCamel ? CodeUtil.convertToCamelCase(colName) : CodeUtil.converFirstUpper(colName));
				col.setNullable(rs.getString("is_nullable").equals("YES"));// 字段是否为空
				col.setLength(rs.getLong("character_maximum_length"));// 字段长度
				col.setDefaultValue(rs.getString("column_default"));// 字段默认值

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
		return super.getTableUniqueIdx(tableName, con);
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
		String sql = "select cast(obj_description(relfilenode,'pg_class') as varchar) as comment "
				+ "from pg_class where relkind = 'r' and relname=?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, tableName);
			rs = ps.executeQuery();
			if (rs.next()) {
				remark = rs.getString("comment");
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
