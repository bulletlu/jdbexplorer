package cn.com.qimingx.dbe.service.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.qimingx.dbe.service.WorkDirectory;

/**
 * @author inc062805
 * 
 * 临时工作目录工具类
 */
public class TemporaryWorkDirectory implements WorkDirectory {
	// logger
	private static final Log log = LogFactory
			.getLog(TemporaryWorkDirectory.class);

	// 当前工作目录的根目录
	private static File home = null;

	// 分类名称
	private String tag = null;
	// 当前目录
	private File dir = null;

	// 设置分类名称
	public void setTagName(String tag) {
		this.tag = tag;
	}

	// 取得临时文件目录的Home
	public static File getWorkDirectoryHome(boolean autoCreate) {
		if (home == null) {
			String path = TemporaryWorkDirectory.class.getResource("/")
					.getFile();
			home = new File(path, "temporary");
		}
		return home;
	}

	// 新建一个命名的临时文件
	public File newFile(String name, String ext) {
		// 取得当前目录
		File tempDir = getCurrentDir();

		// 创建输出数据文件的临时文件
		try {
			File file = null;
			if (name == null) {
				ext = ext == null ? ".tmp" : ext;
				ext = ext.startsWith(".") ? ext : "." + ext;
				file = File.createTempFile("tf_", ext, tempDir);
			} else {
				if (ext != null) {
					ext = ext.startsWith(".") ? ext : "." + ext;
					name = name + ext;
				}
				file = new File(tempDir, name);
			}
			return file;
		} catch (IOException e) {
			log.error("newFile.error：" + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("", e);
			}
			return null;
		}
	}

	// 取得当前目录
	private File getCurrentDir() {
		if (dir == null) {
			// 创建临时目录
			dir = getWorkDirectoryHome(true);
			if (tag != null) {
				dir = new File(dir, tag);
				if (!dir.exists()) {
					if (dir.mkdirs()) {
						log.debug("WorkDirectory init OK~:"
								+ dir.getAbsolutePath());
					} else {
						log.error("dir.mkdirs() is False.");
					}
				}
			} else {
				String msg = "create WorkDirectory error:tag is null.";
				log.error(msg);
				return null;
			}
		}
		return dir;
	}

	// 通过名称从工作目录取得文件
	public File getFileByName(String name) {
		File file = new File(getCurrentDir(), name);
		if (file.exists()) {
			return file;
		} else {
			log.error("文件：" + name + "，不存在.");
			return null;
		}
	}

	// 初始化目录
	public void initWorkDirectory() {
		getCurrentDir();
	}

	public void cleanWorkDirectoryByTag(String tag) {
		if (dir != null) {
			try {
				FileUtils.deleteDirectory(dir);
				String msg = "current WorkDirectory clean OK~~:";
				msg += dir.getAbsolutePath();
				log.debug(msg);
			} catch (IOException e) {
				String msg = "current clean WorkDirectory.error:";
				msg += e.getMessage();
				log.debug(msg);
			}
		}
	}

	// 清理临时目录
	// public void cleanWorkDirectory() {
	// if (home != null) {
	// try {
	// FileUtils.deleteDirectory(home);
	// log.debug("WorkDirectory clean OK~~:" + home.getAbsolutePath());
	// } catch (IOException e) {
	// log.debug("cleanWorkDirectory.error:" + e.getMessage());
	// }
	// }
	// }
}
