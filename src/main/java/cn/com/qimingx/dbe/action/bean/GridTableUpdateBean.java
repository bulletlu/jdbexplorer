package cn.com.qimingx.dbe.action.bean;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * @author inc062805
 * 
 * Ext Tree Loader 的参数Bean
 */
public class GridTableUpdateBean extends TreeNodeBean {
	// json Data
	private String data;
	private GridTableUpdateInfoBean tableUpdate;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public GridTableUpdateInfoBean getTableUpdate() {
		if (tableUpdate == null) {
			JSONObject json = JSONObject.fromObject(data);
			Map<String, Class<?>> map = new HashMap<String, Class<?>>(1);
			map.put("pkList", PkColumnObject.class);
			Class<GridTableUpdateInfoBean> cls = GridTableUpdateInfoBean.class;
			Object obj = JSONObject.toBean(json, cls, map);
			return (GridTableUpdateInfoBean) obj;
		}
		return tableUpdate;
	}

	public void setTableUpdate(GridTableUpdateInfoBean tableUpdate) {
		this.tableUpdate = tableUpdate;
	}
}
