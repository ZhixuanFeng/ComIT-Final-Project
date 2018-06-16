<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Timoc</title>
<h3>Welcome To Timoc</h3>
<p>Today is <%= new java.util.Date().toString() %></p>
</head>
<%
	String bgColor = request.getParameter("COLOR");
	
	if (bgColor == null)
	   bgColor = "WHITE";
%>
<body BGCOLOR="<%= bgColor%>">
  <form>
  <input type="submit" value="Display">
  </form>
</body>
</html>