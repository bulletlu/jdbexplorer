package cn.com.qimingx.dbe.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.DBConnectionState;
import cn.com.qimingx.dbe.service.WorkDirectory;
import cn.com.qimingx.dbe.web.DBEListener;
import cn.com.qimingx.spring.BaseMultiActionController;

/**
 * @author Wangwei
 * 
 * 抽象的Controller层 基类
 */
public abstract class AbstractDbeActionController extends
		BaseMultiActionController {

	/**
	 * 检查当前是否已登录数据库
	 * 
	 * @param req
	 * @return
	 */
	protected ProcessResult<DBConnectionState> checkLogin(HttpServletRequest req) {
		ProcessResult<DBConnectionState> pr = new ProcessResult<DBConnectionState>();
		HttpSession sess = req.getSession();
		if (sess == null) {
			pr.setMessage("current Session is null~!");
			return pr;
		}

		DBConnectionState dbcs = DBConnectionState.current(sess);
		if (dbcs == null) {
			pr.setMessage("current DBConnectionState is null~!");
			return pr;
		}

		if (!dbcs.isConnection()) {
			pr.setMessage("current DB Connection invalid~!");
			return pr;
		}

		pr.setSuccess(true);
		pr.setData(dbcs);
		return pr;
	}

	/**
	 * 取得当前工作目录————临时文件目录
	 * 
	 * @param req
	 * @return
	 */
	protected WorkDirectory workDirectory(HttpServletRequest req) {
		return DBEListener.getWorkDirectory(req.getSession(true));
	}
}
