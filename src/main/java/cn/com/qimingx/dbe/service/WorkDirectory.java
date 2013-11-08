package cn.com.qimingx.dbe.service;

import java.io.File;

/**
 * @author inc062805
 * 
 * 工作目录描述接口
 */
public interface WorkDirectory {
	// 可以对工作目录中的临时文件 进行分类，这里可以设置分类名称
	void setTagName(String tag);

	// 新建一个临时文件
	File newFile(String name, String ext);

	// 通过文件名称 取得文件
	File getFileByName(String name);

	// 初始工作目录
	void initWorkDirectory();

	void cleanWorkDirectoryByTag(String tag);

	// 清理工作目录
	//void cleanWorkDirectory();
}