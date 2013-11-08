package cn.com.qimingx.dbe.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONObject;
import cn.com.qimingx.dbe.DBConnectionState;
import cn.com.qimingx.spring.BaseMultiActionController;

/**
 * @author inc062805
 * 
 * DBExplorer过滤器 用于检查当前是否有登录数据库
 */
public class DBEFilter implements Filter {
	// logger
	private static final Log log = LogFactory.getLog(DBEFilter.class);

	public void destroy() {
	}

	// 在过滤器中检查当前是否在登录状态...
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hreq = null;
		if (req instanceof HttpServletRequest) {
			hreq = (HttpServletRequest) req;
		} else {
			throw new RuntimeException("get HttpServletRequest Error~!");
		}

		HttpSession sess = hreq.getSession(true);
		if (!DBConnectionState.isConnection(sess)) {
			// 当前还未登录，或需要重写登录...
			JSONObject json = new JSONObject();
			json.element("notLogin", true);
			resp.setContentType(BaseMultiActionController.HTML_CONTENT_TYPE);
			try {
				resp.getWriter().write(json.toString());
				log.debug("response output:" + json);
			} catch (IOException e) {
				log.error("export JSON 出错：" + e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug("", e);
				}
				throw new RuntimeException(e.getMessage(), e);
			}
		} else {
			chain.doFilter(req, resp);
		}
	}

	public void init(FilterConfig config) throws ServletException {
	}

}
