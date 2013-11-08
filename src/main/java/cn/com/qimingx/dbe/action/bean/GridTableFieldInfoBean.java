package cn.com.qimingx.dbe.action.bean;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import cn.com.qimingx.core.WebParamBean;

/**
 * @author inc062805
 * 
 * 描述 表格中 某条记录的某个字段 的定位信息
 */
public class GridTableFieldInfoBean extends WebParamBean {
	// 表名称
	private String tablename;

	// 主键信息列表
	// private PkColumnObject pk;
	private List<PkColumnObject> pkList;

	// 主键信息 json字符串
	private String pkInfo;

	// 字段名称
	private String field;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public List<PkColumnObject> getPkList() {
		if (pkList == null) {
			JSONArray jpks = JSONArray.fromObject(pkInfo);
			pkList = new ArrayList<PkColumnObject>(jpks.size());
			for (int i = 0; i < jpks.size(); i++) {
				JSONObject jpk = jpks.getJSONObject(i);
				Object o = JSONObject.toBean(jpk, PkColumnObject.class);
				PkColumnObject pko = (PkColumnObject) o;
				pkList.add(pko);
			}
		}
		return pkList;
	}

	public void setPkList(List<PkColumnObject> pkList) {
		this.pkList = pkList;
	}

	public String getPkInfo() {
		return pkInfo;
	}

	public void setPkInfo(String pkInfo) {
		this.pkInfo = pkInfo;
	}
}
