package cn.com.qimingx.utils;

import java.sql.Types;


/**
 * @author inc063212
 * 
 * Extjs 的类型信息， 实现了从JDBC SQLType 向 Extjs Type 转换的逻辑
 */
public class ExtTypeInfo {
	// 各种日期类型数据的显示格式定义
	private static final String FORMAT_DATA_TIME = "yyyy-MM-dd HH:mm";
	private static final String FORMAT_DATA_TIME_EXT = "Y-m-d H:i";
	private static final String FORMAT_DATA = "yyyy-MM-dd";
	private static final String FORMAT_DATA_EXT = "Y-m-d";
	private static final String FORMAT_TIME = "HH:mm";
	private static final String FORMAT_TIME_EXT = "H:i";

	// extjs 类型名称
	private String type;
	// JDBC SQLType类型
	private int jdbcType;
	// 数据显示格式定义(目前主要考虑使用在日期类型、数值型的数据)
	private String format;

	// 构建器
	public ExtTypeInfo(int jdbcType) {
		this.jdbcType = jdbcType;

		switch (jdbcType) {
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.BIT:
			setType("int");
			break;
		case Types.BOOLEAN:
			setType("boolean");
			break;
		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.DECIMAL:
		case Types.NUMERIC:
		case Types.REAL:
			setType("float");
			break;
		case Types.DATE:
			setFormat(FORMAT_DATA);
			setType("date");
			break;
		case Types.TIME:
			setFormat(FORMAT_TIME);
			setType("date");
			break;
		case Types.TIMESTAMP:
			setFormat(FORMAT_DATA_TIME);
			setType("date");
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.BLOB:
		case Types.CLOB:
			setType("string");
			break;
		default:
			setType("auto");
		}
	}

	public boolean isNumberType() {
		return SQLTypeUtils.isNumberType(jdbcType);
	}

	public boolean isDateType() {
		return SQLTypeUtils.isDateType(jdbcType);
	}

	public boolean isLongType() {
		return SQLTypeUtils.isLongType(jdbcType);
	}

	public boolean isBooleanType() {
		return jdbcType == Types.BOOLEAN;
	}

	// 是否可以排序
	public boolean isSortable() {
		// 数值、日期、Boolean三种类型可以排序
		boolean sortable = type.equalsIgnoreCase("int");
		sortable = sortable || type.equalsIgnoreCase("float");
		sortable = sortable || type.equalsIgnoreCase("boolean");
		sortable = sortable || type.equalsIgnoreCase("date");

		return sortable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}

	// 返回ext 类型的日期格式
	public String getDateFormat() {
		switch (jdbcType) {
		case Types.DATE:
			return FORMAT_DATA_EXT;
		case Types.TIME:
			return FORMAT_TIME_EXT;
		case Types.TIMESTAMP:
			return FORMAT_DATA_TIME_EXT;
		default:
			return null;
		}
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
