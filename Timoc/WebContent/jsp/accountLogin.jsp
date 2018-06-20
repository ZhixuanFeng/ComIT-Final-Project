<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
</head>
<h3>Login</h3>
<body>
<form action="login" method="post">
	Username:<br>
  	<input type="text" name="username" maxlength="21" id="username"><br>
	Password:<br>
 	<input type="password" name="password" id="password"><br>
	<font size="3" color="red">	${returnMessage}</font><br>
	<input type="submit" value="Login"> <!-- TODO: Add a link to register page -->
</form>
</body>
</html>