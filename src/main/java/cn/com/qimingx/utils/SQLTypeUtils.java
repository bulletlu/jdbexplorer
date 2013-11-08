package cn.com.qimingx.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author inc062805
 * 
 * JDBC SQLType 相关工具
 */
public class SQLTypeUtils {
	// logger
	private static final Log log = LogFactory.getLog(SQLTypeUtils.class);

	// 通过JDBC SQLType代码 创建 ExtTypeInfo 对象
	public static ExtTypeInfo jdbcType2ExtType(int jdbcType) {
		ExtTypeInfo extType = new ExtTypeInfo(jdbcType);
		return extType;
	}

	// 通过JDBC SQLType的类型名称 取得 类型代码
	public static int getJdbcType(String jdbcTypeName) {
		int type = 0;
		if ("ARRAY".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.ARRAY;
		} else if ("BIGINT".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.BIGINT;
		} else if ("BINARY".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.BINARY;
		} else if ("BIT".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.BIT;
		} else if ("BLOB".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.BLOB;
		} else if ("BOOLEAN".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.BOOLEAN;
		} else if ("CHAR".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.CHAR;
		} else if ("CLOB".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.CLOB;
		} else if ("DATE".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.DATE;
		} else if ("DATALINK".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.DATALINK;
		} else if ("DECIMAL".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.DECIMAL;
		} else if ("DISTINCT".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.DISTINCT;
		} else if ("DOUBLE".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.DOUBLE;
		} else if ("FLOAT".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.FLOAT;
		} else if ("INTEGER".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.INTEGER;
		} else if ("JAVA_OBJECT".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.JAVA_OBJECT;
		} else if ("LONGVARBINARY".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.LONGVARBINARY;
		} else if ("LONGVARCHAR".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.LONGVARCHAR;
		} else if ("NULL".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.NULL;
		} else if ("NUMERIC".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.NUMERIC;
		} else if ("OTHER".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.OTHER;
		} else if ("REAL".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.REAL;
		} else if ("REF".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.REF;
		} else if ("SMALLINT".equals(jdbcTypeName)) {
			type = Types.SMALLINT;
		} else if ("STRUCT".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.STRUCT;
		} else if ("TIME".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.TIME;
		} else if ("TIMESTAMP".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.TIMESTAMP;
		} else if ("TINYINT".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.TINYINT;
		} else if ("VARBINARY".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.VARBINARY;
		} else if ("VARCHAR".equalsIgnoreCase(jdbcTypeName)) {
			type = Types.VARCHAR;
			// 以下为JDK1.6中才添加的内容..
			// }
			// else if ("LONGNVARCHAR".equalsIgnoreCase(jdbcTypeName)) {
			// type = Types.LONGNVARCHAR;
			// } else if ("NCHAR".equalsIgnoreCase(jdbcTypeName)) {
			// type = Types.NCHAR;
			// } else if ("NCLOB".equalsIgnoreCase(jdbcTypeName)) {
			// type = Types.NCLOB;
			// } else if ("ROWID".equalsIgnoreCase(jdbcTypeName)) {
			// type = Types.ROWID;
			// } else if ("SQLXML".equalsIgnoreCase(jdbcTypeName)) {
			// type = Types.SQLXML;
		}
		//else {
		//	throw new RuntimeException("jdbcTypeName 无效~");
		//}
		return type;
	}

	// 是否是数值类型..
	public static boolean isNumberType(int jdbcType) {
		boolean result = false;
		switch (jdbcType) {
		case Types.TINYINT:
		case Types.SMALLINT:
		case Types.BIGINT:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.INTEGER:
		case Types.NUMERIC:
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	// 是否是日期类型
	public static boolean isDateType(int jdbcType) {
		boolean result = false;
		switch (jdbcType) {
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	// 是否是CLOB类型
	public static boolean isClobType(int jdbcType) {
		boolean result = false;
		switch (jdbcType) {
		case Types.LONGVARCHAR:
			// case Types.LONGNVARCHAR:
		case Types.CLOB:
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	// 是否是BLOB类型
	public static boolean isBlobType(int jdbcType) {
		boolean result = false;
		switch (jdbcType) {
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
		case Types.BLOB:
			// case Types.LONGNVARCHAR:
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	// 是否是字符类型的
	public static boolean isStringType(int jdbcType) {
		boolean result = false;
		switch (jdbcType) {
		case Types.CHAR:
		case Types.VARCHAR:
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	// 是否是不能够直接显示的类型
	public static boolean isLongType(int jdbcType) {
		boolean result = false;
		// switch (jdbcType) {
		// case Types.LONGVARBINARY:
		// case Types.LONGVARCHAR:
		// case Types.BLOB:
		// case Types.CLOB:
		// result = true;
		// break;
		// default:
		// result = false;
		// }
		result = !isDateType(jdbcType) && !isStringType(jdbcType)
				&& !isNumberType(jdbcType) && jdbcType != Types.BOOLEAN;
		// String type = getJdbcTypeName(jdbcType);
		// log.debug(type + "----> isLongType " + result);
		return result;
	}

	// 通过JDBC SQLType代码，取得类型名称
	public static String getJdbcTypeName(int jdbcType) {
		String typeName = "";
		switch (jdbcType) {
		case Types.ARRAY:
			typeName = "ARRAY";
			break;
		case Types.BOOLEAN:
			typeName = "BOOLEAN";
			break;
		case Types.BIGINT:
			typeName = "BIGINT";
			break;
		case Types.BINARY:
			typeName = "BINARY";
			break;
		case Types.BIT:
			typeName = "BIT";
			break;
		case Types.BLOB:
			typeName = "BLOB";
			break;
		case Types.CHAR:
			typeName = "CHAR";
			break;
		case Types.CLOB:
			typeName = "CLOB";
			break;
		case Types.DATALINK:
			typeName = "DATALINK";
			break;
		case Types.DATE:
			typeName = "DATE";
			break;
		case Types.DECIMAL:
			typeName = "DECIMAL";
			break;
		case Types.DISTINCT:
			typeName = "DISTINCT";
			break;
		case Types.DOUBLE:
			typeName = "DOUBLE";
			break;
		case Types.FLOAT:
			typeName = "FLOAT";
			break;
		case Types.INTEGER:
			typeName = "INTEGER";
			break;
		case Types.JAVA_OBJECT:
			typeName = "JAVA_OBJECT";
			break;
		case Types.LONGVARBINARY:
			typeName = "LONGVARBINARY";
			break;
		case Types.LONGVARCHAR:
			typeName = "LONGVARCHAR";
			break;
		// 以下为JDK1.6才添加的内容
		// case Types.LONGNVARCHAR:
		// typeName = "LONGNVARCHAR";
		// break;
		// case Types.NCHAR:
		// typeName = "NCHAR";
		// break;
		// case Types.NCLOB:
		// typeName = "NCLOB";
		// break;
		// case Types.NVARCHAR:
		// typeName = "NCLOB";
		// break;
		// case Types.ROWID:
		// typeName = "ROWID";
		// break;
		// case Types.SQLXML:
		// typeName = "SQLXML";
		// break;
		case Types.NULL:
			typeName = "NULL";
			break;
		case Types.NUMERIC:
			typeName = "NUMERIC";
			break;
		case Types.OTHER:
			typeName = "OTHER";
			break;
		case Types.REAL:
			typeName = "REAL";
			break;
		case Types.REF:
			typeName = "REF";
			break;
		case Types.SMALLINT:
			typeName = "SMALLINT";
			break;
		case Types.STRUCT:
			typeName = "STRUCT";
			break;
		case Types.TIME:
			typeName = "TIME";
			break;
		case Types.TIMESTAMP:
			typeName = "TIMESTAMP";
			break;
		case Types.TINYINT:
			typeName = "TINYINT";
			break;
		case Types.VARBINARY:
			typeName = "VARBINARY";
			break;
		case Types.VARCHAR:
			typeName = "VARCHAR";
			break;
		}
		return typeName;
	}

	// 通过JDBC SQLType代码及字符类型的值，取得特定类型的对象值
	public static Object getSQLValueObject(int type, String value, String param) {
		Object objValue = null;
		switch (type) {
		case Types.VARCHAR:
			objValue = value;
			break;
		case Types.BOOLEAN:
			objValue = Boolean.valueOf(value);
			break;
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
			objValue = Integer.valueOf(value);
			break;
		case Types.FLOAT:
			objValue = Float.valueOf(value);
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
		case Types.DOUBLE:
			objValue = Double.valueOf(value);
			break;
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			Date date = MyUtils.parseDate(value, param);
			if (type == Types.DATE) {
				objValue = new java.sql.Date(date.getTime());
			} else if (type == Types.TIME) {
				objValue = new Time(date.getTime());
			} else {
				objValue = new Timestamp(date.getTime());
			}
			break;
		default:
			String msg = "无法处理的类型，" + getJdbcTypeName(type);
			log.error("getSQLValueObject出错：" + msg);
			throw new RuntimeException(msg);
		}
		return objValue;
	}
}
