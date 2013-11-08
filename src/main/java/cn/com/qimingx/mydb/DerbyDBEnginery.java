package cn.com.qimingx.mydb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.drda.NetworkServerControl;

/**
 * @author Wangwei
 * 
 *         借助Derby提供一个轻量级数据库支持
 */
public class DerbyDBEnginery {
	// 日志访问器
	private static transient Log log = LogFactory.getLog(DerbyDBEnginery.class);

	//
	public static void main(String[] args) {
		File dbhome = new File("c:/temp/derbyDB");
		DerbyDBEnginery derby = new DerbyDBEnginery(dbhome);
		derby.startup();
		if (derby.isStarted(3)) {
			log.info("DerbyDBEnginery Startup~,Please input 'Exit' Shutdown.");
			InputStreamReader in = new InputStreamReader(System.in);
			BufferedReader reader = new BufferedReader(in);
			while (true) {
				try {
					String cmd = reader.readLine();
					if (cmd.equalsIgnoreCase("exit")) {
						derby.shutdown();
						break;
					} else {
						log.info("bad command~,Please again input~:");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 默认构造器
	 */
	public DerbyDBEnginery(File dbhome) {
		this("127.0.0.1", 1527, dbhome);
	}

	/**
	 * 构造器
	 */
	public DerbyDBEnginery(String host, int port, File dbhome) {
		this.port = port;
		this.host = host;
		if (host == null || host.length() == 0) {
			this.host = "127.0.0.1";
		}

		this.dbhome = dbhome;
		if (dbhome == null) {
			this.dbhome = new File("databases");
		}
		if (!this.dbhome.exists()) {
			this.dbhome.mkdirs();
		}

		// 设置数据库服务器HOME
		String path = dbhome.getAbsolutePath();
		System.setProperty("derby.system.home", path);

		log.debug("DerbyDBEnginery 初始完成:[host=" + host + ";port=" + port
				+ ";home=" + path + "]");
	}

	/**
	 * 启动数据库Server.
	 */
	public void startup() {
		try {
			InetAddress iAddr = InetAddress.getByName(host);
			server = new NetworkServerControl(iAddr, port);
			server.start(dblog);
			if (isStarted(3)) {
				log.info("数据库服务启动成功~!");
			} else {
				log.warn("数据库服务启动失败~!");
			}
		} catch (Exception e) {
			log.error("启动数据库时发生错误：");
		}

	}

	/**
	 * 关闭数据库Server
	 */
	public void shutdown() {
		if (server == null) {
			log.warn("数据库服务器尚未启动.");
			return;
		}

		try {
			server.shutdown();
			log.info("成功关闭数据库服务器");
		} catch (Exception e) {
			log.error("关闭数据库服务器出错：", e);
		}
	}

	/**
	 * 检查服务器是否已启动..
	 */
	public boolean isStarted(int retrys) {
		for (int i = 1; i <= retrys; i++) {
			try {
				Thread.sleep(500);
				server.ping();
				return true;
			} catch (Exception e) {
				if (i == retrys) {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 服务监听主机
	 */
	private String host;

	/**
	 * 服务监听端口
	 */
	private int port;

	/**
	 * 数据库主目录.
	 */
	private File dbhome;

	/**
	 * 数据库日志输出..
	 */
	private PrintWriter dblog;

	/**
	 * 数据库服务器控制器
	 */
	private NetworkServerControl server = null;

	public final String getHost() {
		return host;
	}

	public final void setHost(String host) {
		this.host = host;
	}

	public final int getPort() {
		return port;
	}

	public final void setPort(int port) {
		this.port = port;
	}

	public final File getDbhome() {
		return dbhome;
	}

	public final void setDbhome(File dbhome) {
		this.dbhome = dbhome;
	}

	public final PrintWriter getDblog() {
		return dblog;
	}

	public final void setDblog(PrintWriter dblog) {
		this.dblog = dblog;
	}
}
