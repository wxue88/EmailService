Description:
This is an email service prototype application to send email using Amazon Simple Email 
Service and/or SendGrid service.


Project Requirement:
Create a service that accepts the necessary information and sends emails. It should provide 
an abstraction between two different email service providers. If one of the services goes down,
your service can quickly failover to a different provider without affecting your customers. 
The two service providers can be chosen by the following four providers:
1.SendGrid
2.Mailgun
3.Mandrill
4.Amazon SES


Solution Overview:
1.This application has chosen Amazon SES and SendGrid as the two service providers
per the requirement.
2.From the design point of view, this application defines an email service interface which 
all email service providers can implement. New service provider can be easily added by 
implementing the interface. The factory design pattern can be considered to use
to get the email service provider.(please see the to do items)
3.This application does not only support the primary/secondary service providers failover. It
can attempt failovers among multiple service providers. It will be run in a loop of all
supported service providers, once the service provider sends email successfully, it will exit
the loop and return response to the client, otherwise, it will go to the next provider.
4.The supported service provider list is configured in a property file and so it is very convenient 
for system admin to make change such as add more supported provider or remove a provider since 
the provider no longer offers free service.
5.This application will do failover if the service provider fails to send email due to any
exception. This way the application can achieve high availability and more robust.
With the original understand of 'If one of the services goes down', I assume it means Amazon
SES and SendGrid SMTP server is down. Need to double check with customer. If so, it means if
the service returns the exception which matches the server is unavailable(e.g.for SendGrid,response status
code is 500), then we failover to the next provider. For other service exception, we do not
do failover. It seems not logic. For example, for a test address configured in Amazon SES sandbox,
the test address can only send email to the email addresses which are verified with Amazon SES, 
otherwise it will be rejected. This EmailReject exception is not server down exception. However
it can be sent by SendGrid since as long as the sender address is configured, he can send to any 
email address.  


Design and Technologies:
This application is more like an back end application with very light front end.
Back end: Spring MVC, Java, AWS SDK, SendGrid-java library
Front end: JQuery, HTML, css

controller package:
EmailServiceController
-receive the email related HTTP requests from UI, delegate them to the proper service provider
to handle the email service and then return the status of whether email service executes
successful back to UI.

service package:
IEmailService
-the interface. 'I' indicates Interface.
AWSEmailService
-implement IEmailService, handle email service using AWS SDK.
SendGridEmailService
-implement IEmailService, handle email service using SendGrid-java.
 
model package:
Email 
-an object containing from, to, cc, bcc, subject, content fields pass from UI.
StatusResponse 
-an object containing a flag whether send email successful and display message pass back to UI.

util package:
Constants
-define hardcoded value such as success status, success message.

properties file:
emailservice.properties
-define configurable properties like serviceproviderlist, AWS access key,...

logging:
log4j

Deployment and Testing
1.This application is deployed to AWS. Here is the link:
http://sample-env.5pqza83kpn.us-west-2.elasticbeanstalk.com/

2.The service providers are configurable with property 'serviceproviderlist' in emailservice.properties.
The value is set to 'AWS,SendGrid'. So the primary is set to AWS.

2.The sender address:emailservicetester@gmail.com has been configured and verified with
Amazon SES and SendGrid in order to use the two service providers. It is configured to emailservice.properties.
The UI does not provide the from field.

3.Testing
emailservicetester@gmail.com currently is still in AWS sandbox. So emailservicetester@gmail.com can
only send to the email addresses which are verified with AWS. I created the following email addresses
and verified them with AWS. The password of these four addresses is kqed1111. 
a)etester20161@gmail.com
b)etester20162@gmail.com
c)etester20163@gmail.com
d)etester20164@gmail.com
i)Amazon SES test
On UI, you can put the above four addresses to To/Cc/Bcc. The email should be sent via
Amazon SES. On gmail account, it will show 'via amazonses.com or via Sendgrid.net. You can log in
to the above addresses and verify you get email and also verify it is sent via which service provider. 

ii)SendGrid test
On UI, you can put any email address to To/Cc/Bcc or combining the above four addresses. Once To/Cc/Bcc
contains the address which is not verified with AWS. AWS will reject email. And it will failover to
use SendGrid. You can log in your email address to verify you get mail. If your email address is gmail,
you can also verify it is sent via which service provider. If it is not gmail, you can verify you get 
email only. Like yahoo email, it won't show via which service provider.


Due to the time constraint, there are issues not resolved yet and there are improvements. 
To do:
Issues:
1.Need to fix the issue that unable to get property values in service classes. Current 
alternative way is get those values in controller class, and then pass them through
service constructor to service. And thus make the code look a bit messy in controller class. 
2.For security, add validate email fields in controller class at back end although they are verified
on front end already.
3.Finish the remaining unit tests classes and upload them. 
4.The current UI is very rough. Need to fix it.
5.Move some hardcoded status messages in controller to Constants.

Improvements:
1.May consider to use factory pattern to get specific service provider.
2.Apply the new front end technology like angular js to implement a better UI. 
3.May create an utilitiy class to handle validate function or other functions.

