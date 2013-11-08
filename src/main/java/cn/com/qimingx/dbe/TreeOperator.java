package cn.com.qimingx.dbe;

import java.util.List;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.action.bean.TreeNodeBean;
import cn.com.qimingx.dbe.service.DBInfoService;
import cn.com.qimingx.json.MyJSONUtils;
import cn.com.qimingx.utils.SQLTypeUtils;

/**
 * @author inc062805
 * 
 * 实现 DBTreePanel 上的各种操作
 */
@Service("treeOperator")
public class TreeOperator {
	// logger
	private static final Log log = LogFactory.getLog(TreeOperator.class);

	/**
	 * 取得当前指定Node 的子Nodes
	 */
	public ProcessResult<JSON> tree(DBInfoService service, TreeNodeBean node) {
		// load treenodes
		if (node.getDepth() < 0) {
			// 取得数据库的名称
			return getDatabaseName(service);
		} else if (node.getDepth() == 0) {
			// 取得模式列表
			return getDBSchemas(service);
		} else if (node.getDepth() == 1) {
			// 取得数据库支持的元素类型列表
			return getDBElementTypes(service);
		} else if (node.getDepth() == 2) {
			// 取得指定类型的元素列表
			String schema = node.getSchema();
			String type = node.getType();
			return getDBElements(service, schema, type);
		} else if (node.getDepth() == 3) {
			// 取得数据表的列信息
			// TODO:某些元素可能没有列信息，请予以处理
			String schema = node.getSchema();
			String name = node.getElementName();
			return getTableColumns(service, schema, name);
		} else {
			log.debug("not sub nodes:" + node);
		}
		// other~~~...

		ProcessResult<JSON> pr = new ProcessResult<JSON>(true);
		pr.setData(new JSONArray());
		return pr;
	}

	/**
	 * 删除 当前节点代表 的元素
	 */
	public ProcessResult<String> drop(DBInfoService service, TreeNodeBean node) {
		ProcessResult<String> pr = new ProcessResult<String>(false);
		String type = node.getType();
		if (!type.equalsIgnoreCase("TABLE") && !type.equalsIgnoreCase("VIEW")) {
			pr.setMessage("不支持的dbelement类型：" + type);
			log.error(pr.getMessage());
			return pr;
		}
		// make sql
		String sql = "drop " + type + " " + node.getElementName();
		log.debug("drop.sql:" + sql);

		// get service Object.
		return service.executeUpdate(sql, null);
	}

	// 打开指定的元素的结构信息
	public ProcessResult<JSON> open(DBInfoService service, TreeNodeBean node) {
		String name = node.getNodeName();
		String type = node.getType();
		if (type.equalsIgnoreCase("table") || type.equalsIgnoreCase("view")) {
			String schema = node.getSchema();
			return getTableInfo(service, schema, name);
		}
		// otehrs...

		// 无处理分支，返回空数据
		ProcessResult<JSON> pr = new ProcessResult<JSON>(true);
		pr.setData(new JSONArray());
		return pr;
	}

	// 取得Table 和 View 信息
	public static ProcessResult<JSON> getTableInfo(DBInfoService service,
			String schema, String name) {
		ProcessResult<JSON> pr = new ProcessResult<JSON>();

		// get TableInfo
		ProcessResult<TableInfo> prTableInfo;
		prTableInfo = service.getTableInfo(schema, name);
		if (prTableInfo.isFailing()) {
			pr.setSuccess(false);
			pr.setMessage(prTableInfo.getMessage());
			return pr;
		}
		TableInfo tableInfo = prTableInfo.getData();

		// 生成json
		JSON jsont = MyJSONUtils.toJsonExclude(tableInfo);

		JSONObject json = new JSONObject();
		json.element("succeed", true);
		json.element("table", jsont);
		pr.setSuccess(true);
		pr.setData(json);

		return pr;
	}

	// 取得表的列信息
	public ProcessResult<JSON> getTableColumns(DBInfoService service,
			String schema, String name) {
		ProcessResult<TableInfo> prTableInfo;
		prTableInfo = service.getTableInfo(schema, name);
		List<TableColumnInfo> list = prTableInfo.getData().getColumns();
		JSONArray jsons = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject json = new JSONObject();
			TableColumnInfo tci = list.get(i);
			// String type = tci.getExtType().getType();
			// json.element("id", element);
			json.element("text", tci.getName());// + "(" + type + ")"
			if (tci.isPkColumn()) {
				json.element("icon", "images/field_key.png");
			} else if (tci.isFkColumn()) {
				json.element("icon", "images/field_link.png");
			} else {
				json.element("icon", "images/field.png");
			}
			json.element("leaf", "true");
			jsons.add(json);
		}
		ProcessResult<JSON> pr = new ProcessResult<JSON>(true);
		pr.setData(jsons);
		return pr;
	}

	// 取得数据库 中指定类型的元素
	public ProcessResult<JSON> getDBElements(DBInfoService service,
			String schema, String type) {
		List<String> list = service.getElements(schema, type);
		JSONArray jsons = new JSONArray();
		for (String element : list) {
			JSONObject json = new JSONObject();
			// json.element("id", element);
			json.element("text", element);
			json.element("icon", "images/table.png");
			jsons.add(json);
		}
		ProcessResult<JSON> pr = new ProcessResult<JSON>(true);
		pr.setData(jsons);
		return pr;
	}

	// 取得数据库支持的元素类型：例如表、视图、存储过程、序列等
	public ProcessResult<JSON> getDBElementTypes(DBInfoService service) {
		List<String> list = service.getElementTypes();
		JSONArray jsons = new JSONArray();
		for (String type : list) {
			JSONObject json = new JSONObject();
			// json.element("id", type);
			json.element("text", type);
			json.element("icon", "images/type.png");
			jsons.add(json);
		}
		ProcessResult<JSON> pr = new ProcessResult<JSON>(true);
		pr.setData(jsons);
		return pr;
	}

	// 取得数据库模式名称
	public ProcessResult<JSON> getDBSchemas(DBInfoService service) {
		List<String> list = service.getSchemas();
		JSONArray jsons = new JSONArray();
		for (String schema : list) {
			JSONObject json = new JSONObject();
			// json.element("id", schema);
			json.element("text", schema);
			json.element("icon", "images/schema.png");
			jsons.add(json);
		}

		ProcessResult<JSON> pr = new ProcessResult<JSON>(true);
		pr.setData(jsons);
		return pr;

	}

	// 取得数据库名称
	public ProcessResult<JSON> getDatabaseName(DBInfoService service) {
		String name = service.getDatabaseName();
		JSONObject json = new JSONObject();
		// json.element("id", "database");
		json.element("text", name);
		json.element("icon", "images/database.png");
		ProcessResult<JSON> pr = new ProcessResult<JSON>(true);
		pr.setData(json);
		return pr;
	}

	//
	public ProcessResult<JSON> getDataTypes(DBInfoService service) {
		List<FieldDataType> fdts = service.getDataTypes();
		JSONArray jsonArray = new JSONArray();
		for (FieldDataType fdt : fdts) {
			jsonArray.add(fdt.toJSON());
		}
		JSONObject json = new JSONObject();
		json.element("types", jsonArray);
		ProcessResult<JSON> pr = new ProcessResult<JSON>(true);
		pr.setData(json);
		return pr;
	}

	/**
	 * 根据树节点获取表属性信息
	 */
	public ProcessResult<JSON> loadColumn(DBInfoService service,
			TreeNodeBean param) {
		ProcessResult<TableInfo> prData;
		prData = service.getTableInfo(param.getSchema(), param.getText());
		List<TableColumnInfo> columns = prData.getData().getColumns();
		JSONArray jsonArray = new JSONArray();
		for (TableColumnInfo column : columns) {
			JSONObject jsonData = new JSONObject();
			jsonData.put("columName", column.getName());
			jsonData.put("dataType", SQLTypeUtils.getJdbcTypeName(column
					.getType()));
			jsonData.put("maxlength", column.getSize());
			jsonData.put("isNull", column.isNullable());
			jsonArray.add(jsonData);
		}
		JSONObject json = new JSONObject();
		json.element("columns", jsonArray);

		// return
		ProcessResult<JSON> pr = new ProcessResult<JSON>(true);
		pr.setData(json);
		return pr;
	}

}
