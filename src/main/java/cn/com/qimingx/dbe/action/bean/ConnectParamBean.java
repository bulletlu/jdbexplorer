package cn.com.qimingx.dbe.action.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.qimingx.core.WebParamBean;
import cn.com.qimingx.dbe.DBEConfig;
import cn.com.qimingx.dbe.DBTypeInfo;

/**
 * @author inc062805
 * 
 * 连接DB的参数Bean
 */
public class ConnectParamBean extends WebParamBean {
	private static final Log log = LogFactory.getLog(ConnectParamBean.class);

	private String dbtype;
	private String user;
	private String password;
	private String url;
	private String dbname;
	private String dbhost;

	// ..
	public static ConnectParamBean fromCookieValue(String cookie) {
		ConnectParamBean param = new ConnectParamBean();
		if (cookie.length() < 1) {
			String typeName = null;
			if (DBEConfig.getInstance().getDbTypeNames().hasNext()) {
				typeName = DBEConfig.getInstance().getDbTypeNames().next();
			}
			param.setDbtype(typeName);
			param.setDbhost("localhost");
			param.setDbname("dbname");
			param.setUser("username");
		} else {
			String[] items = cookie.split("#");
			if (items.length != 4) {
				log.error("cookieValue format error:" + cookie);
			}
			for (int i = 0; i < items.length; i++) {
				if (i == 0)
					param.setDbtype(items[0]);
				if (i == 1)
					param.setDbhost(items[1]);
				if (i == 2)
					param.setDbname(items[2]);
				if (i == 3)
					param.setUser(items[3]);
			}

		}

		//
		DBTypeInfo type = DBEConfig.getInstance().getDBTypeInfo(
				param.getDbtype());
		if (type != null) {
			String url = type.getUrl();
			url = url.replaceAll("<server>", param.getDbhost());
			url = url.replaceAll("<dbname>", param.getDbname());
			param.setUrl(url);
		}
		return param;
	}

	//
	public String toCookieValue() {
		String value = getDbtype() + "#" + getDbhost();
		value += "#" + getDbname() + "#" + getUser();
		// value += "#" + getUrl();
		// +"#"param.getPassword(),不记录密码
		return value;
	}

	public String getHistoryRecord() {
		String value = getDbtype() + "|" + getDbhost();
		value += "|" + getDbname() + "|" + getUser();
		value += "|" + getUrl();
		// +"#"param.getPassword(),不记录密码
		value = "{history:'" + value + "'}";
		return value;
	}

	public String getDbhost() {
		return dbhost;
	}

	public void setDbhost(String dbhost) {
		this.dbhost = dbhost;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getDbtype() {
		return dbtype;
	}

	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
