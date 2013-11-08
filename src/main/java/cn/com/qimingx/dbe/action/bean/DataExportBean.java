package cn.com.qimingx.dbe.action.bean;

/**
 * @author inc062805
 * 
 * 数据导出的参数Bean
 */
public class DataExportBean extends GridQueryLoadBean {
	// 导出字段
	private String[] fields;
	// 导出范围类型
	private String rangeType;
	// 格式类型
	private String formatType;
	// 开始页码
	private int startPageNo = 0;
	// 结束页码
	private int endPageNo = 0;

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String getRangeType() {
		return rangeType;
	}

	public void setRangeType(String rangeType) {
		this.rangeType = rangeType;
	}

	public String getFormatType() {
		return formatType;
	}

	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}

	public int getStartPageNo() {
		return startPageNo;
	}

	public void setStartPageNo(int startPageNo) {
		this.startPageNo = startPageNo;
	}

	public int getEndPageNo() {
		return endPageNo;
	}

	public void setEndPageNo(int endPageNo) {
		this.endPageNo = endPageNo;
	}
}
