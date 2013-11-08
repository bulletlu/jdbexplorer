package cn.com.qimingx.dbe;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import cn.com.qimingx.core.ProcessResult;
import cn.com.qimingx.dbe.action.bean.GridQueryLoadBean;
import cn.com.qimingx.dbe.service.DBInfoService;
import cn.com.qimingx.json.MyJSONUtils;

/**
 * @author inc062805
 * 
 * 实现在SQLQueryPanel上的各种操作
 */
@Service("sqlQueryOperator")
public class SQLQueryOperator {
	// 执行sql语句
	public ProcessResult<JSON> execute(DBInfoService service,
			GridQueryLoadBean param) {
		String sql = param.getSql();
		if (sql != null || sql.length() > 0) {
			if (sql.startsWith("SELECT") || sql.startsWith("select")) {
				return executeQuery(sql, service, param);
			} else {
				return executeUpdate(sql, service, param);
			}
		} else {
			ProcessResult<JSON> pr = new ProcessResult<JSON>();
			pr.setMessage("sql sentence invalid~!");
			return pr;
		}
	}

	// 执行 sql
	private ProcessResult<JSON> executeUpdate(String sql,
			DBInfoService service, GridQueryLoadBean param) {
		ProcessResult<String> pr = service.executeUpdate(sql, null);

		// columns
		List<TableColumnInfo> columns = new ArrayList<TableColumnInfo>(1);
		columns.add(new TableColumnInfo("Result", Types.VARCHAR, 100, true));

		// data
		Map<String, Object> row = new HashMap<String, Object>(1);
		row.put("Result", pr.getMessage());
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(1);
		rows.add(row);
		TableDataInfo data = new TableDataInfo(1, rows);

		TableInfo tableInfo = new TableInfo();
		tableInfo.setColumns(columns);
		tableInfo.setData(data);

		// return..
		ProcessResult<JSON> ppr = new ProcessResult<JSON>(pr.isSuccess());
		if (ppr.isSuccess()) {
			ppr.setData(makeQueryResult(tableInfo));
		} else {
			ppr.setMessage(pr.getMessage());
		}
		return ppr;
	}

	// 执行sql
	private ProcessResult<JSON> executeQuery(String sql, DBInfoService service,
			GridQueryLoadBean param) {
		int start = param.getStart();
		int limit = param.getLimit();
		String condition = param.getSearchCondition();
		ProcessResult<TableInfo> prtInfo;
		prtInfo = service.executeQuery(sql, start, limit, condition);

		ProcessResult<JSON> pr = new ProcessResult<JSON>();
		if (prtInfo.isFailing()) {
			pr.setFailing(true);
			pr.setMessage(prtInfo.getMessage());
		} else {
			pr.setSuccess(true);
			pr.setData(makeQueryResult(prtInfo.getData()));
		}
		return pr;
	}

	// 创建查询结果..
	private JSON makeQueryResult(TableInfo tableInfo) {
		// metaData
		JSONArray jsonFields = new JSONArray();
		for (TableColumnInfo column : tableInfo.getColumns()) {
			JSON jsonExtType = MyJSONUtils.toJsonExclude(column.getExtType());
			JSONObject jsonField = new JSONObject();
			jsonField.element("name", column.getName());
			jsonField.element("type", column.getExtType().getType());
			jsonField.element("extType", jsonExtType);
			if (column.getExtType().isDateType()) {
				String[] args = new String[] { "value", "record" };
				String script = "if (value.time) return new Date(value.time);";
				script += "else return value;";
				JSONFunction jsonFun = new JSONFunction(args, script);
				jsonField.element("convert", jsonFun);
			}
			jsonFields.add(jsonField);
		}
		JSONObject metaData = new JSONObject();
		metaData.element("totalProperty", "total");
		metaData.element("root", "rows");
		metaData.element("fields", jsonFields);

		// Data rows
		JSONArray rows = new JSONArray();
		for (Map<String, Object> row : tableInfo.getData().getRows()) {
			JSONObject jsonData = new JSONObject();
			for (Iterator<String> it = row.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				jsonData.element(key, row.get(key));
			}
			rows.add(jsonData);
		}

		JSONObject json = new JSONObject();
		json.element("metaData", metaData);
		json.element("total", tableInfo.getData().getTotal());
		json.element("rows", rows);

		return json;
	}
}
