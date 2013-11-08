package cn.com.qimingx.dbe.service;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.FieldDataType;
import cn.com.qimingx.dbe.LobObject;
import cn.com.qimingx.dbe.TableColumnInfo;
import cn.com.qimingx.dbe.TableDataInfo;
import cn.com.qimingx.dbe.TableInfo;
import cn.com.qimingx.dbe.action.bean.PkColumnObject;

/**
 * @author Wangwei
 * 
 * 数据库信息服务类
 */
public interface DBInfoService {
	// 设置数据库链接对象
	void setDBConnection(Connection conn);

	// 获取数据库链接对象
	Connection getDBConnection();

	// 取得数据库名称
	String getDatabaseName();

	// 取得DB上的 模式名称列表
	List<String> getSchemas();

	// 取得DB支持的元素类型（例如：表、视图、触发器等...）
	List<String> getElementTypes();

	// 取得DB上的指定类型的元素名称列表
	List<String> getElements(String schema, String... types);

	// 取得指定名称的Table or View的信息
	ProcessResult<TableInfo> getTableInfo(String schema, String name);

	// 取得指定名称的Table or View的列信息
	ProcessResult<List<TableColumnInfo>> getTableColumnInfo(String schema,
			String name);

	// 取得指定Table 的主键信息
	ProcessResult<String> getPrimaryKeys(String schema, String name);

	// 取得指定名称的Table or View的数据内容
	ProcessResult<TableDataInfo> getTableData(String schema, String name,
			int start, int limit, String condition);

	// 执行 更新数据库的SQL语句（eg:INSERT、UPDATE 或 DELETE） 并返回结果..
	ProcessResult<String> executeUpdate(String sql, Map<String, Object> params);

	// 执行 查询数据库的SQL语句（指 SELECT），并返回查询结果..
	ProcessResult<TableInfo> executeQuery(String sql, int start, int limit,
			String condition);

	// 通过sql文件执行SQL
	ProcessResult<String> executeByFile(File file);

	// 读取LOB字段
	ProcessResult<LobObject> readLob(String table, List<PkColumnObject> pks,
			String fieldName, WorkDirectory work);

	// 更新BLOB字段
	ProcessResult<String> updateBLob(String table, List<PkColumnObject> pks,
			String fieldName, File file);

	// 更新CLOB字段
	ProcessResult<String> updateCLob(String table, List<PkColumnObject> pks,
			String field, final String clob);

	// 数据库是否支持 可滚动的ResultSet
	boolean supportScrollableResultSet();

	// 数据库是否支持用于分页的SQL语句（如mysql的limit；oracle的rownum结合嵌套查询等）
	boolean supportLimit();

	// 返回用于分页的SQL语句
	String getLimitSQLString(String originalSQL);

	// 返回当前数据库支持的数据类型列表
	List<FieldDataType> getDataTypes();
}
