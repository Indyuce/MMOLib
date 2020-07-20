package net.asangarin.hexcolors;

public class HEXUtility {
	private final String color;
	private final int start, end;

	protected HEXUtility(String color, int start, int end) {
		this.color = color;
		this.start = start;
		this.end = end;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public String getHex() {
		if(color.contains("HEX")) return color.substring(1, 10).replace("HEX", "#");
		return color.substring(1, 8);
	}
}
