package cn.com.qimingx.spring;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author inc062805
 * 
 * 
 */
public class CustomJsonDateEditor extends PropertyEditorSupport {
	// 尝试进行日期解析的 模式
	private static final String[] patterns = new String[] { "yyyy-MM-dd HH:mm",
			"yyyy-MM-dd", "HH:mm" };

	// logger
	private static final Log log = LogFactory
			.getLog(CustomJsonDateEditor.class);

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		try {
			Date date = DateUtils.parseDate(text, patterns);
			setValue(date);
		} catch (ParseException e) {
			log.error("解析日期数据出错:text:" + text + "error:" + e.getMessage());
			// setValue(value);
		}
	}

}
