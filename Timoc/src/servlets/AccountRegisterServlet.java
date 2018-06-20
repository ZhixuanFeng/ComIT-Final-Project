package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Account;
import beans.User;
import dao.ApplicationDAO;

/**
 * Servlet implementation class AccountRegisterServlet
 */
@WebServlet("/register")
public class AccountRegisterServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AccountRegisterServlet()
    {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.getRequestDispatcher("/jsp/accountRegister.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
{
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		
		// check if username/password/email is valid, such as length
		String usernameProblem = checkUsername(username);
		String passwordProblem = checkPassword(password);
		boolean shouldRetry = false;
		if (usernameProblem != null)
		{
			request.setAttribute("UsernameProblem", usernameProblem + "<br>");
			shouldRetry = true;
		}
		if (passwordProblem != null)
		{
			request.setAttribute("PasswordProblem", passwordProblem + "<br>");
			shouldRetry = true;
		}
		else if (password.equals(request.getParameter("password_re")))  // TODO: learn how to do this in front end
		{
			request.setAttribute("PasswordProblem", "The two passwords entered do not match, please try again." + "<br>");
		}
		if (!isValidEmail(email))
		{
			request.setAttribute("EmailProblem", email + " is not an email address." + "<br>");
			shouldRetry = true;
		}
		
		if (shouldRetry)
		{
			// direct the user back to register page to try different username/password/email
			request.setAttribute("EnteredUsername", username);
			request.setAttribute("EnteredEmail", email);
			request.getRequestDispatcher("/jsp/accountRegister.jsp").forward(request, response);
		}
		else 
		{
			// check if username or email is already exist, and actually register the user
			Account newAccount = new Account(username, password, email);
			
			ApplicationDAO dao = new ApplicationDAO();
			Connection connection = (Connection)getServletContext().getAttribute("dbconnection");
			int row = dao.registerUser(newAccount, connection);
			
			if (row > 0)
			{
				System.out.println("New account registerred: " + username);
				int id = dao.getAccountID(username, connection);
				User user = new User(id, username);
				HttpSession session = request.getSession();
				session.setAttribute("user", user);
				//request.getRequestDispatcher("/jsp/home.jsp").forward(request, response);
				response.sendRedirect("home");
				System.out.println("Login success, username " + username);
			}
			else if (row == 0)
			{
				System.out.println("Error registerring account: " + username);
			}
			else if (row == -1)
			{
				System.out.println("Failed registerring account, username already exists: " + username);
			}
			else if (row == -2)
			{
				System.out.println("Failed registerring account, email already exists: " + email);
			}
			else
			{
				System.out.println("Unknown error occurred when registerring account: " + username);
				request.getRequestDispatcher("/jsp/errorPage.jsp").forward(request, response);
			}
		}
	}

	// returns null if valid, otherwise return why it's invalid
	String checkUsername(String username)
	{
		// check length
		if (username.length() < 4)
		{
			return "Username too short.";
		}
		
		// check illegal words
		String uppercase = username.toUpperCase();
		if (uppercase.contains("ADMIN") || uppercase.contains("MODERATOR"))
		{
			return "Username contains illegal word.";
		}
		
		return null;
	}
	
	// returns null if valid, otherwise return why it's invalid
	String checkPassword(String password)
	{
		if (password.length() < 8)
		{
			return "Password too short.";
		}
		return null;
	}
	
	// check if email is a valid email address
	boolean isValidEmail(String email)
	{
		String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
		Pattern pattern;
		Matcher matcher;
		pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
