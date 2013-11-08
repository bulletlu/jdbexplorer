package cn.com.qimingx.dbe.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.com.qimingx.dbe.FieldDataType;

public class MysqlDBInfoService extends AbstractDBInfoService {

	public String getLimitSQLString(String originalSQL) {
		return originalSQL;
	}

	public boolean supportLimit() {
		return false;
	}

	// 重写获取数据类型方法，返回myssql特有的数据类型
	public List<FieldDataType> getDataTypes() {
		List<FieldDataType> fdts = new ArrayList<FieldDataType>();
		fdts = super.getDataTypes();
		Iterator<FieldDataType> it = fdts.iterator();
		while (it.hasNext()) {
			FieldDataType fdt = it.next();
			if ("CLOB".equalsIgnoreCase(fdt.getTypeName())) {
				it.remove();
				fdts.remove(fdt);
			}
			if ("TIMESTAMP".equalsIgnoreCase(fdt.getTypeName())
					|| "INTEGER".equalsIgnoreCase(fdt.getTypeName())
					|| "SMALLINT".equalsIgnoreCase(fdt.getTypeName())
					|| "BLOB".equalsIgnoreCase(fdt.getTypeName())) {
				fdt.setResetLength(true);
			}
			
			if("FLOAT".equalsIgnoreCase(fdt.getTypeName())){
				fdt.setResetLength(true);
				fdt.setResetScale(true);
			}
		}
		fdts.add(new FieldDataType("TEXT", true));
		return fdts;
	}
}
