package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.GameSession;

/**
 * Servlet implementation class GenerateConnectCode
 */
@WebServlet("/generateSession")
public class GenerateSessionServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GenerateSessionServlet()
    {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.sendRedirect("home");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		GameSession gameSession;
		try
		{
			gameSession = new GameSession();
			String code = gameSession.getCodeString();
			response.getWriter().append("Your Connect Code Is: ").append(code);
		} catch (Exception e)
		{
			e.printStackTrace();
			request.getRequestDispatcher("/jsp/errorPage.jsp").forward(request, response);
		}
	}

}
