package com.prototype.emailservice.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.prototype.emailservice.model.Email;
import com.prototype.emailservice.model.StatusResponse;
import com.prototype.emailservice.service.AWSEmailService;
import com.prototype.emailservice.service.IEmailService;
import com.prototype.emailservice.service.SendGridEmailService;
import com.prototype.emailservice.util.Constants;


/**
 * This Spring MVC Controller is responsible to handle email related HTTP requests from client side 
 * , delegate them to the proper services to execute and then return the response back to client  
 * @author wei xue
 *
 */
@RestController
@RequestMapping("/email")
public class EmailServiceController {
	
	Logger logger = Logger.getLogger(EmailServiceController.class);	
	
	// This property is a list of supported email service providers in emailservice.properties
	@Value("${serviceproviderlist}")
	private String serviceProviderList;
	
	// This property is a AWS access key in emailservice.properties
	// To do item:
	// This property should be get in AWSEmailService.java, but somehow unable to get
	// Will fix it and move it back to AWSEmailService.java
	@Value("${aws_access_key}")
	private String AWS_ACCESS_KEY;
	// This property is a AWS secret key in emailservice.properties
	// To do item:
	// This property should be get in AWSEmailService.java, but somehow unable to get
	// Will fix it and move it back to AWSEmailService.java
	@Value("${aws_secret_key}")
	private String AWS_SECRET_KEY;
	// This property is a AWS SES endpoint in emailservice.properties
	// To do item:
	// This property should be get in AWSEmailService.java, but somehow unable to get
	// Will fix it and move it back to AWSEmailService.java
	@Value("${aws_ses_endpoint}")
	private String AWS_SES_ENDPOINT;
	// This property is SendGrid API key in emailservice.properties
	// To do item:
	// This property should be get in SendGridEmailService.java, but somehow unable to get
	// Will fix it and move it back to SendGridEmailService.java
	@Value("${sendgrid_api_key}")
	private String SENDGRID_API_KEY;	
	// This property is a sender email address in emailservice.properties
	// In order to access to AWS and SendGrid send email service, the send email needs to be
	// configured with them. So we have to use the configured email address
	// To do item:
	// It may be better to access this property from front end and pass it to back end, 
	// instead of back end
	@Value("${tester_from_address}")
	private String TESTER_FROM_ADDRESS;
	
	/**
	 * This method accepts the passed in Email object from
	 * from UI client, and then delegates it to either AWS SES or SendGrid to send email
	 * , and finally send an StatusResponse object back to UI
	 * @param email An Email object passed from UI client
	 * @return StatusResponse an object contains a flag whether it is success and message
	 */
	@RequestMapping(value = "/send", 
			method=RequestMethod.POST, headers="Accept=application/json")
	public @ResponseBody StatusResponse send(@RequestBody Email email) {	
		// To do item:
		// Validate the fields of email object on the server side although they are 
		// already validated on the client side (create validate method)
		//if (!validate(email)){
		//	return new StatusResponse(Constants.FAILURE, "Please fix all the fields on the form");
		//}
		// Validate properties in property files
		// To do item:
		// this class should validate for serviceproviderlist property only
		// other AWS and SendGrid properties should be validated in AWSEmailService.java
		// and SendGridEmailService.java only they can be get in those classes
		if (!validateProperties()) {
			return new StatusResponse(Constants.FAILURE, Constants.FAILURE_MESSAGE);
		}
		// Set the sender email to be the one already configured with AWS SES and SendGrid
		email.setFromAddress(TESTER_FROM_ADDRESS);	
		
		IEmailService emailService = null;
		StatusResponse response = null;
		
		// The email service provider list does not need to be two , can be all available
		// providers which we support		
		String[] serviceProviders = serviceProviderList.split(",");
		for (int i = 0; i < serviceProviders.length; i++) {
			if (serviceProviders[i].equals("AWS")){		
				logger.info("Now we are using AWS Email Service");
				emailService = new AWSEmailService(AWS_ACCESS_KEY, AWS_SECRET_KEY, AWS_SES_ENDPOINT, email);
			} else if (serviceProviders[i].equals("SendGrid")) {
				logger.info("Now we are using SendGrid Email Service");
				emailService = new SendGridEmailService(SENDGRID_API_KEY, email);
			} else {
				logger.error(serviceProviders[i] + " may not be correct or we have not supported it yet");
				return new StatusResponse(Constants.FAILURE, Constants.FAILURE_MESSAGE);
			}
			response = emailService.send();
			if (response.getSuccess()) {				
				break;
			}
			// With original understand on the requirement 'if one of the services goes down'
			// I go with check whether AWS or SendGrid SMTP server is down, and check this
			// specific exception. for example, for SendGrid, if response status code is 500,
			// it indicates the server is unavailable. So only for that exception, I will
			// switch to the other service provider. 
			// And then I think it will be better to do switch whenever it is not successful,
			// So the below is the code for the original approach
			// which is to do switch only for the specific exception
			//if (!response.getSuccess() && !response.getMessage().equals(Constants.EMAIL_SERVICE_IS_DOWN_MESSAGE)) {
			//	do switch
			//}
		}
		return response;		
	}	
	
	private boolean validateProperties() {
		if (serviceProviderList == null || serviceProviderList.length() == 0)  {
			logger.error("Please check emailservice.properties. 'serviceproviderlist' property can not be null");
			return false;		
		}
		if (AWS_ACCESS_KEY == null || AWS_ACCESS_KEY.length() == 0) {
			logger.error("Please check emailservice.properties. 'aws_access_key' property can not be null");
			return false;
		}
		if (AWS_SECRET_KEY == null || AWS_SECRET_KEY.length() == 0) {
			logger.error("Please check emailservice.properties. 'aws_secret_key' property can not be null");
			return false;
		}
		if (AWS_SES_ENDPOINT == null || AWS_SES_ENDPOINT.length() == 0) {
			logger.error("Please check emailservice.properties. 'aws_ses_endpoint' property can not be null");
			return false;
		}
		if (SENDGRID_API_KEY == null || SENDGRID_API_KEY.length() == 0) {
			logger.error("Please check emailservice.properties. 'sendgrid_api_key' property can not be null");
			return false;
		}
		if (TESTER_FROM_ADDRESS == null || TESTER_FROM_ADDRESS.length() == 0) {
			logger.error("Please check emailservice.properties. 'tester_from_address' property can not be null");
			return false;
		}
		if (!validateEmail(TESTER_FROM_ADDRESS)){
			logger.error("Please check emailservice.properties. 'tester_from_address' property need to be an email address");
			return false;
		}
		return true;		
	}
	
	private boolean validateEmail(String mail){
		String regex = "^(.+)@(.+)$";		 
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(mail);
		return matcher.matches();
	}

}
