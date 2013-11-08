package cn.com.qimingx.dbe.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.DBConnectionState;
import cn.com.qimingx.dbe.SQLQueryOperator;
import cn.com.qimingx.dbe.action.bean.GridQueryLoadBean;
import cn.com.qimingx.dbe.service.DBInfoService;
import cn.com.qimingx.spring.UploadFile;

/**
 * @author Wangwei
 * 
 * 用于SQLQueryPanel 相关的功能Action
 */
@Controller("dbeSQLQueryAction")
public class DbeSQLQueryActionController extends AbstractDbeActionController {
	// logger
	private static final Log log = LogFactory
			.getLog(DbeSQLQueryActionController.class);

	//
	private SQLQueryOperator sqlQueryOperator;
	
	//打开sql文件
	public void openSqlFile(HttpServletRequest req, HttpServletResponse resp,
			UploadFile param){
		log.debug("open sqlfile dbeSQLQueryAction.openSqlFile," + param);
		log.debug("param.getName():" + param.getName() + "param.getType():"
				+ param.getType());
		InputStream input =null;
		Writer  writer = new StringWriter();
		String str = null;
		try {
			input = param.getFile().getInputStream();
			IOUtils.copy(input, writer);
			str =writer.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(writer);
		}
 
		sendJSON(resp, str.toString() );
		
	}
	//执行sql文件
	public void execSqlFile(HttpServletRequest req, HttpServletResponse resp,
			UploadFile param){
		log.debug("execute sqlfile dbeSQLQueryAction.execSqlFile," + param);
		log.debug("param.getName():" + param.getName() + "param.getType():"
				+ param.getType());
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// 取得sql文件..
		File file = workDirectory(req).newFile("temp.sql", null);
		OutputStream output = null;
		InputStream input = null;

		try {
			output = new FileOutputStream(file);
			input = param.getFile().getInputStream();
			FileCopyUtils.copy(input, output);

			//
			DBInfoService service = prDBCS.getData().getDBInfoService();
			ProcessResult<String> pr = service.executeByFile(file);
			if (!pr.isSuccess()) {
				sendErrorJSON(resp, pr.toJSON());
			}else{
				sendJSON(resp, pr.getData().toString());
			}
		} catch (Exception e) {
			log.debug("execSqlFile.error:" + e.getMessage());
			sendErrorJSON(resp, e.getMessage());
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
		
	}

	// 执行SQL语句
	public void query(HttpServletRequest req, HttpServletResponse resp,
			GridQueryLoadBean param) {
		log.debug("call dbeSQLQueryAction.query," + param);

		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		//
		DBInfoService service = prDBCS.getData().getDBInfoService();
		ProcessResult<JSON> pr = sqlQueryOperator.execute(service, param);

		// return
		if (pr.isSuccess()) {
			JSON json = pr.getData();
			sendJSON(resp, json.toString());
		} else {
			sendErrorJSON(resp, pr.toJSON());
		}
	}

	// 保存查询语句为.sql文件
	public void saveAsSQL(HttpServletRequest req, HttpServletResponse resp) {
		String fileName = "新建SQLDocument.sql";
		String sql = req.getParameter("sql");
		sql = sql == null ? "" : sql;
		log.debug("call dbeSQLQueryAction.saveasfile.sql:" + sql);
		download(resp, fileName, sql.getBytes());
	}

	public SQLQueryOperator getSqlQueryOperator() {
		return sqlQueryOperator;
	}

	@Autowired
	public void setSqlQueryOperator(SQLQueryOperator sqlQueryOperator) {
		this.sqlQueryOperator = sqlQueryOperator;
	}
}
