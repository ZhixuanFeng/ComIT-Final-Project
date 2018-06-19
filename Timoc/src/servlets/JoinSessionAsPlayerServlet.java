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
		String respond = "";
		
		// check if user has login
		User user = (User)request.getSession().getAttribute("User");
		if (user == null)
		{
			return; // do nothing, can't redirect or forward to login page
		}
		
		int code = GameSession.codeToInt((String) request.getAttribute("code"));
		
		// check if code is valid
		if (GameSession.isCodeValid(code))
		{
			respond = "Invalid Code";
			return;
		}
		
		// check if game session exists
		if (!GameSession.gameSessionExist(code))
		{
			respond = "Game Sesseion Does not Exist";
			return;
		}
		
		// get game session
		GameSession gameSession = GameSession.getGameSessionByCode(code);
		
		// check if game session is full
		if (gameSession.isFull())
		{
			respond = "Game Session is Full";
			return;
		}
		
		// add user to game session
		gameSession.addUser(user);
		user.setConnectedGameSession(code);
		respond = "Connection success";
		
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
