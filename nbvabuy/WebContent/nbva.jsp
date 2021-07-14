<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Olympus FIS NBVA Secure Get Asset Application</title>
<!--  <link href="includes/appstyle.css" rel="stylesheet" type="text/css" /> 

<style><%@include file="includes/css/reports.css"%></style>
<style><%@include file="includes/css/table.css"%></style>
-->
<style><%@include file="includes/css/header.css"%></style>
<style><%@include file="includes/css/menu.css"%></style>
</head>
<body>
<%@include  file="includes/header.html" %>


<div style="padding-left:20px">
  <h3>Olympus FIS Authentication Application</h3>
</div>

<BR>

<h5>Please enter your Windows credentials to gain access to the apps.</h5>



<h5>Note: <font color="red">Requires Javascript to be enabled.</font> <BR>
 
</h5>

<BR>
 

	<form name="actionform" method="POST" action="nbvaauth">
<!--  <form name="actionform" method="POST" action="adauth">  -->
<BR>


<table class="a" width="40%"  border="1" cellpadding="1" cellspacing="1">
<tr> <th class="theader"> Olympus FIS App Authentication</th> </tr>
  <tr>
    <td class="table_cell">
    <!--  Inner Table -->
    <table class="a" width="100%"  border="1" cellpadding="1" cellspacing="1">
  <tr>
  <td width="20" valign="bottom"> <b>Username:</b> </td> 
  <!--<td width="20" valign="bottom">   -->
     <%  //out.println("<input name=\"startDate\" id=\"date2\" type=\"text\" value=\"Click for Calendar\" onclick=\"pureJSCalendar.open('yyyy-MM-dd', 20, 30, 7, '2017-1-1', '2025-12-31', 'date2', 20)\"   />" );
     %>
     <td><input type="text" name="username" /></td>
     
  </td>
  </tr>
  
   <tr>
  <td width="20" valign="bottom"> <b>Password:</b> </td> 
  <!--   <td width="20" valign="bottom">  -->
      
   <td><input type="password" name="password"/ ></td>
   
  </td>
  </tr>
  <tr>
   <td  valign="bottom" class="a">
	<div id='ajaxDiv'> </div>
	</td>
	 <td> 
    <INPUT type="submit" value="Submit">  
    </td>
	
  </tr>
  </table>

</table>

 </form>


</body>
</html>