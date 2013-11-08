package cn.com.qimingx.dbe.service.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.springframework.dao.DataAccessException;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.TableColumnInfo;
import cn.com.qimingx.dbe.TableDataInfo;
import cn.com.qimingx.dbe.TableInfo;
import cn.com.qimingx.dbe.TableColumnInfo.FKColumnInfo;
import cn.com.qimingx.utils.MyUtils;
import cn.com.qimingx.utils.SQLTypeUtils;
import cn.com.qimingx.utils.SQLUtils;

/**
 * @author inc062805
 * 
 * 协助 AbstractDBInfoService 类实现方法的 table/view 操作助手类
 */
class HelperDBInfoServiceTable {
	private Log log;
	private AbstractDBInfoService service;

	// 构建器
	public HelperDBInfoServiceTable(AbstractDBInfoService service, Log log) {
		this.service = service;
		this.log = log;
	}

	// 执行更新 sql 语句
	public ProcessResult<String> executeUpdate(String sql,
			Map<String, Object> params) {
		ProcessResult<String> pr = new ProcessResult<String>();
		try {
			int updateRows = service.namedJdbcTemplate.update(sql, params);
			pr.setSuccess(true);
			pr.setMessage("成功更新 " + updateRows + " 条记录。");
			return pr;
		} catch (DataAccessException e) {
			pr.setMessage(e.getRootCause().getMessage());
			log.error("执行SQL[" + sql + "]出错：" + pr.getMessage());
			return pr;
		}
	}

	// 执行查询sql
	public ProcessResult<TableInfo> executeQuery(String sql, int start,
			int limit, String condition) {
		// 读取数据
		ProcessResult<TableInfo> prTableInfo;
		prTableInfo = readTableInfo(sql, start, limit, condition, null);

		// return
		ProcessResult<TableInfo> pr = new ProcessResult<TableInfo>();
		if (prTableInfo.isFailing()) {
			pr.setMessage(prTableInfo.getMessage());
		} else {
			pr.setSuccess(true);
			pr.setData(prTableInfo.getData());
		}
		return pr;
	}

	// 取得指定模式下 指定Table的数据，并进行分页
	public ProcessResult<TableDataInfo> getTableData(final String schema,
			final String name, int start, int limit, String condition) {
		ProcessResult<TableDataInfo> pr = new ProcessResult<TableDataInfo>();
		// 读取列信息
		ProcessResult<List<TableColumnInfo>> prCI;
		prCI = service.getTableColumnInfo(schema, name);
		if (prCI.isFailing()) {
			pr.setMessage(prCI.getMessage());
			return pr;
		}
		List<TableColumnInfo> columns = prCI.getData();

		// sql 语句
		String sql = "SELECT * FROM " + name;

		// 读取数据
		ProcessResult<TableInfo> prTable;
		prTable = readTableInfo(sql, start, limit, condition, columns);

		// return
		if (prTable.isFailing()) {
			pr.setMessage(prTable.getMessage());
		} else {
			pr.setSuccess(true);
			pr.setData(prTable.getData().getData());
		}
		return pr;
	}

	// 取得指定模式下 指定Table 的信息
	public ProcessResult<TableInfo> getTableInfo(String schema, String name) {
		ProcessResult<TableInfo> pr = new ProcessResult<TableInfo>();
		// 构建Tableinfo
		TableInfo info = new TableInfo();
		info.setTableName(name);
		// 读取主键信息
		ProcessResult<String> prPK = service.getPrimaryKeys(schema, name);
		info.setReadOnly(prPK.isFailing());
		if (prPK.isFailing()) {
			// pr.setMessage(prPK.getMessage());
			log.warn("取 [" + name + "] 主键失败，表将只读~~");
		} else {
			info.setPkColumnName(prPK.getData());
		}

		// 读取列信息
		ProcessResult<List<TableColumnInfo>> prCI;
		prCI = service.getTableColumnInfo(schema, name);
		if (prCI.isFailing()) {
			pr.setMessage(prCI.getMessage());
			return pr;
		}
		info.setColumns(prCI.getData());

		// 设置主键标志
		for (TableColumnInfo tci : prCI.getData()) {
			String cname = tci.getName();
			String pkname = info.getPkColumnName();
			if (pkname.indexOf(cname) > -1) {
				tci.setPkColumn(true);
			}
		}

		// return .
		pr.setSuccess(true);
		pr.setData(info);
		return pr;
	}

	// 通过查询SQL语句 生成TableInfo对象(columns、data)..
	public ProcessResult<TableInfo> readTableInfo(String sql, int start,
			int limit, String condition, List<TableColumnInfo> columns) {
		ProcessResult<TableInfo> pr = new ProcessResult<TableInfo>();
		try {
			// get statement
			Statement stat = service.getDBConnection().createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			// 附加条件
			if (condition != null && condition.length() > 0) {
				sql = SQLUtils.appendCondition(sql, condition);
			}
			// 计算记录总数
			long total = SQLUtils.totalRowsBySQL(sql, stat);
			// 取得分页SQL
			if (service.supportLimit()) {
				sql = service.getLimitSQLString(sql);
			}

			// 读取数据
			log.debug("load data by sql:" + sql);
			ResultSet rs = stat.executeQuery(sql);
			// 如果需要 进行游标定位
			if (!service.supportLimit() && start > 1) {
				// 不支持分页SQL的情况下 手动移动指针
				if (service.supportScrollableResultSet()) {
					rs.absolute(start);
				} else {
					int counter = 0;// 指针移动计数器
					while (rs.next()) {
						if (++counter == start)
							break;
					}
				}
			}
			// 如果未传递列信息 则自动读取列信息
			if (columns == null || columns.size() == 0) {
				columns = getTableColumnInfoByResultSet(rs);
			}
			// 读取指定数据行
			TableDataInfo tableData = getTableDataInfoByResultSet(rs, limit,
					columns, service.supportLimit(), total);
			rs.close();

			// 数据读取完成，returning
			TableInfo tableInfo = new TableInfo();
			tableInfo.setData(tableData);
			tableInfo.setColumns(columns);
			pr.setSuccess(true);
			pr.setData(tableInfo);
			return pr;
		} catch (SQLException e) {
			log.error("readTableInfo出错：" + e.getMessage());
			pr.setMessage(e.getMessage());
			return pr;
		}
	}

	// 取得指定名称的Table的主键名称(多个主键以逗号分隔)
	public ProcessResult<String> getPrimaryKeys(String schema, String name) {
		ProcessResult<String> pr = new ProcessResult<String>();
		try {
			ResultSet rs = null;
			if (service.baseHelper.isSchema()) {
				rs = service.meta.getPrimaryKeys(null, schema, name);
			} else {
				rs = service.meta.getPrimaryKeys(schema, null, name);
			}
			String pkName = "";
			while (rs.next()) {
				if (pkName.length() > 0) {
					pkName += ",";
				}
				pkName += rs.getString("COLUMN_NAME");
			}
			rs.close();

			if (pkName != null) {
				pr.setSuccess(true);
				pr.setData(pkName);
			} else {
				pr.setMessage(name + " without PrimaryKey~!?");
			}
			return pr;
		} catch (SQLException e) {
			log.error("getPrimaryKeys出错：" + e.getMessage());
			pr.setMessage(e.getMessage());
			return pr;
		}
	}

	// 通过Schema名称取得列信息
	public ProcessResult<List<TableColumnInfo>> getTableColumnInfo(
			String schema, String name) {
		ProcessResult<List<TableColumnInfo>> pr;
		pr = new ProcessResult<List<TableColumnInfo>>();
		try {
			// 取得列信息
			ResultSet rs = null;
			if (service.baseHelper.isSchema()) {
				rs = service.meta.getColumns(null, schema, name, null);
			} else {
				rs = service.meta.getColumns(schema, null, name, null);
			}

			List<TableColumnInfo> columns = new ArrayList<TableColumnInfo>();
			while (rs.next()) {
				TableColumnInfo cinfo = new TableColumnInfo();
				cinfo.setName(rs.getString("COLUMN_NAME"));
				cinfo.setType(rs.getInt("DATA_TYPE"));
				cinfo.setSize(rs.getInt("COLUMN_SIZE"));
				cinfo.setDigits(rs.getInt("DECIMAL_DIGITS"));
				int nullIdx = rs.getInt("NULLABLE");
				cinfo.setNullable(nullIdx == DatabaseMetaData.columnNullable);
				cinfo.setDefaultValue(rs.getString("COLUMN_DEF"));
				cinfo.setComment(rs.getString("REMARKS"));
				columns.add(cinfo);
			}
			rs.close();

			// 取得外键、主键 等列信息
			rs = service.meta.getImportedKeys(null, schema, name);
			while (rs.next()) {
				String table = rs.getString("PKTABLE_NAME");
				String colname = rs.getString("PKCOLUMN_NAME");
				String fkName = rs.getString("FKCOLUMN_NAME");
				for (TableColumnInfo tci : columns) {
					if (tci.getName().equalsIgnoreCase(fkName)) {
						tci.setFkColumn(true);
						tci.setFkInfo(new FKColumnInfo(table, colname));
						break;
					}
				}
			}
			rs.close();

			pr.setSuccess(true);
			pr.setData(columns);
			return pr;
		} catch (SQLException e) {
			log.error("getTableColumnInfo出错：" + e.getMessage());
			pr.setMessage(e.getMessage());
			return pr;
		}
	}

	// 通过ResultSet取得列信息集合
	private List<TableColumnInfo> getTableColumnInfoByResultSet(ResultSet rs)
			throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int length = rsmd.getColumnCount();
		List<TableColumnInfo> columns;
		columns = new ArrayList<TableColumnInfo>(length);
		for (int i = 0; i < length; i++) {
			int idx = i + 1;
			String name = rsmd.getColumnName(idx);
			int type = rsmd.getColumnType(idx);
			int width = rsmd.getColumnDisplaySize(idx);
			columns.add(new TableColumnInfo(name, type, width, true));
		}
		return columns;
	}

	/*
	 * 通过记录集，读取Table数据集合
	 * 
	 * @param rs
	 * 
	 * @param limit
	 * 
	 * @param columns
	 * 
	 * @param supportLimitSQL,是否支持LimitSQL语句
	 * 
	 * @param total
	 */
	private TableDataInfo getTableDataInfoByResultSet(ResultSet rs, int limit,
			List<TableColumnInfo> columns, boolean supportLimitSQL, long total)
			throws SQLException {
		// init..
		List<Map<String, Object>> rows;
		rows = new ArrayList<Map<String, Object>>(limit + 10);

		// read
		int counter = 0;// limit计数器
		while (rs.next()) {
			Map<String, Object> row = new HashMap<String, Object>();
			for (TableColumnInfo column : columns) {
				Object value = readFieldValue(rs, column);
				row.put(column.getName(), value);
			}
			rows.add(row);
			if (!supportLimitSQL && limit > 0 && ++counter == limit) {
				break;
			}
		}
		// return
		return new TableDataInfo(total, rows);
	}

	/*
	 * 从ResultSet中读取 指定列的值
	 * 
	 * @param rs @param column @return @throws SQLException
	 */
	private Object readFieldValue(ResultSet rs, TableColumnInfo column)
			throws SQLException {
		int type = column.getType();
		String field = column.getName();
		// 读取字段值
		Object value = null;
		if (SQLTypeUtils.isDateType(type)) {
			value = rs.getTimestamp(field);
		} else if (SQLTypeUtils.isNumberType(type)) {
			value = rs.getString(field);
		} else if (SQLTypeUtils.isStringType(type)) {
			value = rs.getString(field);
		} else if (SQLTypeUtils.isBlobType(type)) {
			value = "[LONGVARBINARY]";
		} else if (SQLTypeUtils.isClobType(type)) {
			value = "[LONGVARCHAR]";
		} else {
			value = "[" + SQLTypeUtils.getJdbcTypeName(type) + "]";
		}
		// 处理 NULL 值
		if (rs.wasNull() && !SQLTypeUtils.isNumberType(type)
				&& !SQLTypeUtils.isDateType(type)) {
			value = "[NULL]";
		} else {
			if (SQLTypeUtils.isStringType(type)) {
				// 检查是否是html内容...
				if (MyUtils.isHTMLContent(value.toString())) {
					value = "[HTML]";
				}
			}
		}
		return value;
	}
}
