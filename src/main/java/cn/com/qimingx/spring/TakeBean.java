package cn.com.qimingx.spring;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author inc062805
 * 
 * 获取Spring Bean的工具类，该类需要初始化后才能使用~~。
 */
public final class TakeBean implements ServletContextListener {
	// Startup
	public void contextInitialized(ServletContextEvent evn) {
		// 初始SpringBeanFactory
		TakeBean.init(evn.getServletContext());
		log.debug("TakeBean Startup~");
	}

	// Shutdown
	public void contextDestroyed(ServletContextEvent env) {
		log.debug("TakeBean Shutdown~");
	}

	// spring mvc 配置空间在上下文中的 key前缀
	private static final String CTX_KEY = "org.springframework.web.servlet.FrameworkServlet.CONTEXT.";

	// Servlet Context
	private ServletContext sctx = null;

	// 应用上下文环境
	private ApplicationContext actx = null;

	// 单例
	private static final TakeBean sbf = new TakeBean();

	// logger
	private static Log log = LogFactory.getLog(TakeBean.class);

	/**
	 * 隐藏构建器
	 */
	private TakeBean() {
	}

	/**
	 * 取得系统配置对象单实例
	 */
	public static TakeBean getInstance() {
		return sbf;
	}

	/**
	 * 通过 ApplicationContext 取得工厂实例
	 */
	public static TakeBean init(ApplicationContext context) {
		getInstance().setApplicationContext(context);
		return sbf;
	}

	/**
	 * 通过 ServletContext 取得工厂实例
	 */
	public static TakeBean init(ServletContext context) {
		getInstance().setServletContext(context);
		return sbf;
	}

	/**
	 * 取得SpringBean
	 */
	public static Object take(String sNm) {
		if (sbf.actx == null) {
			// 检查servlet context
			if (sbf.sctx == null) {
				throw new RuntimeException("ServletContext未初始化~!");
			}
			sbf.initApplicationContext(sbf.sctx);
		}
		if (sbf.actx == null) {
			log.warn("ApplicationContext is null~~!");
			throw new RuntimeException("ApplicationContext未初始化~!");
		}

		return sbf.actx.getBean(sNm);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) {
		Object bean = take(beanName);
		return (T) bean;
	}

	/**
	 * @param context
	 */
	public void setApplicationContext(ApplicationContext context) {
		actx = context;
	}

	public ApplicationContext getApplicationContext() {
		if (actx == null) {
			initApplicationContext(sctx);
		}
		return actx;
	}

	public void setServletContext(ServletContext context) {
		initApplicationContext(context);
	}

	/**
	 * 通过ServletContext 初始化ApplicationContext
	 */
	@SuppressWarnings("unchecked")
	private void initApplicationContext(ServletContext context) {
		// 取得DispatcherServlet空间的上下文
		Enumeration<String> attrs = context.getAttributeNames();
		while (attrs.hasMoreElements()) {
			String attr = attrs.nextElement();
			// log.debug(attr + ":" + context.getAttribute(attr));
			if (attr.indexOf(CTX_KEY) != -1) {
				actx = (ApplicationContext) context.getAttribute(attr);
				break;
			}
		}

		if (actx == null) {
			// 再取得ROOT 上下文
			actx = WebApplicationContextUtils.getWebApplicationContext(context);
		}
	}
}
