package net.asangarin.hexcolors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.mmogroup.mmolib.MMOLib;

public class ColorParse {
	private final List<MessageHolder> messages = new ArrayList<>();
	private String pre = "";

	public ColorParse(String str) {
		Pattern p = Pattern.compile("\\<((#|HEX)[^\\>]+)\\>");
		Matcher m = p.matcher(str);

		HEXUtility last = null;

		while (m.find()) {
			HEXUtility hex = new HEXUtility(m.group(), m.start(), m.end());
			if (last == null)
				pre = str.substring(0, hex.getStart());
			else
				messages.add(new MessageHolder(str.substring(last.getEnd(), hex.getStart()), last.getHex()));
			last = hex;
		}

		if (last == null) {
			messages.clear();
			pre = str;
		} else
			messages.add(new MessageHolder(str.substring(last.getEnd(), str.length()), last.getHex()));
	}

	public ColorParse(char colorCode, String str) {
		this(ChatColor.translateAlternateColorCodes(colorCode, str));
	}

	public String toJson() {
		if (messages.isEmpty())
			return "[\"\",{\"text\":\"" + pre + "\"}]";

		JsonArray json = new JsonArray();
		json.add("");
		if (!pre.isEmpty()) {
			JsonObject preText = new JsonObject();
			preText.addProperty("text", pre);
			json.add(preText);
		}
		for (MessageHolder s : messages)
			if (s.hasMessage())
				json.add(s.colorize());
		return json.toString();
	}

	public String toChatColor() {
		StringBuilder builder = new StringBuilder();
		builder.append(pre);

		for (MessageHolder holder : messages)
			builder.append(getColor(holder.getColor()) + holder.getMessage());

		return builder.toString();
	}

	public TextComponent toComponents() {
		TextComponent base = new TextComponent();
		for (BaseComponent comp : TextComponent.fromLegacyText(pre))
			base.addExtra(comp);
		for (MessageHolder holder : messages) {
			for (BaseComponent comp : TextComponent.fromLegacyText(holder.getMessage())) {
				comp.setColor(getColor(holder.getColor()));
				base.addExtra(comp);
			}
		}
		return base;
	}

	@SuppressWarnings("deprecation")
	public static ChatColor getColor(String str) {
		String input = str.toUpperCase();
		try {
			if (MMOLib.plugin.getVersion().isStrictlyHigher(1, 15)) return ChatColor.of(input);
			else return ChatColor.valueOf(input);
		} catch (IllegalArgumentException e1) {
			Optional<ChatColor> color = Optional.ofNullable(ChatColor.getByChar(input.charAt(0)));
			if (color.isPresent())
				return color.get();
			else {
				MMOLib.plugin.getLogger().severe("Tried parsing '" + input + "' as a chat color, but failed!");
				if (!MMOLib.plugin.getVersion().isStrictlyHigher(1, 15)) {
					MMOLib.plugin.getLogger().severe("Are you trying to use HEX colors? That only works on 1.16+!");
					MMOLib.plugin.getLogger().severe("If not, Make sure the color you entered exists!");
				} else
					MMOLib.plugin.getLogger().severe("Make sure the color you entered exists!");

				return ChatColor.WHITE;
			}
		}
	}
}
