package net.mmogroup.mmolib.api;

import com.google.gson.JsonArray;

public class MMOColor {
	public static String createJSONText(String text) {
		JsonArray json = new JsonArray();
		json.add("");
		return json.toString();
	}
}
