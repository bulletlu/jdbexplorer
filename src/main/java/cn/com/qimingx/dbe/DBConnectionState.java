package cn.com.qimingx.dbe;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.action.bean.ConnectParamBean;
import cn.com.qimingx.dbe.service.DBInfoService;
import cn.com.qimingx.dbe.service.impl.DefaultDBInfoService;
import cn.com.qimingx.utils.MyUtils;
import cn.com.qimingx.utils.SQLUtils;

/**
 * @author Wangwei
 * 
 * 数据库连接状态描述对象
 */
public class DBConnectionState implements Serializable {
	// Logger
	private static final Log log = LogFactory.getLog(DBConnectionState.class);

	// serialVersion
	private static final long serialVersionUID = 8886397095815863532L;

	// session key.
	private static final String KEY_CURRENT_STATE = "cn.com.qimingx.dbe.DBConnectionState.KEY";

	// DB Connection
	private transient Connection dbConnection;

	// DB TypeInfo
	private transient DBTypeInfo dbType;

	// service
	private transient DBInfoService service;

	/**
	 * 隐藏构建器
	 */
	private DBConnectionState(Connection conn, DBTypeInfo dbtype) {
		dbConnection = conn;
		this.dbType = dbtype;
	}

	/**
	 * 从Session中取得当前的实例
	 */
	public static DBConnectionState current(HttpSession sess) {
		Object obj = sess.getAttribute(KEY_CURRENT_STATE);
		if (obj == null) {
			return null;
		} else {
			return (DBConnectionState) obj;
		}
	}

	/**
	 * 判断当前是否已经和数据库取得链接（即已经登录）
	 */
	public static boolean isConnection(HttpSession sess) {
		if (sess == null) {
			return false;
		}
		DBConnectionState dbcs = current(sess);
		return dbcs == null ? false : dbcs.isConnection();
	}

	// connect to DB...
	public static ProcessResult<String> connect(ConnectParamBean param,
			HttpSession sess) {
		// get typeinfo.
		String typeName = param.getDbtype();
		DBTypeInfo type = DBEConfig.getInstance().getDBTypeInfo(typeName);

		ProcessResult<String> pr = new ProcessResult<String>();
		// check type
		if (type == null) {
			log.error("不支持的DBType,typeName:" + param.getDbtype());
			pr.setMessage("不支持的DBType:" + param.getDbtype());
			return pr;
		}

		// load class
		try {
			Class.forName(type.getDriver());
		} catch (ClassNotFoundException e) {
			log.error("Load JDBC Driver Class出错:" + type.getDriver());
			pr.setMessage("无法加载JDBC驱动：" + type.getDriver());
			return pr;
		}

		// connect to
		try {
			//
			String url = param.getUrl();
			String user = param.getUser();
			String passwd = param.getPassword();
			Connection conn = DriverManager.getConnection(url, user, passwd);

			// 
			DBConnectionState dbcs = new DBConnectionState(conn, type);
			sess.setAttribute(KEY_CURRENT_STATE, dbcs);

			pr.setSuccess(true);
			return pr;
		} catch (SQLException e) {
			String msg = "链接数据库出错：" + e.getMessage();
			log.error(msg);
			pr.setMessage(msg);
			return pr;
		}
	}

	/**
	 * 释放数据库链接，销毁链接描述对象，用于Logout~~
	 */
	public void destroy(HttpSession sess) {
		// close 链接..
		if (dbConnection != null) {
			try {
				if (!dbConnection.isClosed()) {
					dbConnection.close();
				}
			} catch (SQLException e) {
				log.error("Close DB Connect Error：" + e.getMessage());
			}
		}

		// remove dbcs
		sess.removeAttribute(KEY_CURRENT_STATE);
	}

	// 取得数据库的详细属性信息
	public JSON getDBProperties() {
		try {
			DatabaseMetaData dbmd = dbConnection.getMetaData();
			// create dataItems
			JSONArray items = new JSONArray();
			Map<String, String> map = SQLUtils.getDBInfos(dbmd);
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String name = it.next();
				JSONObject data = new JSONObject();
				data.element("name", name);
				data.element("value", map.get(name));
				items.add(data);
			}

			// create MetaData
			JSONArray fields = new JSONArray();
			JSONObject field = new JSONObject();
			field.element("name", "name");
			fields.add(field);
			field = new JSONObject();
			field.element("name", "value");
			fields.add(field);
			JSONObject meta = new JSONObject();
			meta.element("totalProperty", "total");
			meta.element("root", "items");
			meta.element("fields", fields);

			// create return JSON
			JSONObject json = new JSONObject();
			json.element("metaData", meta);
			json.element("total", 100);
			json.element("items", items);
			return json;
		} catch (SQLException e) {
			String msg = "getDBProperties Error:" + e.getMessage();
			log.error(msg);
			throw new RuntimeException(msg);
		}
	}

	// 获取 提供DB信息的服务接口
	public DBInfoService getDBInfoService() {
		if (service == null) {
			String clsName = dbType.getService();
			Object o = MyUtils.newObjectOfClassName(clsName);
			if (o != null) {
				service = (DBInfoService) o;
			} else {
				service = new DefaultDBInfoService();
			}
			service.setDBConnection(dbConnection);
			log.debug("use " + service.getClass().getName() + " instance");
		}
		return service;
	}

	public boolean isConnection() {
		boolean bool = dbConnection != null;
		if (bool) {
			try {
				return !dbConnection.isClosed();
			} catch (SQLException e) {
				log.error("call isConnection.isClosed.error:" + e.getMessage());
				return false;
			}
		}
		return false;
	}
}
