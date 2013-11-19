package cn.com.qimingx.dbe;

/**
 * @author lunianping
 *
 */
public class AuditInfo {
	
	private String client;
	private String user;
	private String url;
	
	public AuditInfo(String client,String user,String url){
		this.client = client;
		this.user = user;
		this.url = url;
	}
	
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String toString(){
		return "["+client+"]["+user+"]["+url+"]";
	}
	
}
