package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.GameSession;
import beans.User;

/**
 * Servlet implementation class ExitSessionAsPlayer
 */
@WebServlet("/exitSessionAsPlayer")
public class ExitSessionAsPlayerServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExitSessionAsPlayerServlet() 
    {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		exitSession((User) request.getSession().getAttribute("user"));
		response.sendRedirect("home");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	// removes user from its currently connected session
	public static void exitSession(User user)
	{
		// remove user from connected game session
		if (user.getConnectedGameSession() != -1)
		{
			GameSession gameSession = GameSession.getGameSessionByCode(user.getConnectedGameSession());

			System.out.println("Disconnecting user " + user.getUsername() + " from " + gameSession.toString());
			// code for removing user from the game
			
			gameSession.removeUser(user);
			user.setConnectedGameSession(-1);
		}
	}
}
