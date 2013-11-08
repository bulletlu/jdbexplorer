package cn.com.qimingx.dbe.action;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

/**
 * @author Wangwei
 * 
 * 用于提供全局公用的功能Action
 */
@Controller("dbeAction")
public class DbeActionController extends AbstractDbeActionController {
	// logger
	private static final Log log = LogFactory.getLog(DbeActionController.class);

	// 下载临时文件
	public void downfile(HttpServletRequest req, HttpServletResponse resp) {
		String target = req.getParameter("targetFile");
		log.debug("call dbeAction.downfile.target:" + target);
		Assert.hasLength(target, "未指定目标文件..~~");

		File file = workDirectory(req).getFileByName(target);
		if (file != null) {
			String name = file.getName();
			int idx = name.indexOf(".") + 1;
			if (idx > 0) {
				name = name.substring(idx);
			}
			download(resp, name, file);
		} else {
			sendError(resp, 500, "文件无法下载:" + target);
		}
	}
}
