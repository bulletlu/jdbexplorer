package cn.com.qimingx.spring;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * @author Wangwei
 * 
 * Action的基类，暂时为空
 */
public class BaseMultiActionController extends MultiActionController {
	// JSON 内容类型
	public static final String JSON_CONTENT_TYPE = "text/javascript;charset=UTF-8";
	// XML 内容类型
	public static final String XML_CONTENT_TYPE = "application/xml;charset=UTF-8";
	// HTML 内容类型
	public static final String HTML_CONTENT_TYPE = "text/html;charset=UTF-8";

	public static final String BINARY_CONTENT_TYPE = "application/x-download,charset=utf-8";

	// logger
	private static final Log log = LogFactory
			.getLog(BaseMultiActionController.class);

	@Autowired
	public void setMNResolver(MethodNameResolver methodNameResolver) {
		setMethodNameResolver(methodNameResolver);
	}

	// 输出JSON数据
	public void sendJSON(HttpServletResponse resp, CharSequence json) {
		resp.setContentType(HTML_CONTENT_TYPE);
		try {
			resp.getWriter().write(json.toString());
			log.debug("response output:" + json);
		} catch (IOException e) {
			log.error("export JSON 出错：" + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("", e);
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	// 输出错误JSON
	public void sendErrorJSON(HttpServletResponse resp, CharSequence json) {
		sendErrorJSON(resp, HttpServletResponse.SC_BAD_REQUEST, json);
	}

	// 输出错误JSON
	public void sendErrorJSON(HttpServletResponse resp, int errorID,
			CharSequence json) {
		resp.setStatus(errorID);
		sendJSON(resp, json);
	}

	// 抛出错误页面
	public void sendError(HttpServletResponse resp, int errorID,
			CharSequence errorMsg) {
		resp.setContentType(HTML_CONTENT_TYPE);
		try {
			resp.sendError(errorID, errorMsg.toString());
		} catch (IOException e) {
			log.error("输出错误信息 出错：" + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("", e);
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	// 输出二进制流内容
	public void sendBinaryStream(HttpServletResponse resp, String contentType,
			InputStream data) {
		try {
			resp.setContentType(contentType);
			int length = FileCopyUtils.copy(data, resp.getOutputStream());
			resp.setContentLength(length);
			// resp.getOutputStream().flush();
		} catch (IOException e) {
			log.error("输出BinaryStream出错：" + e.getLocalizedMessage());
			if (log.isDebugEnabled()) {
				log.debug("", e);
			}
		}
	}

	// 输出文件流
	public void sendBinaryStream(HttpServletResponse resp, File file) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			int length = FileCopyUtils.copy(in, resp.getOutputStream());
			resp.setContentLength(length);
			// resp.getOutputStream().flush();
		} catch (Exception e) {
			log.debug("sendBinaryStream.Error：" + e.getMessage());
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	// 生成下载
	public void download(HttpServletResponse resp, String fileName, byte[] data) {
		try {
			fileName = URLEncoder.encode(fileName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Encode FileName Error:" + e.getMessage());
		}

		String content = "attachment; filename=" + fileName;
		resp.addHeader("Content-Disposition", content);
		resp.setCharacterEncoding("utf-8");
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		sendBinaryStream(resp, "application/octet-stream", stream);
	}

	// 生成文件下载
	public void download(HttpServletResponse resp, String fileName, File file) {
		try {
			fileName = URLEncoder.encode(fileName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Encode FileName Error:" + e.getMessage());
		}

		String content = "attachment; filename=" + fileName;
		resp.addHeader("Content-Disposition", content);
		resp.setCharacterEncoding("utf-8");
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			sendBinaryStream(resp, "application/octet-stream", stream);
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException for " + file.getAbsolutePath());
		} catch (Throwable e) {
			log.error("warn：" + e.getLocalizedMessage());
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	/*
	 * 初始化数据绑定 解析器
	 */
	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
		CustomJsonDateEditor editor = new CustomJsonDateEditor();
		binder.registerCustomEditor(java.sql.Date.class, editor);
		binder.registerCustomEditor(java.sql.Timestamp.class, editor);
		binder.registerCustomEditor(java.util.Date.class, editor);
	}

}
