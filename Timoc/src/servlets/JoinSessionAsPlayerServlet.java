package servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ApplicationDAO;

/**
 * Servlet implementation class JoinSessionAsPlayer
 */
@WebServlet("/joinSessionAsPlayer")
public class JoinSessionAsPlayerServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JoinSessionAsPlayerServlet()
    {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String respond = "Connection success";
		
		Connection connection = (Connection)getServletContext().getAttribute("dbconnection");
		ApplicationDAO dao = new ApplicationDAO();
		
		String inputedCode = request.getParameter("code").toUpperCase();
		int row = dao.connectToSession(connection, inputedCode);
		if (row == 0)
		{
			respond = "Invalid Code";
		}
		else if (row < 0)
		{
			respond = "Connection Failed";
		}
		
		response.getWriter().append(respond);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
