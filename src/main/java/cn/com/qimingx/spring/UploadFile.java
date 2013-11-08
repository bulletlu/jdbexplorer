package cn.com.qimingx.spring;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Wangwei
 * 
 * 上传文件的Bean定义
 */
public class UploadFile {
	private String name;// 文件名称
	private String type;// 文件扩展名
	private MultipartFile file;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
