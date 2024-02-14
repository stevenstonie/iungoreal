package com.stevenst.app.util;
import org.json.JSONObject;

public class JsonUtil {
	public static String convertStringToJson(String string) {
		JSONObject jsonObject = new JSONObject();
        jsonObject.put("string", string);
        
        // Print the JSON object
        return jsonObject.toString();
	}
}
