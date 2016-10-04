package hu.farago.ib.model.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

public class IBError {

	private int id;
	private int errorCode;
	private String errorMsg;
	
	public IBError(String errorMsg) {
		this(0, 0, errorMsg);
	}

	public IBError(int id, int errorCode, String errorMsg) {
		this.id = id;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
