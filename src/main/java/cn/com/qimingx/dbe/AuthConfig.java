package cn.com.qimingx.dbe;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author lunianping
 *
 */
public class AuthConfig {
	private static final Log log = LogFactory.getLog(AuthConfig.class.getName());
	
	private static final AuthConfig config = new AuthConfig();
	
	public static final String CONF_FILE = "/whitelist.xml";
	
	private Properties properties;
	
	private AuthConfig(){
		properties = new Properties();
		this.load();
	}
	
	// 获得单例对象
	public static AuthConfig getInstance() {
		return config;
	}
	
	public void reload(){
		this.load();
	}
	
	
	private synchronized void load(){
		InputStream input = null;
		try {
			input = this.getClass().getResourceAsStream(CONF_FILE);
			//properties.load(input);
			this.properties.loadFromXML(input);
		} catch (IOException e) {
			log.error("读取配置文件出错：" + e.getMessage());
		} finally {
			IOUtils.closeQuietly(input);
		}
		
		for(Object key:this.properties.keySet()){
			log.debug("Key:"+key+" Value:"+this.properties.getProperty((String)key));
		}
	}
	
	/**
	 * @param url
	 * @param clientHost
	 * @param user
	 * @return
	 */
	public boolean isValid(String url,String clientHost,String user){
		log.info("DBInfo:"+url+" Client:"+clientHost+" DBUser:"+user);
		
		String hostList = properties.getProperty(url.replaceAll("\\?.*$", ""));
		if(hostList != null){
			String[] hosts = hostList.split(";");
			for(String host:hosts){
				String ipRegx = host.replaceAll("\\(.*\\)$", "");
				String users = host.replaceAll("^.*\\(", "").replaceAll("\\)", "").toLowerCase();
				log.debug(host+"  "+ipRegx+"   "+users);
				if(clientHost.matches(ipRegx) && this.haveUser(users, user)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param loginInfo
	 * @return
	 */
	public boolean isValid(AuditInfo loginInfo){
		return this.isValid(loginInfo.getUrl(), loginInfo.getClient(), loginInfo.getUser());
	}
	
	
	/**
	 * @param users
	 * @param user
	 * @return
	 */
	private boolean haveUser(String users,String user){
		if(users == null) return false;
		for(String u :users.split(",")){
			if(u.trim().equals(user)){
				return true;
			}
		}
		return false;
	}
}
