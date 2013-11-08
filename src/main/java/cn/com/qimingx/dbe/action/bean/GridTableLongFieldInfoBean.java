package cn.com.qimingx.dbe.action.bean;

import java.util.List;

import cn.com.qimingx.spring.UploadFile;

/**
 * @author inc062805
 * 
 * 更新BLOB字段
 */
public class GridTableLongFieldInfoBean extends UploadFile {
	private GridTableFieldInfoBean bean = new GridTableFieldInfoBean();

	public String getField() {
		return bean.getField();
	}

	public String getTablename() {
		return bean.getTablename();
	}

	public void setField(String field) {
		bean.setField(field);
	}

	public void setTablename(String tablename) {
		bean.setTablename(tablename);
	}
	
	public List<PkColumnObject> getPkList(){
		return bean.getPkList();
	}

	public String getPkInfo() {
		return bean.getPkInfo();
	}

	public void setPkInfo(String pkInfo) {
		bean.setPkInfo(pkInfo);
	}
	
	
}
