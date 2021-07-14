<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@page import="java.io.OutputStream"%>   
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.time.*"%>
<%@ page import="javax.servlet.*"%>
<%@ page import="com.olympus.dateutil.DateUtil"%>
<%@ page import="com.olympus.olyutil.*"%>
<%@ page import = "java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import = "javax.servlet.http.*, com.olympus.olyutil.*" %>
    
    <%@ page import="java.time.format.DateTimeFormatter"%>
    <%@ page import="java.sql.*"%>
    
    <% 
	String title =  "Olympus NBVA Buyout App"; 	 
	ArrayList<String> tokens = new ArrayList<String>();
	String formUrl =  (String) session.getAttribute("formUrl");
	String commDate = "";
	String currDate = java.time.LocalDate.now().toString();
	//System.out.println("** CurrDate=" + currDate);
	 /*
 
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	String newDate = DateUtil.getNewEffectiveDate("2020-03-10");
 
	System.out.println("*** NewEffectiveDate=" + newDate );
	*/
	
	String authToken =  (String) session.getAttribute("authToken");
	String username =  (String) session.getAttribute("username");
	
	//System.out.println("***!!!*** authToken=" + authToken + "--");
	String dispatchJSPAuthError = "auth_error.jsp";
	
	if (  Olyutil.isNullStr( authToken ) || ( ! authToken.equals("true")) ) {
		System.out.println("***!!!*** Please log in to authorize use of the app.");
		request.getRequestDispatcher(dispatchJSPAuthError).forward(request, response);
	}  
		
		
	 
	session.setAttribute("authToken", "false");
	authToken =  (String) session.getAttribute("authToken");
	
	
	//System.out.println("***After*** authToken=" + authToken + "--");
%>

    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
 
<!--  	
http://cvyhj3a27:8181/nbvaupdate/nbvaupdate?id=101-0010311-004&eDate=2020-03-10&boDate=2020-03-31&invoice=123123123

101-0009442-019-->

<title><%=title%></title>


<!--  <link href="includes/appstyle.css" rel="stylesheet" type="text/css" /> 

<style><%@include file="includes/css/reports.css"%></style>
<style><%@include file="includes/css/table.css"%></style>
-->
<style><%@include file="includes/css/header.css"%></style>
<style><%@include file="includes/css/menu.css"%></style>
 <link rel="stylesheet" href="includes/css/calendar.css" />
    <script type="text/javascript" src="includes/scripts/pureJSCalendar.js"></script>



<!-- ********************************************************************************************************************************************** -->


<!-- ********************************************************************************************************************************************** -->

<script language="javascript" type="text/javascript">
 
//Browser Support Code
function ajaxFunction(){
	var ajaxRequest;  // The variable that makes Ajax possible!
	
	try{
		// Opera 8.0+, Firefox, Safari
		ajaxRequest = new XMLHttpRequest();
	} catch (e){
		// Internet Explorer Browsers
		try{
			ajaxRequest = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try{
				ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e){
				// Something went wrong
				alert("Your browser broke!");
				return false;
			}
		}
	}
	// Create a function that will receive data sent from the server
	ajaxRequest.onreadystatechange = function(){
		if(ajaxRequest.readyState == 4){
			var ajaxDisplay = document.getElementById('ajaxDiv');
			ajaxDisplay.innerHTML = ajaxRequest.responseText;
		// document.actionform.actiontypeprogram.value = ajaxRequest.responseText;

		}
	}

//	var atype = document.actionform.getElementById('actiontype').value;
	var atype = document.actionform.actiontype.value;
	var date2 = document.actionform.startDate.value;
	//alert("atype=" + atype);
	
	//alert("date2=" + date2);
	//var queryString = "?atype=" + atype + "&wpm=" + wpm + "&ex=" + ex;
	var queryString = "/webreport/ajaxIL.jsp?atype=" + atype + "&date2=" + date2;
	

	  
	//alert("QS=" + queryString);
	//ajaxRequest.open("GET", "ajax.jsp" + queryString, true);
	ajaxRequest.open("POST", queryString, true);
	ajaxRequest.send(); 
}

 
</script>





<!-- ********************************************************************************************************************************************************* -->

</head>
<body>
    
    
 <%@include  file="includes/header.html" %>




<div style="padding-left:20px">

</div>

<BR>
<h5>This page will provide an on-demand NBVA Asset List Report.</h5>
<h5>Enter a valid contract ID and get a list of assets associated with that contract..</h5>




 
</h5>

<BR>


<form id="actionform" name="actionform" method="post" action="getdateparam"   > 

<BR>


<table class="a" width="40%"  border="1" cellpadding="1" cellspacing="1">
<tr> <th class="theader"> <%=title%></th> </tr>
  <tr>
    <td class="table_cell">
    <!--  Inner Table -->
    <table class="a" width="100%"  border="1" cellpadding="1" cellspacing="1">
  <tr>
 
  <td width="60" valign="bottom"> <b>Enter Contract Number:</b> </td> 
  <td width="20" valign="bottom">  
     <% // out.println("<input name=\"startDate\" id=\"date2\" type=\"text\" value=\"Click for Calendar\" onclick=\"pureJSCalendar.open('yyyy-MM-dd', 20, 30, 7, '2017-1-1', '2025-12-31', 'date2', 20)\"   />" );
     %>
    <!--  <CENTER>  <input name="id" type="text"  value="101-0009442-019" /> </CENTER> 
      <CENTER>  <input id="id" class="idbox" name="id" type="text"  value="101-0010311-004" /> </CENTER>
      -->
      
      <CENTER>  <input name="id" type="text"    /> </CENTER> 
      
  </td>
  </tr>
 
  <!-- ********************************************************************************************************************************************************* -->
  <!-- 
   <tr>
  <td width="60" valign="bottom"> <b>Effective Date: ( <font color="red">Later than: <%=currDate%> </font>)</b> </td> 
  <td width="20" valign="bottom"> 
   <center>
   <% out.println("<input name=\"eDate\" id=\"date2\" type=\"text\" value=\" " +     commDate  + "  \"   onclick=\"pureJSCalendar.open('yyyy-MM-dd', 20, 30, 7, '2017-1-1', '2025-12-31', 'date2', 20)\"   />" );
     %>
   </center>
  
   </td>
  </tr>
 -->
  <!-- ********************************************************************************************************************************************************* -->

      <!-- ********************************************************************************************************************************************************* -->

     
     <!-- ********************************************************************************************************************************************************* -->
     
     
     <tr>
  <td width="20" valign="bottom" COLSPAN="2">  &nbsp</td> 
   
  </tr>
  
  <!-- ********************************************************************************************************************************************************* -->
  
  
  <tr>
   <td  valign="bottom" class="a" COLSPAN="2" >
	<div id='ajaxDiv'> </div>
	<!--  </td>
	
	   <td> 
	  --> 
	 <CENTER> 
    <INPUT type="submit" value="Get Effective Date" class="idSubmit" name="submitID" id="submitID">  
     <input type="reset" value="Clear"/>
     </CENTER>
     </td>  
	
  </tr>
  </table>

</table>

 </form>
<h5>If you require access to the reports, please contact: John.Freeh@olympus.com</h5>
<h5>Note: <font color="red">Requires Javascript to be enabled.</font> <BR>

 <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/jquery-validation@1.19.2/dist/jquery.validate.js"></script>
 
    <script type="text/javascript" src="includes/js/validateID.js"></script>
    
    
    <script>
    /*
    $(document).ready(function() {
        $('#submitID').click(function(e) {
        	e.preventDefault();
        	var id = $('input[name=id]').val();
        	alert('ID=' + id);
        	$.ajax({
        		url : "fetch.jsp",
    			type : 'POST',
    			data : {
    				id : id
    			},
    			success : function(data) {
    				$('#dateVal').val(data);
    				
    				var idRtn = data;
    				alert('idRtn=' + idRtn);
    			}
        		
        		
        		
        		
        		
        	});
        });
    });
    
    */
    </script>
</body>
</html>