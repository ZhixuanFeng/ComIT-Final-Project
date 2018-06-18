<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Register</title>
</head>
<body>
<form action="register" method="post">
	Username(4-20 characters):<br>
  	<input type="text" name="username" maxlength="21" id="username"><br>
	Password(8-32 characters):<br>
 	<input type="password" name="password" id="password"><br>
	E-mail Address(Password recovery not yet implemented):<br>
 	<input type="text" name="email" id="email"><br><br>
	<input type="submit" value="Submit">
</form>
</body>
</html>