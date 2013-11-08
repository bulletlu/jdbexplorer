package cn.com.qimingx.json;

import java.sql.Timestamp;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;

public class MyJsonConfig extends JsonConfig {
	public MyJsonConfig() {
		JsonBeanProcessor dbp = new MyJsDateJsonBeanProcessor();
		registerJsonBeanProcessor(Date.class, dbp);
		registerJsonBeanProcessor(Timestamp.class, dbp);
		registerJsonBeanProcessor(java.sql.Date.class, dbp);
	}
}
