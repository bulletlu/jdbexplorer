package cn.com.qimingx.utils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author inc062805
 * 
 */
public class SQLUtils {
	public static void main(String[] args) {
		String sql = "select * from users where id=3 order by id,name desc";
		String cond = "aa=b and c=d";
		System.out.println(appendCondition(sql, cond));
	}

	// Logger
	private static final Log log = LogFactory.getLog(SQLUtils.class);

	// 取得 from 位置的表达式
	private static final Pattern FROM_PATTERN = Pattern.compile(
			"(^|\\s)(from)\\s", Pattern.CASE_INSENSITIVE);

	// 取得 order 位置的表达式
	private static final Pattern ORDER_PATTERN = Pattern.compile(
			"\\s(order)(\\s)+by\\s", Pattern.CASE_INSENSITIVE);

	public static Map<String, String> getDBInfos(DatabaseMetaData dbmd) {
		Map<String, String> infos = new LinkedHashMap<String, String>();
		try {
			// conn Infos
			String value = dbmd.getDatabaseProductName();
			value += "[" + dbmd.getDatabaseMajorVersion();
			value += "." + dbmd.getDatabaseMinorVersion() + "]";
			infos.put("数据库名称和版本", value);

			value = dbmd.getDriverName();
			value += "[" + dbmd.getDriverMajorVersion();
			value += "." + dbmd.getDriverMinorVersion() + "]";
			infos.put("JDBC驱动名称和版本", value);

			value = dbmd.getUserName();
			value += " connect to " + dbmd.getURL();
			infos.put("当前链接信息", value);

			int iValue = dbmd.getJDBCMajorVersion();
			value = String.valueOf(iValue);
			iValue = dbmd.getJDBCMinorVersion();
			value += "." + iValue;
			infos.put("JDBC规范版本", value);

			// 事务
			boolean support = dbmd.supportsTransactions();
			infos.put("是否支持事务", String.valueOf(support));
			// TODO：检查事务支持级别
			support = dbmd.supportsStoredProcedures();
			infos.put("是否支持使用存储过程", String.valueOf(support));
			// others
			support = dbmd.supportsStatementPooling();
			infos.put("是否支持Statement Pooling", String.valueOf(support));
			support = dbmd.supportsBatchUpdates();
			infos.put("是否支持批量更新", String.valueOf(support));
			support = dbmd.supportsGetGeneratedKeys();
			infos.put("是否支持执行语句后检索自动生成的键", String.valueOf(support));
			support = dbmd.supportsConvert();
			infos.put("是否支持SQL类型之间的转换(CONVERT)", String.valueOf(support));

			// SQL
			value = "高级";
			support = dbmd.supportsANSI92FullSQL();
			if (!support) {
				value = "中级";
				support = dbmd.supportsANSI92IntermediateSQL();
				if (!support) {
					value = "初级";
				}
			}
			infos.put("支持ANSI92 QL语法的级别", value);

			support = dbmd.supportsSubqueriesInComparisons();
			infos.put("是否支持比较表达式中的子查询", String.valueOf(support));
			support = dbmd.supportsSubqueriesInExists();
			infos.put("是否支持 EXISTS 表达式中的子查询", String.valueOf(support));
			support = dbmd.supportsSubqueriesInIns();
			infos.put("是否支持 IN 语句中的子查询", String.valueOf(support));
			support = dbmd.supportsCorrelatedSubqueries();
			infos.put("是否支持相关子查询", String.valueOf(support));
			support = dbmd.supportsSubqueriesInQuantifieds();
			infos.put("是否支持量化表达式 (quantified expression) 中的子查询", String
					.valueOf(support));

			support = dbmd.supportsSelectForUpdate();
			infos.put("是否支持 SELECT FOR UPDATE", String.valueOf(support));
			support = dbmd.supportsUnion();
			infos.put("否支持 UNION", String.valueOf(support));
			support = dbmd.supportsUnionAll();
			infos.put("是否支持 UNION ALL", String.valueOf(support));
			support = dbmd.supportsGroupBy();
			infos.put("是否支持 GROUP BY", String.valueOf(support));
			support = dbmd.supportsOuterJoins();
			infos.put("是否支持的外连接", String.valueOf(support));
			support = dbmd.supportsLimitedOuterJoins();
			infos.put("是否为外连接提供受限制的支持", String.valueOf(support));
			support = dbmd.supportsFullOuterJoins();
			infos.put("是否支持完全嵌套的外连接", String.valueOf(support));
			iValue = dbmd.getMaxStatementLength();
			infos.put("允许在 SQL 语句中使用的最大字符数", String.valueOf(iValue));

			value = dbmd.getSQLKeywords();
			infos.put("非SQL92标准的关键字", value);
			value = dbmd.getSystemFunctions();
			infos.put("可用的系统函数", value);
			value = dbmd.getNumericFunctions();
			infos.put("可用于数值类型的数学函数", value);
			value = dbmd.getStringFunctions();
			infos.put("可用于字符串类型的函数", value);
			value = dbmd.getTimeDateFunctions();
			infos.put("可用于时间和日期类型的函数", value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return infos;
	}

	/**
	 * 从查询 sql 语句生成 计算记录总数的count sql语句
	 */
	public static String getCountSQL(String sql) {
		Matcher fMatcher = FROM_PATTERN.matcher(sql);
		if (!fMatcher.find()) {
			throw new IllegalArgumentException("no from clause found in query");
		}
		int fLoc = fMatcher.start(2);

		Matcher oMatcher = ORDER_PATTERN.matcher(sql);
		int oLoc = oMatcher.find() ? oMatcher.start(1) : sql.length();

		return "select count(*) " + sql.substring(fLoc, oLoc);
	}

	/**
	 * 通过指定的sql语句，统计目标结果集的行数
	 * 
	 * @param sql
	 * @return
	 */
	public static long totalRowsBySQL(String sql, Statement stat) {
		long total = 0;
		String countSQL = SQLUtils.getCountSQL(sql);
		log.debug("count rows total:" + countSQL);
		try {
			ResultSet rs = stat.executeQuery(countSQL);
			if (rs.next()) {
				total = rs.getInt(1);
			} else {
				log.error("计算记录总数出错~~,total=0");
			}
			rs.close();
		} catch (SQLException e) {
			log.error("计算记录总数出错~~,total=0");
		}
		return total;
	}

	// 
	/**
	 * 向sql语句附件新的条件，如果已有where语句 则添加到后面，如果没有添加where语句
	 * 
	 * @param sql
	 * @param condition
	 * @return
	 */
	public static String appendCondition(final String sql,
			final String condition) {
		// check params
		if (condition == null || condition.length() == 0) {
			return sql;
		}

		// 取得order by 的位置
		Matcher oMatcher = ORDER_PATTERN.matcher(sql);
		int oLoc = oMatcher.find() ? oMatcher.start(1) : sql.length();

		StringBuilder str = new StringBuilder(sql.toLowerCase());
		String cond = null;
		if (str.lastIndexOf("where") == -1) {
			cond = "where (" + condition + ") ";
		} else {
			cond = "and (" + condition + ") ";
		}
		if (oLoc == sql.length()) {
			cond = " " + cond;
		}
		str.insert(oLoc, cond);
		return str.toString();
	}
}
