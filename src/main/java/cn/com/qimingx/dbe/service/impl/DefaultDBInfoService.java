package cn.com.qimingx.dbe.service.impl;


public class DefaultDBInfoService extends AbstractDBInfoService {
	// Logger
	//private static final Log log = LogFactory
	//		.getLog(DefaultDBInfoService.class);

	// 假设当前数据库 不支持用于分页的SQL语句
	public boolean supportLimit() {
		//log.info("假设当前数据库 不支持用于分页的SQL语句~~");
		return false;
	}

	//
	public String getLimitSQLString(String originalSQL) {
		return originalSQL;
	}

}
