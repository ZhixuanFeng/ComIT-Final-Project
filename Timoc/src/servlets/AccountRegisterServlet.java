package servlets;

import java.io.IOException;
import java.sql.Connection;

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
		}
	}

}
