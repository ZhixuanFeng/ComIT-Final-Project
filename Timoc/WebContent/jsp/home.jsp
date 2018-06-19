<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Timoc</title>
</head>
<h3>Welcome To Timoc</h3>
<p><%= new java.util.Date().toString() %></p>
<body>
  <form action="generateSession" method="get"><input type="submit" value="Display"></form>
  <form action="login" method="get"><input type="submit" value="Login"></form>
  <form action="register" method="get"><input type="submit" value="Create new account"></form>
</body>
</html>