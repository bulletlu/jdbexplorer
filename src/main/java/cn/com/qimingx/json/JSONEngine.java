package cn.com.qimingx.json;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

/**
 * @author Wangwei
 * 
 * 订制 Object <---> JSON 转换细节
 */
public class JSONEngine<T> {
	public JSON json(T object) {
		return JSONSerializer.toJSON(object);
	}

	public T bean(String jsonString) {
		// JSON json = JSONObject.fromObject(jsonString);
		// T t = JSONObject.toBean(json);
		// System.out.println("pt:" + getParameterizedType().getName());
		return null;
	}

	public void getParameterizedType() {
		// ParameterizedType pt;
		// pt = (ParameterizedType) getClass().getGenericInterfaces()[0];
		// System.out.println("@@:" + pt.getActualTypeArguments()[0]);
		// TypeVariable<Class<JSONEngine>>[] tvs =
		// getClass().getTypeParameters();

		// Type type =
		// System.out.println("@@@:" + (type instanceof ParameterizedType));
		//TypeVariable[] tvs = getClass().getTypeParameters();
		//for (TypeVariable tv : tvs) {
		//	
		//}

		// (Class<T>) pt.getActualTypeArguments()[0];
		// return null;
	}
}
