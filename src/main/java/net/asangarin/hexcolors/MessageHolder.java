package net.asangarin.hexcolors;

import com.google.gson.JsonObject;

public class MessageHolder {
	private final String message, hex;

	protected MessageHolder(String message, String hex) {
		this.message = message;
		this.hex = hex;
	}
	
	public boolean hasMessage() {
		return !message.isEmpty();
	}
	
	public JsonObject colorize() {
		JsonObject json = new JsonObject();
		json.addProperty("text", message);
		json.addProperty("color", hex);
		return json;
	}

	public String getMessage() {
		return message;
	}
	
	public String getColor() {
		return hex;
	}
}
