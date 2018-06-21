package beans;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class Display implements HttpSessionBindingListener
{
	public Display(String iPAddress, GameSession gameSession)
	{
		IPAddress = iPAddress;
		this.gameSession = gameSession;
	}

	String IPAddress;
	GameSession gameSession;

	@Override
    public void valueUnbound(HttpSessionBindingEvent event)
    {
    	gameSession.removeDisplay(this);
    }
	
	public String getIPAddress()
	{
		return IPAddress;
	}

	public void setIPAddress(String iPAddress)
	{
		IPAddress = iPAddress;
	}

	public GameSession getGameSession()
	{
		return gameSession;
	}

	public void setGameSession(GameSession gameSession)
	{
		this.gameSession = gameSession;
	}
}
