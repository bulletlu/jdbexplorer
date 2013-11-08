package cn.com.qimingx.dbe.action.bean;

import cn.com.qimingx.core.WebParamBean;
import cn.com.qimingx.utils.SQLTypeUtils;

/**
 * @author inc062805
 * 
 * 主键列对象
 */
public class PkColumnObject extends WebParamBean {
	// 主键名称列表
	private String pk;
	// 主键值列表
	private String pkValue;
	// 主键值类型
	private int pkType;

	// 取得 对象类型的 主键值
	public Object getPkValueObject() {
		Object value = SQLTypeUtils.getSQLValueObject(pkType, pkValue, null);
		return value;
	}

	public String getPk() {
		return pk;
	}

	public void setPk(String pk) {
		this.pk = pk;
	}

	public String getPkValue() {
		return pkValue;
	}

	public void setPkValue(String pkValue) {
		this.pkValue = pkValue;
	}

	public int getPkType() {
		return pkType;
	}

	public void setPkType(int pkType) {
		this.pkType = pkType;
	}
}
