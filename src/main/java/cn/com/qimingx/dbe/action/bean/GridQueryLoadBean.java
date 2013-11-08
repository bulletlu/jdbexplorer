package cn.com.qimingx.dbe.action.bean;

/**
 * @author inc062805
 * 
 * 描述Grid Query load
 */
public class GridQueryLoadBean extends GridTableLoadBean {
	private String sql;

	public String getSql() {
		if (sql != null && sql.endsWith(";")) {
			sql = sql.substring(0, sql.length() - 1);
		}
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}
