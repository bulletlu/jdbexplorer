package cn.com.qimingx.core;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

/**
 * @author inc062805
 * 
 * 任务处理结果描述对象
 */
public class ProcessResult<T> {
	private boolean success = false;
	private String message = null;
	private T data = null;

	public ProcessResult() {
	}

	public ProcessResult(boolean isSuccess) {
		success = isSuccess;
	}

	public String toJSON() {
		return toJSONObject().toString();
	}

	public JSON toJSONObject() {
		JSONObject json = new JSONObject();
		json.element("success", success);
		if (message != null) {
			json.element("msg", message);
		}
		return json;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T result) {
		this.data = result;
	}

	public boolean isFailing() {
		return !success;
	}

	public void setFailing(boolean failing) {
		success = !failing;
	}
}
