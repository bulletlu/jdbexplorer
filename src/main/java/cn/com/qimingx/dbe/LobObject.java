package cn.com.qimingx.dbe;

import java.io.File;

import cn.com.qimingx.utils.ImageUtils;
import cn.com.qimingx.utils.SQLTypeUtils;

/**
 * @author inc062805
 * 
 * 描述一个大二进制对象
 */
public class LobObject {
	// 类型，CLOB or BLOB
	private int type;
	// 内容值
	private File value;

	public LobObject(int type) {
		this.type = type;
	}

	public boolean isNull() {
		return value == null;
	}

	public boolean isBLOB() {
		return SQLTypeUtils.isBlobType(type);
	}

	public boolean isCLOB() {
		return SQLTypeUtils.isClobType(type);
	}

	//
	public boolean isImage() {
		if (isBLOB()) {
			return ImageUtils.isImage(value);
		} else {
			return false;
		}
	}

	public String getTypeName() {
		return SQLTypeUtils.getJdbcTypeName(type);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public File getValue() {
		return value;
	}

	public void setValue(File value) {
		this.value = value;
	}
}
