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



public class SqlServerTableService extends AbstractTableService {

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

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT  cast(CASE WHEN col.colorder = 1 THEN obj.name ELSE '' END as varchar(100)) AS table_name ,");
		sb.append("  cast(col.colorder as int) AS column_id ,");
		sb.append("  col.name AS column_name ,");
		sb.append("  cast (ISNULL(ep.[value], '') as varchar(100)) AS comments ,");
		sb.append("  t.name AS data_type ,");
		sb.append("  cast (col.length as int) AS data_length ,");
		sb.append("  cast(ISNULL(COLUMNPROPERTY(col.id, col.name, 'Scale'), 0) as int) AS precision ,");
		sb.append("  cast (CASE WHEN COLUMNPROPERTY(col.id, col.name, 'IsIdentity') = 1 THEN 'Y' ELSE '' END as varchar(3)) AS seq ,");
		sb.append("  cast ( CASE WHEN EXISTS ( SELECT   1 FROM     dbo.sysindexes si");
		sb.append("  INNER JOIN dbo.sysindexkeys sik ON si.id = sik.id AND si.indid = sik.indid");
		sb.append("  INNER JOIN dbo.syscolumns sc ON sc.id = sik.id AND sc.colid = sik.colid");
		sb.append("  INNER JOIN dbo.sysobjects so ON so.name = si.name AND so.xtype = 'PK'");
		sb.append("  WHERE sc.id = col.id AND sc.colid = col.colid ) THEN 'Y' ELSE '' END as varchar(3)) AS prim ,");
		sb.append("  cast (CASE WHEN col.isnullable = 1 THEN 'Y' ELSE '' END as varchar(3)) AS nullable ,");
		sb.append("  cast (ISNULL(comm.text, '') as varchar(30)) AS data_default");
		sb.append(" FROM dbo.syscolumns col");
		sb.append("  LEFT  JOIN dbo.systypes t ON col.xtype = t.xusertype");
		sb.append("  inner JOIN dbo.sysobjects obj ON col.id = obj.id AND obj.xtype = 'U' AND obj.status >= 0");
		sb.append("  LEFT  JOIN dbo.syscomments comm ON col.cdefault = comm.id");
		sb.append("  LEFT  JOIN sys.extended_properties ep ON col.id = ep.major_id AND col.colid = ep.minor_id AND ep.name = 'MS_Description'");
		sb.append("  LEFT  JOIN sys.extended_properties epTwo ON obj.id = epTwo.major_id AND epTwo.minor_id = 0 AND epTwo.name = 'MS_Description'");
		sb.append(" WHERE  obj.name = ? ORDER BY col.colorder;");
		String sql = sb.toString();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, tableName);
			rs = ps.executeQuery();

			while (rs.next()) {
				Column col = new Column();
				String colName = rs.getString("column_name");
				col.setColName(colName);
				String type = rs.getString("data_type");

				col.setColType(CodeUtil.convertJdbcType(type, module.getPersistence()));
				col.setRemark(rs.getString("comments"));

				col.setPropName(isCamel ? CodeUtil.convertToFstLowerCamelCase(colName) : colName);
				col.setPropType(CodeUtil.convertType(type));
				col.setUpperPropName(
						isCamel ? CodeUtil.convertToCamelCase(colName) : CodeUtil.converFirstUpper(colName));
				col.setNullable(rs.getString("nullable").equals("Y"));
				col.setLength(rs.getLong("data_length"));
				col.setDefaultValue(rs.getString("data_default"));

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
		String sql = "SELECT cast (ds.value as varchar(100)) comments FROM sys.extended_properties ds LEFT "
				+ "JOIN sysobjects tbs ON ds.major_id=tbs.id WHERE  ds.minor_id=0 and tbs.name=?";
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
