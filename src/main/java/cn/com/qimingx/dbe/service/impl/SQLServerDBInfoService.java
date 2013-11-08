package cn.com.qimingx.dbe.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.com.qimingx.dbe.FieldDataType;

public class SQLServerDBInfoService extends AbstractDBInfoService {

	public String getLimitSQLString(String originalSQL) {
		return originalSQL;
	}

	// 返回用于分页的SQL语句
	public boolean supportLimit() {
		return false;
	}

	// 重写获取数据类型方法，返回sqlserver特有的数据类型
	public List<FieldDataType> getDataTypes() {
		List<FieldDataType> fdts = new ArrayList<FieldDataType>();
		fdts = super.getDataTypes();
//		for (int i = 0; i < fdts.size(); i++) {
//			FieldDataType fdt = fdts.get(i);
//			if (fdt != null && "DATE".equalsIgnoreCase(fdt.getTypeName())) {
//				fdts.remove(fdt);
//				i--;
//			}
//			if (fdt != null && "CLOB".equalsIgnoreCase(fdt.getTypeName())) {
//				fdts.remove(fdt);
//				i--;
//			}
//			if (fdt != null && "BLOB".equalsIgnoreCase(fdt.getTypeName())) {
//				fdts.remove(fdt);
//				i--;
//			}
//		}
		Iterator<FieldDataType> it = fdts.iterator();
		while (it.hasNext()) {
			FieldDataType fdt = it.next();
			if ("DATE".equalsIgnoreCase(fdt.getTypeName())
					|| "CLOB".equalsIgnoreCase(fdt.getTypeName())
					|| "BLOB".equalsIgnoreCase(fdt.getTypeName())) {
				it.remove();
				fdts.remove(fdt);
			}
		}
		
		fdts.add(new FieldDataType("DATETIME",false));
		fdts.add(new FieldDataType("IMAGE",false));
		fdts.add(new FieldDataType("TEXT",false));
		return fdts;
	}
}
