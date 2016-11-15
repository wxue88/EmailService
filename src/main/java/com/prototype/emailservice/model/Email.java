package com.prototype.emailservice.model;

import java.io.Serializable;
/**
 * This class is served as a model object to capture all fields of an email 
 * @author wei_x
 *
 */
public class Email implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String fromAddress;
	
	private String toAddress;
	
	private String ccAddress;
	
	private String bccAddress;
	
	private String subject;
	
	private String content;
	
	public Email() {
		super();
	}
	
	public Email(String fromAddress, String toAddress, 
			String ccAddress, String bccAddress,
			String subject, String content) {
		super();
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.ccAddress = ccAddress;
		this.bccAddress = bccAddress;
		this.subject = subject;
		this.content = content;
	}
	
	public String getFromAddress() {
		return fromAddress;
	}
	
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	
	public String getToAddress() {
		return toAddress;
	}
	
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	
	public String getCcAddress() {
		return ccAddress;
	}
	
	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}
	
	public String getBccAddress() {
		return bccAddress;
	}
	
	public void setBccAddress(String bccAddress) {
		this.bccAddress = bccAddress;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
}
