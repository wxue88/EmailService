<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<link rel="stylesheet" type="text/css" media="screen" href='<c:url value="/resources/css/jquery-ui/pepper-grinder/jquery-ui-1.8.16.custom.css"/>'/>
	<link rel="stylesheet" type="text/css" media="screen" href='<c:url value="/resources/css/style.css"/>'/>
	<script type='text/javascript' src='<c:url value="/resources/js/jquery-1.6.4.min.js"/>'></script>
	<script type='text/javascript' src='<c:url value="/resources/js/jquery-ui-1.8.16.custom.min.js"/>'></script>
	<script type='text/javascript' src='<c:url value="/resources/js/util.js"/>'></script>	
	<title>Email Service Prototype</title>
	
	<script type='text/javascript'>
	$(function() {
		init();
	});
	
	function init() {
		$('input:button').button();
		$('#submit').button();
		
		$('#emailForm').submit(function(event) {
			event.preventDefault();
			if (validate()){
				$.postJSON('<c:url value="/email/send"/>', {					
					toAddress: $('#toAddress').val(),
					ccAddress: $('#ccAddress').val(),
					bccAddress: $('#bccAddress').val(),
					subject: $('#subject').val(),
					content: $('#content').val(),				
					},
					function(result) {
						if (result.success == true) {
							dialog('Success', result.message);
						} else {
							dialog('Failure', result.message);
						}
				});
			}
		});		
		
		$('#reset').click(function() {
			clearForm();
			dialog('Success', 'Fields have been cleared!');
		});
	}
	
	function validateEmail(mail) {
		var emailPattern = /^[a-zA-Z0-9._~`!#$%\^&+={}|\/-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
		return emailPattern.test(mail);
	}
	
	function validate() {
		var to = $('#toAddress').val();
		var cc = $('#ccAddress').val();
		var bcc = $('#bccAddress').val();
		// Validate To field
		if ($.trim(to) === "") {
			dialog('Failure', "To field can not be empty.")
			return false;
		}
		var toEmailIds = to.split(",");
		for (var i = 0; i < toEmailIds.length; i++) {
			if (!validateEmail($.trim(toEmailIds[i]))) {
				dialog('Failure', "To Address is not valid.")
				return false;
			}
		}
		// Validate CC field
		if ($.trim(cc) !== "") {
			var ccEmailIds = cc.split(",");
			for (var i = 0; i < ccEmailIds.length; i++) {
				if (!validateEmail($.trim(ccEmailIds[i]))) {
					dialog('Failure', "Cc Address is not valid.")
					return false;
				}
			}			
		}
		// Validate BCC field
		if ($.trim(bcc) !== "") {
			var bccEmailIds = bcc.split(",");
			for (var i = 0; i < bccEmailIds.length; i++) {
				if (!validateEmail($.trim(bccEmailIds[i]))) {
					dialog('Failure', "BCc Address is not valid.")
					return false;
				}
			}			
		}	
		return true;
	}
	function dialog(title, text) {
		$('#msgbox').text(text);
		$('#msgbox').dialog( 
				{	
					title: title,
					modal: true,
					buttons: {"Ok": function()  {
						$(this).dialog("close");} 
					}
				});
	}
	
	function clearForm() {
		$('#toAddress').val('');
		$('#ccAddress').val('');
		$('#bccAddress').val('');		
		$('#subject').val('');
		$('#content').val('');
	}
	</script>
</head>

<body>
	<div class="wrapper">
		<div class="heading">
			<img src='<c:url value="/resources/images/KQED_fireworks.jpg"/>' width ="170px" height="100px" align="left">
			Marketing Strategy			
		</div>
	</div>	
	<hr>	
	<div class="navigation">
	<H3>
		<a href="http://www.kqed.org/"> Home </a> | 
		<a href="http://www.kqed.org/radio/"> Radio </a> |
		<a href="http://www.kqed.org/news/"> News </a> |
	    <a href="http://ww2.kqed.org/arts/"> Arts </a> |
	    <a href="http://www.kqed.org/food/"> Food </a> | 
	    Marketing | 
	    <a href="http://www.kqed.org/about/"> About Us </a>	    
	</H3>
	</div>
	<hr>
	<div class="content">	
	   <h2 id='banner'>Compose Email</h2>
		<form id='emailForm'>  			
  			<p>
  				<label for='toAddress'>To :</label>
  				<input type='text' id='toAddress'/>
  				
  			</p>
  			<p>
  				<label for='ccAddress'>CC :</label>
  				<input type='text' id='ccAddress'/> 
  				 				
  			</p>
  			<p>
  				<label for='bccAddress'>BCC :</label>
  				<input type='text' id='bccAddress'/>   				 				
  			</p>
  			<p>
  				<label for='subject'>Subject :</label>
  				<input type='text' id='subject'/>   						
  			</p>
  			<p>  			
  				<label for='textBody' style="display:break">Message</label>
  				<textarea name="content" id="content"></textarea>
  			</p>
  			<input type='button' value='Reset' id='reset' />
			<input type='submit' value='Send' id='submit'/>
		</form>
	</div>
		
	<div id='msgbox' title='' style='display:none'></div>	
	
	<div class="footer">
		This is an email service prototype.
		<img src='<c:url value="/resources/images/kqed_logo_color.jpg"/>' width ="50px" height="20px" align="right">
	</div>		
</body>
</html>