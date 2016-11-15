package com.prototype.emailservice.model;

public class StatusResponse {
	private Boolean success;
	
	private String message;
	
	public StatusResponse(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}
	
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	
	public Boolean getSuccess() {
		return success;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
