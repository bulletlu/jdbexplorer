package cn.com.qimingx.dbe.action.bean;

import cn.com.qimingx.core.WebParamBean;

/**
 * @author inc062805
 * 
 * Ext Tree Loader 的参数Bean
 */
public class TreeNodeBean extends WebParamBean {
	private String node;
	private String text;
	private String path;
	private String[] pathValue;

	private String getPathValue(int index) {
		if (pathValue == null) {
			pathValue = getPath().split("/");
		}
		if (pathValue.length > index) {
			return pathValue[index];
		}
		return null;
	}

	public int getDepth() {
		return getPathValueLength() - 1;
	}

	public String getDatabase() {
		return getPathValue(0);
	}

	public String getSchema() {
		return getPathValue(1);
	}

	public String getType() {
		return getPathValue(2);
	}

	public String getElementName() {
		return getNodeName();
	}

	public String getNodeName() {
		int idx = getPathValueLength() - 1;
		return idx > -1 ? getPathValue(idx) : null;
	}

	public int getPathValueLength() {
		if (path == null) {
			return -1;
		}

		if (pathValue == null) {
			pathValue = getPath().split("/");
		}
		return pathValue.length;
	}

	public String getPath() {
		if (path == null) {
			return null;
		}

		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
