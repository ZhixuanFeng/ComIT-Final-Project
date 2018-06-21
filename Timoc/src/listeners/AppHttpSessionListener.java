package listeners;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import beans.Display;

public class AppHttpSessionListener implements HttpSessionListener
{
	@Override
	public void sessionCreated(HttpSessionEvent event) 
	{
		event.getSession().setMaxInactiveInterval(15 * 60); 
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event)
	{
		// session destroyed
		HttpSession sesson = event.getSession();
		if (sesson.getAttribute("display") != null)
		{
			System.out.println(((Display)sesson.getAttribute("display")).toString() + " is disconnected.");
		}
		System.out.println(sesson.toString() + " is destroyed.");
	}
}
