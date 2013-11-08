package cn.com.qimingx.dbe;

import java.sql.Types;

import net.sf.json.JSON;
import cn.com.qimingx.json.MyJSONUtils;
import cn.com.qimingx.utils.ExtTypeInfo;
import cn.com.qimingx.utils.SQLTypeUtils;

/**
 * @author inc062805
 * 
 * 字段数据类型定义
 */
public class FieldDataType {
	public static void main(String[] args) {
		FieldDataType fdt = new FieldDataType("SMALLINT");
		System.out.println(fdt.toJSON());
		fdt = new FieldDataType("INTEGER");
		System.out.println(fdt.toJSON());
		fdt = new FieldDataType("NUMERIC");
		System.out.println(fdt.toJSON());
		fdt = new FieldDataType("TimeStamp");
		System.out.println(fdt.toJSON());
	}

	private String typeName;// 类型名称
	private int typeIndex;// JDBC类型代码
	private ExtTypeInfo extType;//
	private boolean resetLength = true;// 是否可重设长度
	private boolean resetScale = false;// 是否可设置精度

	// 构建器
	public FieldDataType(String type) {
		this.typeName = type;
		typeIndex = SQLTypeUtils.getJdbcType(type);
		if (typeIndex != 0) {
			extType = new ExtTypeInfo(typeIndex);
			if (extType.isDateType()) {
				resetLength = false;
			} else if (extType.isNumberType()) {
				// 数值型不能设置长度，除非是NUMERIC、DECIMAL
				resetLength = typeIndex == Types.DECIMAL
						|| typeIndex == Types.NUMERIC;
			} else {
				resetLength = true;
			}

			// 是 实数型 数据时 才能设置精度
			if (extType.isNumberType()) {
				resetScale = typeIndex == Types.DECIMAL
						|| typeIndex == Types.NUMERIC;
			} else {
				resetScale = false;
			}
		}
	}

	// 构建器
	public FieldDataType(String type, boolean resetLength) {
		this(type);
		this.setResetLength(resetLength);
	}

	public JSON toJSON() {
		JSON json = MyJSONUtils.toJsonExclude(this);
		return json;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setResetLength(boolean resetLength) {
		this.resetLength = resetLength;
	}

	public void setResetScale(boolean resetScale) {
		this.resetScale = resetScale;
	}

	public boolean isResetLength() {
		return resetLength;
	}

	public boolean isResetScale() {
		return resetScale;
	}
}
