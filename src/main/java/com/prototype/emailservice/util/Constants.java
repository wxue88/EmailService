package com.prototype.emailservice.util;

public class Constants {
	//Prevents instantiation
	private Constants() {}
	
	public static final String AWS_UNAVAILABLE_EXCEPTION = "com.amazonaws.AmazonServiceException";
	
	public static final Boolean SUCCESS = true; 
	
	public static final Boolean FAILURE = false;
	
	public static final String SUCCESS_MESSAGE = "THE MESSAGE WAS SENT SUCCESSFULLY!";
	
	public static final String FAILURE_MESSAGE = "AN ERROR HAS OCCURRED. PlEASE CONTACT SYSTEM ADMIN!";
	
	public static final String EMAIL_SERVICE_IS_DOWN_MESSAGE = "EMAIL SERVICE IS DOWN";
	
	public static final String SENDGRID_CONTENT_TYPE = "text/plain";
	
	public static final String SENDGRID_ENDPOINT = "mail/send";

}
