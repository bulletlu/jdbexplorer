package cn.com.qimingx.dbe.action.bean;

import cn.com.qimingx.utils.SQLTypeUtils;

/**
 * @author inc062805
 * 
 * 描述对表格中 某条记录 某个字段的 更新信息
 */
public class GridTableUpdateInfoBean extends GridTableFieldInfoBean {
	// 字段值
	private String value;
	// 字段类型
	private int type;
	// 字段格式
	private String format;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public UpdateValue getUpdateValue() {
		return new UpdateValue(value, type, format);
	}

	/**
	 * 描述更新Table record 的值对象
	 */
	public static class UpdateValue {
		private Object value;
		private int type;

		private UpdateValue(String str, int type, String param) {
			this.type = type;
			value = SQLTypeUtils.getSQLValueObject(type, str, param);
		}

		public String getTypeName() {
			return SQLTypeUtils.getJdbcTypeName(getType());
		}

		public int getType() {
			return type;
		}

		public Object getValue() {
			return value;
		}
	}
}
