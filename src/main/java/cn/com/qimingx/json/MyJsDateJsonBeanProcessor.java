package cn.com.qimingx.json;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;

public class MyJsDateJsonBeanProcessor implements JsonBeanProcessor {
	public static String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm";

	public static String processDate(Date date) {
		return DateFormatUtils.format(date, DEFAULT_DATE_PATTERN);
	}

	private String datePattern = null;

	public String getDatePattern() {
		if (datePattern == null) {
			return DEFAULT_DATE_PATTERN;
		} else {
			return datePattern;
		}
	}

	public void resetDatePattern() {
		datePattern = null;
	}

	public JSONObject processBean(Object bean, JsonConfig jsonConfig) {
		if (bean instanceof java.sql.Date) {
			java.sql.Date d = (java.sql.Date) bean;

			long time = d.getTime();
			String pattern = getDatePattern();
			String date = DateFormatUtils.format(time, pattern);
			return makeJSONObject(date, time, pattern);
		}

		if (bean instanceof Date) {
			Date d = (Date) bean;

			long time = d.getTime();
			String pattern = getDatePattern();
			String date = DateFormatUtils.format(time, pattern);
			return makeJSONObject(date, time, pattern);
		}

		return new JSONObject(true);
	}

	private static JSONObject makeJSONObject(String date, long time,
			String pattern) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.element("date", date);
		jsonObject.element("time", time);
		jsonObject.element("pattern", pattern);
		return jsonObject;
	}
}