package com.mapleLeaf.code.other;

import com.mapleLeaf.code.service.ITableService;
import com.mapleLeaf.code.service.impl.InformixTableService;
import com.mapleLeaf.code.service.impl.MysqlTableService;
import com.mapleLeaf.code.service.impl.OracleTableService;
import com.mapleLeaf.code.service.impl.PostgresqlTableService;
import com.mapleLeaf.code.service.impl.SqlServerTableService;

/**
 * 针对各类数据库的服务创建工厂
 * @author mars.liu
 *
 */
public class TableServiceFactory {
	
	public static ITableService getInstance(String dbType) {
		dbType = dbType.toLowerCase();
		if (dbType.equals("mysql")) {
			return new MysqlTableService();
		} else if (dbType.equals("oracle")) {
			return new OracleTableService();
		} else if (dbType.equals("sqlserver")) {
			return new SqlServerTableService();
		}else if (dbType.equals("informix")) {
			return new InformixTableService();
		}else if (dbType.equals("postgresql")) {
			return new PostgresqlTableService();
		}
		throw new RuntimeException("不支持的数据库类型");
	}
	

}
