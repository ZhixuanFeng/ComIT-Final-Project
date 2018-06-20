<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Register</title>
</head>
<h3>Register</h3>
<body>
<form action="register" method="post">
	Username(4-20 characters):<br>
	<font size="3" color="red">	${UsernameProblem}</font>
  	<input type="text" name="username" maxlength="21" id="username" value="${EnteredUsername}"><br>
	Password(8-32 characters):<br>
	<font size="3" color="red">	${PasswordProblem}</font>
 	<input type="password" name="password" maxlength="32" id="password"><br>
	Confirm password:<br>
 	<input type="password" name="password_re" maxlength="32" id="password_re"><br>
	E-mail Address(Password recovery not yet implemented):<br>
	<font size="3" color="red">	${EmailProblem}</font>
 	<input type="text" name="email" id="email" value="${EnteredEmail}"><br><br>
 	<input type="submit" value="Create my Account"> <!-- TODO: Add a link to login page -->
</form>
</body>
</html>