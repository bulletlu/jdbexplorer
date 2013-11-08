package cn.com.qimingx.dbe.service.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.lob.DefaultLobHandler;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.FieldDataType;
import cn.com.qimingx.dbe.LobObject;
import cn.com.qimingx.dbe.TableColumnInfo;
import cn.com.qimingx.dbe.TableDataInfo;
import cn.com.qimingx.dbe.TableInfo;
import cn.com.qimingx.dbe.action.bean.PkColumnObject;
import cn.com.qimingx.dbe.service.DBInfoService;
import cn.com.qimingx.dbe.service.WorkDirectory;
import cn.com.qimingx.utils.sql.SQLExec;

/**
 * @author inc062805
 * 
 * 抽象的DBInfoService实现，提供了对所有类型数据库都适用的实现，其中某些实现可能不是最恰当的，因此子类可以对其进行覆盖。
 */
public abstract class AbstractDBInfoService implements DBInfoService {
	// Logger
	private static final Log log = LogFactory
			.getLog(AbstractDBInfoService.class);

	// db connection
	protected Connection conn;

	// database meta data
	protected DatabaseMetaData meta;

	// spring jdbcTemplate
	protected JdbcTemplate jdbcTemplate;

	// spring namedJdbcTemplate
	protected NamedParameterJdbcTemplate namedJdbcTemplate;

	// LobHandler
	protected DefaultLobHandler lobHandler;

	// 助手类
	protected HelperDBInfoServiceBase baseHelper;
	protected HelperDBInfoServiceTable tableHelper;
	protected HelperDBInfoServiceLob lobHelper;

	// 设置db connection
	public void setDBConnection(Connection conn) {
		this.conn = conn;

		DataSource ds = new SingleConnectionDataSource(conn, true);
		jdbcTemplate = new JdbcTemplate(ds);
		namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		lobHandler = new DefaultLobHandler();

		try {
			meta = conn.getMetaData();
		} catch (SQLException e) {
			String msg = "set Connection to DBInfoService Error:";
			msg += e.getMessage();
			log.error(msg);
			throw new RuntimeException(msg);
		}

		// 构建助手类
		baseHelper = new HelperDBInfoServiceBase(this, log);
		tableHelper = new HelperDBInfoServiceTable(this, log);
		lobHelper = new HelperDBInfoServiceLob(this, log);
	}

	// 取得当前数据库链接对象
	public Connection getDBConnection() {
		return this.conn;
	}

	// 取得数据库名称
	public String getDatabaseName() {
		return baseHelper.getDatabaseName();
	}

	// 取得数据库模式列表
	public List<String> getSchemas() {
		return baseHelper.getSchemas();
	}

	// 取得数据库元素类型（例如：表、视图、触发器等等）列表
	public List<String> getElementTypes() {
		return baseHelper.getElementTypes();
	}

	// 取得指定模式下指定类型的元素的列表
	public List<String> getElements(String schema, String... types) {
		return baseHelper.getElements(schema, types);
	}

	// 数据库是否支持可滚动记录集
	public boolean supportScrollableResultSet() {
		return baseHelper.supportScrollableResultSet();
	}

	// 执行 更新数据库的SQL语句（eg:INSERT、UPDATE 或 DELETE） 并返回结果..
	public ProcessResult<String> executeUpdate(String sql,
			Map<String, Object> params) {
		return tableHelper.executeUpdate(sql, params);
	}

	// 执行 查询数据库的SQL语句（指 SELECT），并返回查询结果..
	public ProcessResult<TableInfo> executeQuery(String sql, int start,
			int limit, String condition) {
		return tableHelper.executeQuery(sql, start, limit, condition);
	}

	// 取得列信息
	public ProcessResult<List<TableColumnInfo>> getTableColumnInfo(
			String schema, String name) {
		return tableHelper.getTableColumnInfo(schema, name);
	}

	// 取得主键列信息
	public ProcessResult<String> getPrimaryKeys(String schema, String name) {
		return tableHelper.getPrimaryKeys(schema, name);
	}

	// 取得指定模式下 指定Table 的信息
	public ProcessResult<TableInfo> getTableInfo(String schema, String name) {
		return tableHelper.getTableInfo(schema, name);
	}

	// 取得指定模式下 指定Table的数据，并进行分页
	public ProcessResult<TableDataInfo> getTableData(final String schema,
			final String name, int start, int limit, String condition) {
		return tableHelper.getTableData(schema, name, start, limit, condition);
	}

	// 读取LOB值
	public ProcessResult<LobObject> readLob(String table,
			List<PkColumnObject> pks, String fieldName, final WorkDirectory work) {
		return lobHelper.readLob(table, pks, fieldName, work);
	}

	// 更新BLOB类型
	public ProcessResult<String> updateBLob(String table,
			List<PkColumnObject> pks, String fieldName, final File file) {
		return lobHelper.updateBLob(table, pks, fieldName, file);
	}

	// 更新CLOB
	public ProcessResult<String> updateCLob(String table,
			List<PkColumnObject> pks, String field, final String clob) {
		return lobHelper.updateCLob(table, pks, field, clob);
	}

	// 通过.sql文件来执行sql脚本
	public ProcessResult<String> executeByFile(File file) {
		ProcessResult<String> pr = new ProcessResult<String>();
		SQLExec sqlexec = new SQLExec();
		sqlexec.setSrc(file);
		sqlexec.setPrint(true);
		// sqlexec.setOutput(new File("c:/out.txt"));
		try {
			sqlexec.execute(this.getDBConnection(), false);
			String data = sqlexec.getResult();
			pr.setData(data);
		} catch (Exception e) {
			log.debug("执行数据脚本出错" + e.getMessage());
			pr.setMessage("executeByFile 执行数据脚本出错~~!");
			pr.setFailing(true);
		}
		pr.setSuccess(true);
		return pr;

	}

	// 返回数据类型列表，该方法中仅返回标准的、通用的数据类型信息
	public List<FieldDataType> getDataTypes() {
		List<FieldDataType> fdts = new ArrayList<FieldDataType>();
		fdts.add(new FieldDataType("INTEGER"));
		fdts.add(new FieldDataType("SMALLINT"));
		fdts.add(new FieldDataType("NUMERIC"));
		fdts.add(new FieldDataType("DECIMAL"));
		fdts.add(new FieldDataType("CHAR"));
		fdts.add(new FieldDataType("VARCHAR"));
		fdts.add(new FieldDataType("DATE"));
		fdts.add(new FieldDataType("TIMESTAMP"));
		fdts.add(new FieldDataType("CLOB"));
		fdts.add(new FieldDataType("BLOB"));
		return fdts;
	}
}
