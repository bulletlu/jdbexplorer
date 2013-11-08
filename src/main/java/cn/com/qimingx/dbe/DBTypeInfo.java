package cn.com.qimingx.dbe;


/**
 * @author Wangwei
 * 
 * 数据库类型描述对象
 */
public class DBTypeInfo {
	private String name;
	private String url;
	private String driver;
	private String service = "'cn.com.qimingx.dbe.service.DefaultDBInfoService";

	public DBTypeInfo() {
	}

	public DBTypeInfo(String name, String url, String driver) {
		this.name = name;
		this.url = url;
		this.driver = driver;
	}

	/**
	 * 判断系统当前是否支持该Type--即检查jdbc驱动是否存在
	 */
	public boolean isSupport() {
		// TODO：等待实现...
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
}
