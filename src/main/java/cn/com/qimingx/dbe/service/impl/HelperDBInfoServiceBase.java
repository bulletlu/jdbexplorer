package cn.com.qimingx.dbe.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

/**
 * @author inc062805
 * 
 * 协助 AbstractDBInfoService 类实现方法的 基本助手类
 */
class HelperDBInfoServiceBase {
	private Log log;
	private boolean schema = true;
	private AbstractDBInfoService service;

	// 构建器
	public HelperDBInfoServiceBase(AbstractDBInfoService service,
			Log log) {
		this.service = service;
		this.log = log;
	}

	// 取得数据库名称
	public String getDatabaseName() {
		try {
			String name = service.meta.getDatabaseProductName();
			name = name.replaceAll("/", "-");
			return name;
		} catch (SQLException e) {
			log.error("meta.getDatabaseProductName.ERROR：" + e.getMessage());
			return "--ERROR--";
		}
	}

	// 取得数据库模式列表
	public List<String> getSchemas() {
		List<String> list = new ArrayList<String>();
		try {
			// 取得模式数据
			ResultSet rs = service.meta.getSchemas();
			while (rs.next()) {
				String schema = rs.getString("TABLE_SCHEM");
				list.add(schema);
			}
			rs.close();

			// 如果SCHEM数据为空，则取Catalog
			if (list.size() == 0) {
				rs = service.meta.getCatalogs();
				while (rs.next()) {
					String schema = rs.getString("TABLE_CAT");
					list.add(schema);
				}
				schema = false;
			}
			rs.close();
		} catch (SQLException e) {
			log.error("meta.getSchemas出错：" + e.getMessage());
		}
		return list;
	}

	// 取得数据库元素类型（例如：表、视图、触发器等等）列表
	public List<String> getElementTypes() {
		List<String> list = new ArrayList<String>();
		try {
			ResultSet rs = service.meta.getTableTypes();
			while (rs.next()) {
				String type = rs.getString("TABLE_TYPE");
				list.add(type);
			}
			rs.close();
		} catch (SQLException e) {
			log.error("meta.getTableTypes出错：" + e.getMessage());
		}
		return list;
	}

	// 取得指定模式下指定类型的元素的列表
	public List<String> getElements(String schema, String... types) {
		List<String> list = new ArrayList<String>();
		if (types.length == 0) {
			types = new String[] { "table" };
		}
		try {
			ResultSet rs = null;
			if (isSchema()) {
				rs = service.meta.getTables(null, schema, null, types);
			} else {
				rs = service.meta.getTables(schema, null, null, types);
			}

			while (rs.next()) {
				String name = rs.getString("TABLE_NAME");
				// 过滤掉无效元素
				if (name.indexOf('/') > -1 || name.indexOf('$') > -1) {
					continue;
				}
				list.add(name);
			}
			rs.close();
		} catch (SQLException e) {
			log.error("meta.getTableNames出错：" + e.getMessage());
		}
		return list;
	}

	// 数据库是否支持可滚动的记录集
	public boolean supportScrollableResultSet() {
		// 先假设其支持
		return true;
		// TODO:完成 是否支持可滚动记录集 的自动侦测
	}

	public boolean isSchema() {
		return schema;
	}

	public void setSchema(boolean schema) {
		this.schema = schema;
	}
}
