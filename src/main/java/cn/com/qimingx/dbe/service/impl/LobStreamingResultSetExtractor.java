package cn.com.qimingx.dbe.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.AbstractLobStreamingResultSetExtractor;
import org.springframework.jdbc.support.lob.AbstractLobHandler;
import org.springframework.util.FileCopyUtils;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.LobObject;
import cn.com.qimingx.dbe.service.WorkDirectory;

/**
 * @author inc062805
 * 
 * 读取LOB流的工具类
 */
public class LobStreamingResultSetExtractor extends
		AbstractLobStreamingResultSetExtractor {
	private static final Log log = LogFactory
			.getLog(LobStreamingResultSetExtractor.class);

	private AbstractLobHandler lobHandler;
	private WorkDirectory work;
	private ProcessResult<LobObject> pr = new ProcessResult<LobObject>();

	public LobStreamingResultSetExtractor(AbstractLobHandler lobHandler,
			WorkDirectory work) {
		this.lobHandler = lobHandler;
		this.work = work;
	}

	@Override
	protected void handleNoRowFound() throws DataAccessException {
		pr.setMessage("Read LOB Error:Not Row Found~!");
	}

	@Override
	protected void streamData(ResultSet rs) throws SQLException, IOException,
			DataAccessException {
		//
		int type = rs.getMetaData().getColumnType(1);
		LobObject lob = new LobObject(type);
		File file = null;
		if (lob.isBLOB()) {
			file = work.newFile(null, ".blob");
		} else {
			file = work.newFile(null, ".txt");
		}
		lob.setValue(file);
		pr.setData(lob);

		//
		InputStream in = null;
		OutputStream out = null;
		Reader reader = null;
		Writer writer = null;
		try {
			if (lob.isBLOB()) {
				out = new FileOutputStream(file);
				in = lobHandler.getBlobAsBinaryStream(rs, 1);
				if (in != null) {
					int length = FileCopyUtils.copy(in, out);
					pr.setMessage("Read BLOB OK:" + length);
				} else {
					pr.setMessage("Read BLOB stream is null.");
					lob.setValue(null);
				}
			} else {
				out = new FileOutputStream(file);
				writer = new OutputStreamWriter(out, "utf-8");
				reader = lobHandler.getClobAsCharacterStream(rs, 1);
				if (reader != null) {
					int length = IOUtils.copy(reader, writer);
					writer.flush();
					out.flush();
					pr.setMessage("Read CLOB OK:" + length);
				} else {
					pr.setMessage("Read CLOB stream is null.");
					lob.setValue(null);
				}
			}
			pr.setSuccess(true);
			log.debug(pr.getMessage());
		} catch (Throwable e) {
			pr.setSuccess(false);
			pr.setMessage("Read LOB Error:" + e.getMessage());
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(writer);
		}
	}

	public ProcessResult<LobObject> getProcessResult() {
		return pr;
	}
}
