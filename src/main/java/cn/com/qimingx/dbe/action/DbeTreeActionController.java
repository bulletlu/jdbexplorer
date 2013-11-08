package cn.com.qimingx.dbe.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.DBConnectionState;
import cn.com.qimingx.dbe.TreeOperator;
import cn.com.qimingx.dbe.action.bean.TreeNodeBean;
import cn.com.qimingx.dbe.service.DBInfoService;

/**
 * @author Wangwei
 * 
 * 用于提供 DBTreePanel 上的功能Action
 */
@Controller("dbeTreeAction")
public class DbeTreeActionController extends AbstractDbeActionController {
	// logger
	private static final Log log = LogFactory
			.getLog(DbeTreeActionController.class);

	//
	private TreeOperator treeOperator;

	// Tree nodes，读取当前Node 的子Nodes
	public void tree(HttpServletRequest req, HttpServletResponse resp,
			TreeNodeBean param) {
		log.debug("call dbeTreeAction.tree,param:" + param);

		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// process
		DBInfoService service = prDBCS.getData().getDBInfoService();
		ProcessResult<JSON> pr = treeOperator.tree(service, param);
		if (pr.isSuccess()) {
			sendJSON(resp, pr.getData().toString());
		} else {
			sendJSON(resp, pr.toJSON());
		}
	}

	// 删除 TreeNode 代表的元素
	public void drop(HttpServletRequest req, HttpServletResponse resp,
			TreeNodeBean bean) {
		log.debug("call dbeTreeAction.Drop TableOrView..," + bean);

		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// process
		DBInfoService service = prDBCS.getData().getDBInfoService();
		ProcessResult<String> pr = treeOperator.drop(service, bean);
		if (pr.isSuccess()) {
			sendJSON(resp, pr.toJSON());
		} else {
			// TODO:此处应该修改成返回 JSON 表示错误标志,
			sendErrorJSON(resp, pr.toJSON());
		}
	}

	// 打开指定元素
	public void open(HttpServletRequest req, HttpServletResponse resp,
			TreeNodeBean param) {
		log.debug("call dbeTreeAction.open,param:" + param);

		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// process
		DBInfoService service = prDBCS.getData().getDBInfoService();
		ProcessResult<JSON> pr = treeOperator.open(service, param);
		if (pr.isSuccess()) {
			sendJSON(resp, pr.getData().toString());
		} else {
			sendJSON(resp, pr.toJSON());
		}
	}

	// 取得数据库支持的数据类型列表
	public void datatypes(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("call dbeTreeAction.datatypes");
		// JSONArray jsonArray = new JSONArray();
		//
		// JSONObject jsonRow = new JSONObject();
		// jsonRow.element("key", "Integer");
		// jsonRow.element("value", "Integer");
		// jsonArray.add(jsonRow);
		//
		// jsonRow = new JSONObject();
		// jsonRow.element("key", "VARCHAR");
		// jsonRow.element("value", "VARCHAR");
		// jsonArray.add(jsonRow);
		//
		// JSONObject json = new JSONObject();
		// json.element("types", jsonArray);
		//
		// sendJSON(resp, json.toString());
		// check current login state
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		// process
		DBInfoService service = prDBCS.getData().getDBInfoService();
		ProcessResult<JSON> pr = treeOperator.getDataTypes(service);
		if (pr.isSuccess()) {
			sendJSON(resp, pr.getData().toString());
		} else {
			sendJSON(resp, pr.toJSON());
		}
	}

	// 打开指定表的属性信息
	public void loadTableAttribute(HttpServletRequest req,
			HttpServletResponse resp, TreeNodeBean param) {
		log.debug("call dbeTreeAction.loadTableAttribute:" + param);
		ProcessResult<DBConnectionState> prDBCS = checkLogin(req);
		if (prDBCS.isFailing()) {
			log.error(prDBCS.getMessage());
			sendJSON(resp, prDBCS.toJSON());
			return;
		}

		ProcessResult<JSON> pr = null;

		if (param.getNodeName() == null) {
			pr = new ProcessResult<JSON>(false);
			pr.setMessage("getTableAttribute Error:tablename is null~~!");
			log.error(pr.getMessage());
		} else {
			log.debug("call loadTableAttribute,text:" + param.getText());
			DBInfoService service = prDBCS.getData().getDBInfoService();
			pr = treeOperator.loadColumn(service, param);
		}

		if (pr.isSuccess()) {
			sendJSON(resp, pr.getData().toString());

		} else {
			sendError(resp, 500, pr.toJSON());
		}
	}

	// 根据表的列名获取列信息
	public void loadColumnPar(HttpServletRequest req, HttpServletResponse resp,
			TreeNodeBean param) {

		log.debug("load ColumPar......................!");
		String columnname = req.getParameter("column");
		String json = "";
		if (columnname != null && !columnname.equals("")) {
			json = "{root:[{parameter:'默认值',value:'12'},{parameter:'精度',value:'1'},{parameter:'数值范围',value:'2000'},{parameter:'标识',value:'no'},{parameter:'列举',value:'233'}]}";
		} else {
			json = "{root:[{parameter:'默认值',value:'24'},{parameter:'精度',value:'2'},{parameter:'数值范围',value:'4000'},{parameter:'标识',value:'yes'},{parameter:'列举',value:'4544'}]}";
		}
		sendJSON(resp, json);
	}

	public TreeOperator getTreeNodeLoader() {
		return treeOperator;
	}

	@Autowired
	public void setTreeNodeLoader(TreeOperator treeOperator) {
		this.treeOperator = treeOperator;
	}
}
