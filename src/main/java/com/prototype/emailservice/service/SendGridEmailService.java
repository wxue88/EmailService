package com.prototype.emailservice.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.stereotype.Service;

import com.prototype.emailservice.model.StatusResponse;
import com.prototype.emailservice.util.Constants;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

/**
 * This class is responsible to send email through SendGrid using SendGrid-java library
 * @author wei_x
 *
 */
@Service
public class SendGridEmailService implements IEmailService {
	
	Logger logger = Logger.getLogger(SendGridEmailService.class);	
	// @Value("${sendgrid_api_key}") returns null, so access the property through controller
	// To do item: fix it and access SendGrid properties here
	private String SENDGRID_API_KEY;
	
	private com.prototype.emailservice.model.Email email;	
	
	public SendGridEmailService() {}
	
	public SendGridEmailService(String SENDGRID_API_KEY, com.prototype.emailservice.model.Email email) {
		this.SENDGRID_API_KEY = SENDGRID_API_KEY;
		this.email = email;
	}
	
	private List<String> getAddressesAsList(String addresses) {
		return Arrays.asList(addresses.split(","));
	}
	
	// Unlike AWS SES, they take care of list of To, Cc, Bcc. 
	// SendGrid has to use Personalization to handle these.
	private Mail createMail() {
		Personalization personalization = new Personalization();
		com.sendgrid.Email fromEmail = new Email(email.getFromAddress());
		Content content = new Content(Constants.SENDGRID_CONTENT_TYPE, email.getContent());
		com.sendgrid.Email toEmail = new com.sendgrid.Email();
		String toAddresses = email.getToAddress();
		if (toAddresses != null && toAddresses.length() > 0) {
			List<String> toAddressList = getAddressesAsList(toAddresses);
			for (String toAddress : toAddressList) {
				toEmail.setEmail(toAddress);
				personalization.addTo(toEmail);
			}
		}
		com.sendgrid.Email ccEmail = new com.sendgrid.Email();
		String ccAddresses = email.getCcAddress();
		if (ccAddresses != null && ccAddresses.length() > 0) {
			List<String> ccAddressList = getAddressesAsList(ccAddresses);
			for (String ccAddress : ccAddressList) {
				ccEmail.setEmail(ccAddress);
				personalization.addCc(ccEmail);
			}
		}
		com.sendgrid.Email bccEmail = new com.sendgrid.Email();
		String bccAddresses = email.getBccAddress();		
		if (bccAddresses != null && bccAddresses.length() > 0) {
			List<String> bccAddressList = getAddressesAsList(bccAddresses);
			for (String bccAddress : bccAddressList) {				
				bccEmail.setEmail(bccAddress);
				personalization.addBcc(bccEmail);
			}
		}
		Mail mail = new Mail();		
		mail.setFrom(fromEmail);
		mail.setSubject(email.getSubject());
		mail.addContent(content);
		mail.addPersonalization(personalization);
		return mail;		
	}
	
	/**
	 * Send Email
	 */
	public StatusResponse send() {
		Mail mail = createMail();
		SendGrid client = new SendGrid(SENDGRID_API_KEY);
		Request request = new Request();
		Response response = new Response();
		try {
			request.method = Method.POST;
			request.endpoint = Constants.SENDGRID_ENDPOINT;
			request.body = mail.build();
			response = client.api(request);
		} catch (IOException ex) {
			logger.error(ex.getMessage() + ex);
			if (response.statusCode == 500) {
				return new StatusResponse(Constants.FAILURE, Constants.EMAIL_SERVICE_IS_DOWN_MESSAGE);
			}
			return new StatusResponse(Constants.FAILURE, Constants.FAILURE_MESSAGE);
		}		
		return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_MESSAGE);
	}

}
