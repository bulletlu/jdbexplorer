package cn.com.qimingx.dbe;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author inc062805
 * 
 * 系统配置对象
 */
public class DBEConfig {
	// Logger
	private static final Log log = LogFactory.getLog(DBEConfig.class);

	// 配置文件
	public static final String CONF_FILE = "/dbe_config.js";

	// Self
	private static final DBEConfig config = new DBEConfig();

	//
	public static void main(String[] args) {
		getInstance();
	}

	// 获得单例对象
	public static DBEConfig getInstance() {
		return config;
	}

	//
	public DBTypeInfo getDBTypeInfo(String name) {
		return dbtypes.get(name);
	}

	public String getDBTypeOptions() {
		return dbtypeOptions;
	}

	// 数据库类型列表
	private Map<String, DBTypeInfo> dbtypes;

	// 数据库类型选项
	private String dbtypeOptions;

	// 构建器
	@SuppressWarnings("unchecked")
	private DBEConfig() {
		// load Config
		InputStream input = null;
		JSONObject json = null;
		try {
			input = this.getClass().getResourceAsStream(CONF_FILE);
			if (input != null) {
				StringWriter output = new StringWriter();
				IOUtils.copy(input, output, "utf-8");
				json = JSONObject.fromObject(output.toString());
			}
		} catch (IOException e) {
			log.error("读取配置文件出错：" + e.getMessage());
		} finally {
			IOUtils.closeQuietly(input);
		}

		// parse Config
		JSONArray jsonDBTypes = json.getJSONArray("dbtypes");
		Collection<DBTypeInfo> typeList;
		typeList = JSONArray.toCollection(jsonDBTypes, DBTypeInfo.class);
		dbtypes = new HashMap<String, DBTypeInfo>(typeList.size());
		JSONArray jsonOptions = new JSONArray();
		for (DBTypeInfo type : typeList) {
			dbtypes.put(type.getName(), type);
			JSONObject jsonOpt = new JSONObject();
			jsonOpt.element("key", type.getName());
			jsonOpt.element("value", type.getUrl());
			jsonOptions.add(jsonOpt);
		}

		JSONObject jsonDBTypeOptions = new JSONObject();
		jsonDBTypeOptions.element("types", jsonOptions);
		dbtypeOptions = jsonDBTypeOptions.toString();
	}

	public Iterator<String> getDbTypeNames() {
		Iterator<String> it = dbtypes.keySet().iterator();
		return it;
	}
}
