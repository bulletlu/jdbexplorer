package cn.com.qimingx.dbe;

import java.util.List;
import java.util.Map;

/**
 * @author inc062805
 * 
 * 表格数据描述对象
 */
public class TableDataInfo {
	public static void main(String[] args) {
		try {
			// new TableDataInfo(0l, null).makePDFContent(null);
			System.getProperties().list(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private long total;
	private List<Map<String, Object>> rows;

	public TableDataInfo(long total, List<Map<String, Object>> rows) {
		this.total = total;
		this.rows = rows;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<Map<String, Object>> getRows() {
		return rows;
	}

	public void setRows(List<Map<String, Object>> rows) {
		this.rows = rows;
	}
}
