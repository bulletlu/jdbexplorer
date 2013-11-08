package cn.com.qimingx.mydb;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author inc062805
 * 
 * Servlet容器监听器，用于启动和关闭 DerbyDBEnginery
 */
public class DerbyDBEngineryInitListener implements ServletContextListener {
	// 日志输出
	private static final transient Log log = LogFactory
			.getLog(DerbyDBEngineryInitListener.class);

	private DerbyDBEnginery derby;

	// 应用系统启动
	public void contextInitialized(ServletContextEvent evn) {
		log.debug("DerbyDBEnginery Startuping~");
		String home = evn.getServletContext().getInitParameter("db.home");
		if (home == null) {
			home = evn.getServletContext().getRealPath("/");
		}
		File dbHome = new File(home);
		dbHome = new File(dbHome, "derbydb");
		derby = new DerbyDBEnginery(dbHome);
		log.debug("db.home:" + dbHome.getAbsolutePath());
		derby.startup();
		if (derby.isStarted(5)) {
			log.debug("Startup DerbyDBEnginery OK~.");
		} else {
			log.error("Startup DerbyDBEnginery Fail~.");
		}
	}

	// 应用系统关闭
	public void contextDestroyed(ServletContextEvent env) {
		log.debug("DerbyDBEnginery Shutdowning~");
		if (derby != null) {
			derby.shutdown();
		}
		log.debug("Shutdown DerbyDBEnginery OK~.");
	}
}
