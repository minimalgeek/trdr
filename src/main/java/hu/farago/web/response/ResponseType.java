package hu.farago.web.response;

public enum ResponseType {

	ERROR("red"), NOTIFICATION("black"), WARNING("orange");
	
	private String color;
	
	ResponseType(String color) {
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
	
}
