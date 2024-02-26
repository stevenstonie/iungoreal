package com.stevenst.app.util;
import org.json.JSONObject;

public class JsonUtil {
	private JsonUtil() {}

	public static String convertStringToJson(String string) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("string", string);

		return jsonObject.toString();
	}
	// TODO: create a class with the string instead so the string will convert to json automatically
	
	public static String getStringFromJson(String string) {
		JSONObject jsonObject = new JSONObject(string);
		return jsonObject.optString("string", "");
	}
}
