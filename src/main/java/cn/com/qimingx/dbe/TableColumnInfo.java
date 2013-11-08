package cn.com.qimingx.dbe;

import cn.com.qimingx.utils.ExtTypeInfo;
import cn.com.qimingx.utils.SQLTypeUtils;

/**
 * @author inc062805
 * 
 * 描述Table的列信息
 */
public class TableColumnInfo {
	// 外键列信息
	public static class FKColumnInfo {
		private String table;
		private String column;

		public FKColumnInfo(String table, String column) {
			this.table = table;
			this.column = column;
		}

		public String getTable() {
			return table;
		}

		public void setTable(String table) {
			this.table = table;
		}

		public String getColumn() {
			return column;
		}

		public void setColumn(String column) {
			this.column = column;
		}
	}

	// 列名称
	private String name;
	// 列类型
	private int type;
	// sql 类型名称
	private String typeName;
	// 列大小
	private int size;
	// 小数位数
	private int digits;
	// 列是否可以为kong
	private boolean nullable;
	// 列默认值
	private String defaultValue;
	// 对应 ext 的相关信息
	private ExtTypeInfo extType;
	// 是否是主键列
	private boolean pkColumn = false;
	// 是否外键列
	private boolean fkColumn = false;
	// 外键列信息
	private FKColumnInfo fkInfo = null;
	// 列备注信息
	private String comment;

	public TableColumnInfo() {
	}

	public TableColumnInfo(String name, int type, int size, boolean nullable) {
		this.name = name;
		this.type = type;
		this.typeName = SQLTypeUtils.getJdbcTypeName(type);
		this.size = size;
		this.nullable = nullable;
		extType = new ExtTypeInfo(type);
	}	

	public boolean isPkColumn() {
		return pkColumn;
	}

	public void setPkColumn(boolean pkColumn) {
		this.pkColumn = pkColumn;
	}

	public boolean isFkColumn() {
		return fkColumn;
	}

	public void setFkColumn(boolean fkColumn) {
		this.fkColumn = fkColumn;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.typeName = SQLTypeUtils.getJdbcTypeName(type);
		this.extType = new ExtTypeInfo(type);		
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setExtType(ExtTypeInfo extType) {
		this.extType = extType;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public FKColumnInfo getFkInfo() {
		return fkInfo;
	}

	public void setFkInfo(FKColumnInfo fkInfo) {
		this.fkInfo = fkInfo;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getDigits() {
		return digits;
	}

	public void setDigits(int digits) {
		this.digits = digits;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ExtTypeInfo getExtType() {
		if (extType == null) {
			extType = new ExtTypeInfo(type);
		}
		return extType;
	}	
}
