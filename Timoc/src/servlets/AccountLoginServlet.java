package servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ApplicationDAO;

/**
 * Servlet implementation class AccountLoginServlet
 */
@WebServlet("/login")
public class AccountLoginServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AccountLoginServlet()
    {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.getRequestDispatcher("/jsp/accountLogin.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		ApplicationDAO dao = new ApplicationDAO();
		Connection connection = (Connection)getServletContext().getAttribute("dbconnection");
		int returnCode = dao.validateUser(username, password, connection);
		
		switch (returnCode)
		{
		case 0:  // invalid
			System.out.println("Login failed - username does not exist: " + username);
			//response.getWriter().write("Invalid username/password");
			request.setAttribute("returnMessage", "Invalid username/password, please try again");
			request.getRequestDispatcher("/jsp/accountLogin.jsp").forward(request, response);
			break;
		case 1:  // valid
			System.out.println("Login success, username " + username);
			HttpSession session = request.getSession();
			session.setAttribute("username", username);
			request.getRequestDispatcher("/jsp/home.jsp").forward(request, response);
			break;
		case 2:  // already online
			System.out.println("Login failed - user already online: " + username);
			//response.getWriter().write("User already online");
			// TODO logout the already online account and let this login attempt go through
			break;
		default:
			System.out.println("Unknown error validating user " + username);
			// TODO redirect to error page
			break;
		}
		
//		// forward the return code and control to jsp
//		request.setAttribute("returnCode", returnCode);
//		request.getRequestDispatcher("/jsp/AccountLogin.jsp");
	}

}
