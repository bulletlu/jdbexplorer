package cn.com.qimingx.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author inc062805
 * 
 * 常用工具箱..
 */
public class MyUtils {
	// Logger..
	private static final transient Log log = LogFactory.getLog(MyUtils.class);

	public static void main(String[] args) {
	}

	// 判断字符串是否 是/包含 html内容..
	public static boolean isHTMLContent(String content) {
		String[] tags = new String[] { "<html>", "<head>", "<title>",
				"<script", "<meta", "<link", "<body", "<table", "<tr", "<td",
				"<form", "<img", "<input", "<frame", "<iframe", "<frameset",
				"<hr", "<dd", "<dl", "<dt", "<dir", "<ll", "<li", "<ul","<span" };

		content = content.toLowerCase();
		for (String tag : tags) {
			if (content.contains(tag)) {
				log.debug("[" + content + "] isHTML=" + true);
				return true;
			}
		}
		return false;
	}

	/**
	 * 打印对象的详细信息
	 */
	public static String printObject(Object o) {
		String s = ToStringBuilder.reflectionToString(o,
				ToStringStyle.MULTI_LINE_STYLE);
		return s;
	}

	/**
	 * 用当前系统的charset获取字符串的byte[]，并用utf8重新编码；
	 * 
	 * @param str
	 * @return
	 */
	public static String toUTF8(final String str) {
		// 取得当前OS的 encoding
		String encoding = System.getProperty("file.encoding", "GBK");
		return resetCharset(str, encoding, "UTF-8");
	}

	/**
	 * 对指定字符串 按新字符集进行编码
	 * 
	 * @param string
	 * @param srcCharset
	 * @param destCharset
	 * @return
	 */
	public static String resetCharset(String string, String srcCharset,
			String destCharset) {
		String result = null;
		try {
			byte[] bytes = string.getBytes(srcCharset);
			result = new String(bytes, destCharset);
		} catch (UnsupportedEncodingException e) {
			log.error("执行编码出错：" + e.getLocalizedMessage());
			return string;
		}
		log.debug(srcCharset + " --> " + destCharset + ":[" + string + " --> "
				+ result + "]");
		return result;
	}

	public static String urlEncode(String url) {
		try {
			url = URLEncoder.encode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			log.error("urlEncod出错：" + e.getMessage());
		}
		return url;
	}

	/**
	 * 
	 * @param clsName
	 * @return
	 */
	public static Object newObjectOfClassName(String clsName) {
		try {
			return Class.forName(clsName).newInstance();
		} catch (Exception e) {
			log.error("newObjectOfClassName出错：" + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	//
	public static Date parseDate(String date, String... formats) {
		if (formats == null || formats.length == 0) {
			formats = new String[] { "yyyy-MM-dd" };
		}
		try {
			return DateUtils.parseDate(date, formats);
		} catch (ParseException e) {
			String format = Arrays.toString(formats);
			log.error("parseDate出错：" + date + "[" + format);
			return null;
		}
	}
}
