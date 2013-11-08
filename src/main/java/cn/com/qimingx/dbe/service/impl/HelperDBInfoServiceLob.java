package cn.com.qimingx.dbe.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.LobObject;
import cn.com.qimingx.dbe.action.bean.PkColumnObject;
import cn.com.qimingx.dbe.service.WorkDirectory;

/**
 * @author inc062805
 * 
 * 协助 AbstractDBInfoService 类实现方法的 LOB类型对象 操作助手类
 */
class HelperDBInfoServiceLob {
	private Log log;
	private AbstractDBInfoService service;

	// 构建器
	public HelperDBInfoServiceLob(AbstractDBInfoService service, Log log) {
		this.service = service;
		this.log = log;
	}

	// 读取LOB类型的字段内容
	public ProcessResult<LobObject> readLob(String table,
			List<PkColumnObject> pks, String fieldName, final WorkDirectory work) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String where = "";
		for (PkColumnObject pk : pks) {
			if (where.length() > 0) {
				where += " and ";
			}
			where += pk.getPk() + "=:" + pk.getPk();
			paramMap.put(pk.getPk(), pk.getPkValueObject());
		}
		String sql = "select " + fieldName + " from " + table;
		sql += " where (" + where + ")";
		log.debug("readLob.sql:" + sql);

		LobStreamingResultSetExtractor extractor;
		extractor = new LobStreamingResultSetExtractor(service.lobHandler, work);
		try {
			service.namedJdbcTemplate.query(sql, paramMap, extractor);
		} catch (Throwable e) {
			log.error("Read LOB Error:" + e.getMessage());
		}

		ProcessResult<LobObject> pr = extractor.getProcessResult();
		if (pr.isFailing()) {
			log.debug("Read LOB Fail:" + pr.getMessage());
		}
		return pr;
	}

	// 更新BLOB类型
	public ProcessResult<String> updateBLob(String table,
			final List<PkColumnObject> pks, String fieldName, final File file) {
		ProcessResult<String> pr = new ProcessResult<String>(false);
		String where = "";
		for (PkColumnObject pk : pks) {
			if (where.length() > 0) {
				where += " and ";
			}
			where += pk.getPk() + "=?";
		}
		String sql = "update " + table + " SET " + fieldName;
		sql += "=? where (" + where + ")";
		log.debug("updateCLob.sql:" + sql);

		InputStream input = null;
		try {
			input = new FileInputStream(file);
			final InputStream finput = input;
			PreparedStatementCallback callback;
			callback = new AbstractLobCreatingPreparedStatementCallback(
					service.lobHandler) {
				protected void setValues(PreparedStatement stat, LobCreator lobc)
						throws SQLException, DataAccessException {
					lobc.setBlobAsBinaryStream(stat, 1, finput, (int) file
							.length());
					int idx = 2;
					for (PkColumnObject pk : pks) {
						stat.setObject(idx++, pk.getPkValueObject());
					}
				}
			};
			service.jdbcTemplate.execute(sql, callback);

			pr.setSuccess(true);
			pr.setData("{success:true,file:'" + file.getName() + "'}");
			return pr;
		} catch (FileNotFoundException e) {
			pr.setMessage("updateBLob Error:" + e.getMessage());
			log.error(pr.getMessage());
			return pr;
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	// 更新CLOB
	public ProcessResult<String> updateCLob(String table,
			final List<PkColumnObject> pks, String field, final String clob) {
		ProcessResult<String> pr = new ProcessResult<String>(false);
		String where = "";
		for (PkColumnObject pk : pks) {
			if (where.length() > 0) {
				where += " and ";
			}
			where += pk.getPk() + "=?";
		}

		String sql = "update " + table + " SET " + field;
		sql += "=? where (" + where + ")";
		log.debug("updateCLob.sql:" + sql);

		try {
			PreparedStatementCallback callback;
			callback = new AbstractLobCreatingPreparedStatementCallback(
					service.lobHandler) {
				protected void setValues(PreparedStatement stat, LobCreator lobc)
						throws SQLException, DataAccessException {
					lobc.setClobAsString(stat, 1, clob);
					int idx = 2;
					for (PkColumnObject pk : pks) {
						stat.setObject(idx++, pk.getPkValueObject());
					}
				}
			};
			service.jdbcTemplate.execute(sql, callback);
			pr.setSuccess(true);
			pr.setData("{success:true}");
			return pr;
		} catch (Throwable e) {
			pr.setMessage("updateCLob Error:" + e.getMessage());
			log.error(pr.getMessage());
			return pr;
		}
	}
}
