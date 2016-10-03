package hu.farago.web.response;

import org.joda.time.DateTime;

public class Response {

	private ResponseType responseType;
	private String response;
	private DateTime clientDateTime;

	public Response(ResponseType responseType, String response) {
		super();
		this.responseType = responseType;
		this.response = response;
		this.clientDateTime = DateTime.now();
	}
	
	public String getHtmlResponse() {
		return "<div style=\"color: " + responseType.getColor() + "\">"
				+ response + "</div>";
	}
	
	public DateTime getClientDateTime() {
		return clientDateTime;
	}

}
