package cn.com.qimingx.dbe.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.com.qimingx.dbe.FieldDataType;

public class DerbyDBInfoService extends AbstractDBInfoService{

	public String getLimitSQLString(String originalSQL) {
		return null;
	}

	public boolean supportLimit() {
		return false;
	}
	
	// 重写获取数据类型方法，返回derby特有的数据类型
	public List<FieldDataType> getDataTypes() {
		List<FieldDataType> fdts = new ArrayList<FieldDataType>();
		fdts = super.getDataTypes();
		Iterator<FieldDataType> it = fdts.iterator();
		while (it.hasNext()) {
			FieldDataType fdt = it.next();
			if ("FLOAT".equalsIgnoreCase(fdt.getTypeName())
					|| "CLOB".equalsIgnoreCase(fdt.getTypeName())
					|| "BLOB".equalsIgnoreCase(fdt.getTypeName())) {
				fdt.setResetLength(true);
			}
		}
		return fdts;
	}

}
