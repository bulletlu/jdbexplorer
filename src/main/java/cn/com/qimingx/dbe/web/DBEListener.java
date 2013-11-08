package cn.com.qimingx.dbe.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.qimingx.dbe.service.WorkDirectory;
import cn.com.qimingx.dbe.service.impl.TemporaryWorkDirectory;

/**
 * @author inc062805
 * 
 * DBExplorer 监听器,用于监听App的启动与关闭、Session的创建与销毁.
 */
public class DBEListener implements HttpSessionListener, ServletContextListener {
	// logger
	private static final Log log = LogFactory.getLog(DBEListener.class);

	// 当前工作目录对象 在Session中的KEY
	private static final String KEY_WORK_DIRECTORY = "WORK_DIRECTORY_OBJECT";

	// 取得工作目录服务类实例，并以session.id为分类名称
	public static WorkDirectory getWorkDirectory(HttpSession sess) {
		Object obj = sess.getAttribute(KEY_WORK_DIRECTORY);
		if (obj == null) {
			TemporaryWorkDirectory twd = new TemporaryWorkDirectory();
			twd.setTagName(sess.getId());
			twd.initWorkDirectory();
			sess.setAttribute(KEY_WORK_DIRECTORY, twd);
			return twd;
		} else {
			return (WorkDirectory) obj;
		}
	}

	// 应用启动时
	public void contextInitialized(ServletContextEvent event) {
	}

	// Sessiong创建
	public void sessionCreated(HttpSessionEvent event) {
		getWorkDirectory(event.getSession());
	}

	// Session释放
	public void sessionDestroyed(HttpSessionEvent event) {
		String tag = event.getSession().getId();
		getWorkDirectory(event.getSession()).cleanWorkDirectoryByTag(tag);
	}

	// 应用关闭时
	public void contextDestroyed(ServletContextEvent event) {
		File f = TemporaryWorkDirectory.getWorkDirectoryHome(false);
		if (f != null) {
			try {
				FileUtils.deleteDirectory(f);
				log.debug("clean WorkDirectory.ok:" + f.getAbsolutePath());
			} catch (IOException e) {
				log.error("cleanWorkDirectory.error:" + e.getMessage());
			}
		}
	}
}
