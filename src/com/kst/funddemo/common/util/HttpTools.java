package com.kst.funddemo.common.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.kst.funddemo.model.BaseBeen;

/**
 * @Description: json工具类
 * @author:Vincent
 * @copyright © qianrenzhang.com
 * @Date:2014-11-21 下午1:20:00
 */
public class HttpTools {

	public static <T> T json2Object(String json, T t) {
		Gson gson = new Gson();
		T object = null;
		try {
			object = (T) gson.fromJson(json, t.getClass());
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return object;
	}

	public static List<NameValuePair> objectToNameValuePairList(
			String fieldName, BaseBeen object) {
		List<NameValuePair> nvList = new ArrayList<NameValuePair>();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String jsonStr = gson.toJson(object);
		NameValuePair value = new BasicNameValuePair(fieldName, jsonStr);
		nvList.add(value);
		return nvList;
	}

	public static List<NameValuePair> getNameValuePair(BaseBeen been) {
		List<NameValuePair> nvList = new ArrayList<NameValuePair>();
		Class<? extends BaseBeen> clazz = been.getClass();
		Method[] methods = clazz.getMethods();

		NameValuePair nameValuePair = null;
		for (Method method : methods) {

			String mName = method.getName();
			if (mName.startsWith("get") && !mName.startsWith("getClass")
					&& !mName.equals("getCode") && !mName.equals("getMessage")
					&& !mName.equals("getUrl")  && !mName.equals("getData")) {
				String fieldName = mName.substring(3, mName.length())
						.toLowerCase();
				try {
					String value = method.invoke(been) + "";
					nameValuePair = new BasicNameValuePair(fieldName, value);
					nvList.add(nameValuePair);
					value = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return nvList;
	}
	public static Map<String, String> getParams(BaseBeen been) {
		Map<String, String> params = new HashMap<String, String>();
		Class<? extends BaseBeen> clazz = been.getClass();
		Method[] methods = clazz.getMethods();

		for (Method method : methods) {

			String mName = method.getName();
			if (mName.startsWith("get") && !mName.startsWith("getClass")
					&& !mName.equals("getCode") && !mName.equals("getMessage")
					&& !mName.equals("getUrl") && !mName.equals("getData")) {
				String fieldName = mName.substring(3, mName.length()).toLowerCase();
				try {
					String value = method.invoke(been) + "";
					params.put(fieldName, value);
					value = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return params;
	}
}
