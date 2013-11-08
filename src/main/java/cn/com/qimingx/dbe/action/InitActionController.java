package cn.com.qimingx.dbe.action;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.WebUtils;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.DBConnectionState;
import cn.com.qimingx.dbe.DBEConfig;
import cn.com.qimingx.dbe.action.bean.ConnectParamBean;
import cn.com.qimingx.spring.BaseMultiActionController;

/**
 * @author Wangwei
 * 
 * 用于提供初始化、登录、注销 等系统功能的Action
 */
@Controller("initAction")
public class InitActionController extends BaseMultiActionController {
	// logger
	private static final Log log = LogFactory
			.getLog(InitActionController.class);

	// 保存登录历史的cookie名称
	private static final String LOGIN_COOKIE_NAME = "dbelogin";

	// 保存登录历史条目的最大个数
	private static final int LOGIN_ITEM_LENGTH = 5;

	// System init，检查登录状态
	public void init(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("call InitAction.init~~");
		HttpSession sess = req.getSession();
		boolean isLogin = DBConnectionState.isConnection(sess);
		JSONObject json = new JSONObject();
		json.element("success", true);
		json.element("login", isLogin);
		sendJSON(resp, json.toString());
	}

	// Support db types
	public void dbtypes(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("call InitAction.dbtypes~~");
		String json = DBEConfig.getInstance().getDBTypeOptions();
		sendJSON(resp, json);
	}

	// 得到登陆历史
	public void history(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("get login history~~");
		String historys = "";
		Cookie cookie = WebUtils.getCookie(req, LOGIN_COOKIE_NAME);
		if (cookie != null) {
			log.debug("读取到了登录历史...:" + cookie.getValue());

			String[] loginItems = cookie.getValue().split("&");
			for (String loginItem : loginItems) {
				ConnectParamBean param = null;
				try {
					param = ConnectParamBean.fromCookieValue(loginItem);
				} catch (Throwable e) {
					log.warn("ConnectParamBean.fromCookieValue.error:"
							+ e.getMessage() + "[" + loginItem + "]");
					continue;
				}

				if (historys.length() > 0) {
					historys += ",";
				}
				historys += param.getHistoryRecord();
			}

		}

		String json = null;
		if (historys != null && historys.length() > 0) {
			json = "{root:[" + historys + "]}";
		} else {
			json = "{root:[{ history:'没有历史登录记录~'}]}";
		}
		sendJSON(resp, json);
	}

	// Login
	public void login(HttpServletRequest req, HttpServletResponse resp,
			ConnectParamBean param) {
		log.debug("call InitAction.login,param:" + param);
		ProcessResult<String> pr;
		pr = DBConnectionState.connect(param, req.getSession(true));

		JSONObject json = new JSONObject();
		if (pr.isSuccess()) {
			json.element("success", true);
			storeConnectionParam(req, resp, param);
		} else {
			json.element("success", false);
			json.element("msg", pr.getMessage());
		}
		sendJSON(resp, json.toString());
	}

	// Logout~~
	public void logout(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("call InitAction.logout~~");
		HttpSession sess = req.getSession();
		if (sess != null) {
			DBConnectionState dbcs = DBConnectionState.current(sess);
			if (dbcs != null) {
				dbcs.destroy(sess);
				sess.invalidate();
			}
		}

		JSONObject json = new JSONObject();
		json.element("success", true);
		sendJSON(resp, json.toString());
	}

	// Load：从登录历史中load上次登录信息
	public void load(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("call InitAction.load~~");
		Cookie cookie = WebUtils.getCookie(req, LOGIN_COOKIE_NAME);
		if (cookie != null) {
			String[] loginItems = cookie.getValue().split("&");
			if (loginItems.length > 0) {
				JSONArray jsonData = new JSONArray();
				String value = loginItems[0];
				ConnectParamBean pb = ConnectParamBean.fromCookieValue(value);
				jsonData.add(JSONSerializer.toJSON(pb));

				JSONObject json = new JSONObject();
				json.element("success", true);
				json.element("data", jsonData);
				sendJSON(resp, json.toString());
			}
		}
	}

	// welcome,用于显示数据库属性信息
	public void welcome(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("call InitAction.welcome~~");
		HttpSession sess = req.getSession(true);
		DBConnectionState dbcs = DBConnectionState.current(sess);
		if (dbcs != null) {
			JSON json = dbcs.getDBProperties();
			sendJSON(resp, json.toString());
		} else {
			JSONObject json = new JSONObject();
			json.element("success", true);
			sendJSON(resp, json.toString());
		}
	}

	// 存储链接参数 到登录历史中
	private void storeConnectionParam(HttpServletRequest req,
			HttpServletResponse resp, ConnectParamBean param) {
		// 当前登录信息 cookieValue
		String value = param.toCookieValue();

		// 检查是否已有cookie存在，以确定是创建新的登录历史 还是更新历史记录
		Cookie cookie = WebUtils.getCookie(req, LOGIN_COOKIE_NAME);
		if (cookie == null) {
			log.debug("无登录历史....");
			cookie = new Cookie(LOGIN_COOKIE_NAME, value);
		} else {
			log.debug("发现登录历史,准备重新生成登录历史列表~：");
			String[] historys = cookie.getValue().split("&");
			String current = value;
			int count = 1;
			for (String history : historys) {
				if (count == LOGIN_ITEM_LENGTH) {
					// 仅保存指定个数的登录历史..
					break;
				}
				if (!current.equalsIgnoreCase(history)) {
					value += "&";
					value += history;
					++count;
				}
			}
			cookie.setValue(value);
		}

		// 写出/更新Cookie值...
		cookie.setMaxAge(Integer.MAX_VALUE);
		resp.addCookie(cookie);
		log.debug("Store DBConnectionInfo To Cookie:" + value);
	}
}
