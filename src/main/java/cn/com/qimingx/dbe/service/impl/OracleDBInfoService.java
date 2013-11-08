package cn.com.qimingx.dbe.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author inc062805
 * 
 *         Oracle 数据库信息提供服务
 */
public class OracleDBInfoService extends AbstractDBInfoService {
	// Logger
	private static final Log log = LogFactory.getLog(OracleDBInfoService.class);

	// 重写 取得数据库名称的逻辑：添加版本号信息
	public String getDatabaseName() {
		String name = super.getDatabaseName();
		try {
			String version = meta.getDatabaseMajorVersion() + ".";
			version += meta.getDatabaseMinorVersion();
			name += " [" + version + "]";
		} catch (SQLException e) {
			log.error("meta.getDatabaseMajorVersion出错：" + e.getMessage());
		}
		return name;
	}

	// 重写 取得模式列表的逻辑：过滤掉与用户名称不匹配的模式
	public List<String> getSchemas() {
		List<String> schemas = super.getSchemas();

		// 过滤与用户不匹配的其它模式
		try {
			String user = meta.getUserName();
			if (schemas.indexOf(user) > -1) {
				// 仅返回与用户名称匹配的模式，其它的模式全部丢弃
				schemas.clear();
				schemas.add(user);
			}
		} catch (SQLException e) {
			log.error("meta.getUserName出错：" + e.getMessage());
		}

		return schemas;
	}

	// 重写 取得元素类型列表的逻辑：需读取Oracle独有元素,如：Java Package, Sequence等。
	public List<String> getElementTypes() {
		// TODO:等待实现：查询oracle数据字典....
		return super.getElementTypes();
	}

	// 重写 取得元素列表的逻辑
	public List<String> getElements(String schema, String... types) {
		// TODO:等待实现：查询oracle的元素列表
		return super.getElements(schema, types);
	}

	// 重写 用于分页的 Oracle专用的SQL语句
	public String getLimitSQLString(String originalSQL) {
		// TODO:等待实现，用于分页的 Oracle专用的SQL语句
		return originalSQL;
	}

	//
	public boolean supportLimit() {
		return false;
	}

}
