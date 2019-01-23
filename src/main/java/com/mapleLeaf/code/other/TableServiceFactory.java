package com.mapleLeaf.code.other;

import com.mapleLeaf.code.service.ITableService;
import com.mapleLeaf.code.service.impl.InformixTableService;
import com.mapleLeaf.code.service.impl.MysqlTableService;
import com.mapleLeaf.code.service.impl.OracleTableService;
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
			return getMysqlService();
		} else if (dbType.equals("oracle")) {
			return getOracleService();
		} else if (dbType.equals("sqlserver")) {
			return getSqlServerService();
		}else if (dbType.equals("informix")) {
			return new InformixTableService();
		}
		throw new RuntimeException("不支持的数据库类型");
	}
	
	private static ITableService getMysqlService(){
		return new MysqlTableService();
	}
	
	private static ITableService getOracleService(){
		return new OracleTableService();
	}
	
	private static ITableService getSqlServerService(){
		return new SqlServerTableService();
	}

}
