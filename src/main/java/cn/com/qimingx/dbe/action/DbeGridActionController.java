package cn.com.qimingx.dbe.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
import cn.com.qimingx.dbe.GridOperator;
import cn.com.qimingx.dbe.action.bean.DataExportBean;
import cn.com.qimingx.dbe.action.bean.GridTableFieldInfoBean;
import cn.com.qimingx.dbe.action.bean.GridTableLoadBean;
import cn.com.qimingx.dbe.action.bean.GridTableLongFieldInfoBean;
import cn.com.qimingx.dbe.action.bean.GridTableUpdateBean;
import cn.com.qimingx.dbe.service.DBInfoService;
import cn.com.qimingx.dbe.service.WorkDirectory;

/**
 * @author Wangwei
 * 
 * 用于提供 DataGridPanel 相关的功能Action
 */
@Controller("dbeGridAction")
public class DbeGridActionController extends AbstractDbeActionController {
	// logger
	private static final Log log = LogFactory
			.getLog(DbeGridActionController.class);

	// Grid操作实现对象
	private GridOperator gridOperator;

	// 装载指定元素的数据
	public void load(HttpServletRequest req, HttpServletResponse resp,
			GridTableLoadBean param) {
		log.debug("call dbeGridAction.load,param:" + param);

		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		//
		DBInfoService service = prDBCS.getData().getDBInfoService();
		ProcessResult<JSON> pr = gridOperator.load(service, param);
		if (pr.isSuccess()) {
			sendJSON(resp, pr.getData().toString());
		} else {
			sendJSON(resp, pr.toJSON());
		}

	}

	// 保存表更新
	public void update(HttpServletRequest req, HttpServletResponse resp,
			GridTableUpdateBean param) {
		log.debug("call dbeGridAction.update,param:" + param);

		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// process
		DBInfoService service = prDBCS.getData().getDBInfoService();
		ProcessResult<String> pr = gridOperator.update(service, param);
		if (pr.isFailing()) {
			sendErrorJSON(resp, pr.toJSON());
		} else {
			sendJSON(resp, pr.toJSON());
		}

	}

	// 删除表记录...
	public void remove(HttpServletRequest req, HttpServletResponse resp,
			GridTableUpdateBean params) {
		log.debug("call dbeGridAction.remove,param:" + params);
		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// process
		DBInfoService service = prDBCS.getData().getDBInfoService();
		ProcessResult<String> pr = gridOperator.remove(service, params);
		if (pr.isSuccess()) {
			sendJSON(resp, pr.toJSON());
		} else {
			sendErrorJSON(resp, pr.toJSON());
		}
	}

	// 取得外键可选值
	public void fkvalue(HttpServletRequest req, HttpServletResponse resp) {
		// 取得外键表和外键字段 名称
		String t = req.getParameter("table");
		String f = req.getParameter("field");
		log.debug("call dbeGridAction.fkvalue," + t + "." + f);

		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// process
		DBInfoService service = prDBCS.getData().getDBInfoService();
		ProcessResult<JSON> pr = gridOperator.getFKValues(service, t, f);
		if (pr.isSuccess()) {
			sendJSON(resp, pr.getData().toString());
		} else {
			sendJSON(resp, pr.toJSON());
		}
	}

	// 数据导出
	public void export(HttpServletRequest req, HttpServletResponse resp,
			DataExportBean param) {
		log.debug("call dbeGridAction.export.params:" + param);
		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// process
		DBInfoService service = prDBCS.getData().getDBInfoService();
		WorkDirectory wd = workDirectory(req);// 当前临时文件目录
		ProcessResult<JSON> pr = gridOperator.export(service, param, wd);
		if (pr.isSuccess()) {
			sendJSON(resp, pr.getData().toString());
		} else {
			sendError(resp, 500, pr.toJSON());
		}
	}

	// 读取长类型字段...
	public void readlob(HttpServletRequest req, HttpServletResponse resp,
			GridTableFieldInfoBean param) {
		log.debug("call dbeGridAction.readlob:" + param);
		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// process
		DBInfoService service = prDBCS.getData().getDBInfoService();
		WorkDirectory wd = workDirectory(req);// 当前临时文件目录
		ProcessResult<JSON> pr = gridOperator.readLob(service, param, wd);
		if (pr.isSuccess()) {
			sendJSON(resp, pr.getData().toString());
		} else {
			sendError(resp, 500, pr.toJSON());
		}
	}

	// 上传文件 并更新到具体的Blob字段
	public void updateblob(HttpServletRequest req, HttpServletResponse resp,
			GridTableLongFieldInfoBean bean) {
		log.debug("call dbeGridAction.updateblob,param:" + bean.getName());

		// 创建资源的临时文件
		File file = workDirectory(req).newFile(bean.getName(), null);
		OutputStream output = null;
		InputStream input = null;
		try {
			output = new FileOutputStream(file);
			input = bean.getFile().getInputStream();
			FileCopyUtils.copy(input, output);
		} catch (Exception e) {
			log.debug("create temporary file error:" + e.getMessage());
			sendError(resp, 500, "{success:false,msg:'create file error!'}");
			return;
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
		}

		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// process
		DBInfoService service = prDBCS.getData().getDBInfoService();
		ProcessResult<String> pr = gridOperator.updateBlob(service, bean, file);
		if (pr.isSuccess()) {
			sendJSON(resp, pr.getData());
		} else {
			sendError(resp, 500, pr.toJSON());
		}
	}

	// 上传文件 并更新到具体的Blob字段
	public void updateclob(HttpServletRequest req, HttpServletResponse resp,
			GridTableFieldInfoBean param) {
		log.debug("call dbeGridAction.updateclob,param:" + param);
		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// process
		ProcessResult<String> pr = null;
		String clob = req.getParameter("clob");
		if (clob == null) {
			pr = new ProcessResult<String>(false);
			pr.setMessage("updateclob Error:clob is null~~!");
			log.error(pr.getMessage());
		} else {
			log.debug("call dbeGridAction.upload,clob:" + clob);
			DBInfoService service = prDBCS.getData().getDBInfoService();
			pr = gridOperator.updateClob(service, param, clob);
		}

		// return
		if (pr.isSuccess()) {
			sendJSON(resp, pr.getData());
		} else {
			sendError(resp, 500, pr.toJSON());
		}
	}
	


	public GridOperator getGridOperator() {
		return gridOperator;
	}

	@Autowired
	public void setGridOperator(GridOperator gridOperator) {
		this.gridOperator = gridOperator;
	}
}
