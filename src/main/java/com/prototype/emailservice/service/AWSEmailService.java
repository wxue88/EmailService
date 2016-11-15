package com.prototype.emailservice.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.prototype.emailservice.model.Email;
import com.prototype.emailservice.model.StatusResponse;
import com.prototype.emailservice.util.Constants;

/**
 * This class is responsible to send email through AWS SES using AWS SDK
 * @author wei_x
 *
 */
@Service
public class AWSEmailService implements IEmailService {
	
	Logger logger = Logger.getLogger(AWSEmailService.class);	
	
	private Email email;
	// @Value("${aws_access_key}") returns null, so access the property through controller
	// To do item: fix it and access AWS properties here
	private String AWS_ACCESS_KEY;
	// @Value("${aws_secret_key}") returns null, so access the property through controller
	// To do item: fix it and access AWS properties here
	private String AWS_SECRET_KEY;
	// @Value("${aws_ses_endpoint}") returns null, so access the property through controller
	// To do item: fix it and access AWS properties here
	private String AWS_SES_ENDPOINT;
		
	public AWSEmailService() {}
	
	public AWSEmailService(String AWS_ACCESS_KEY, String AWS_SECRET_KEY,
			String AWS_SES_ENDPOINT, Email email) {
		this.AWS_ACCESS_KEY = AWS_ACCESS_KEY;
		this.AWS_SECRET_KEY = AWS_SECRET_KEY;
		this.AWS_SES_ENDPOINT = AWS_SES_ENDPOINT;
		this.email = email;
	}
	
	private AmazonSimpleEmailService createAmzEmailServiceClient() 
		throws IOException {
		return new AmazonSimpleEmailServiceClient(createAWSCredentials());
	}
		
	private AWSCredentials createAWSCredentials() throws IOException {						
		return  new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);		
	}
	
	private List<String> getAddressesAsList(String addresses) {
		return Arrays.asList(addresses.split(","));
	}
	
	private Destination createDestination() {
		Destination destination = new Destination();
		String toAddress = email.getToAddress();
		
		if (toAddress != null && toAddress.length() > 0) {
			destination.setToAddresses(getAddressesAsList(toAddress));
		}
		String ccAddress = email.getCcAddress();
		
		if (ccAddress != null && ccAddress.length() > 0 ) {
			destination.setCcAddresses(getAddressesAsList(ccAddress));			
		}
		String bccAddress = email.getBccAddress();			
		if (bccAddress != null && bccAddress.length() > 0) {
			System.out.println("add bcc:"+bccAddress);
			destination.setBccAddresses(getAddressesAsList(bccAddress));			
		}
		return destination;
	}
	
	private Message createMessage() {
		Body body = new Body(new Content(email.getContent()));
		Message message = new Message(new Content(email.getSubject()), body);
		return message;
	}
	
	/**
	 * Send Email
	 */
	public StatusResponse send() {			
		try {
			AmazonSimpleEmailService client = createAmzEmailServiceClient();
			Destination destination = createDestination();
			Message message = createMessage();
			SendEmailRequest request = new SendEmailRequest(email.getFromAddress(), destination, message);			
			client.setEndpoint(AWS_SES_ENDPOINT);
			client.sendEmail(request);				
		} catch (Exception ex) {			
			logger.error(ex.getMessage() + ex);
			if (ex.getClass().getName().equals(Constants.AWS_UNAVAILABLE_EXCEPTION)) {
				return new StatusResponse(Constants.FAILURE, Constants.EMAIL_SERVICE_IS_DOWN_MESSAGE);
			}
			return new StatusResponse(Constants.FAILURE, Constants.FAILURE_MESSAGE);
		}
		return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_MESSAGE);
		}	
}
