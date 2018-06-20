package beans;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class User implements HttpSessionBindingListener
{
    public User(int id, String username)
	{
		this.id = id;
		this.username = username;
		connectedGameSessionCode = -1; // -1 meaning not connected to any game session
	}

	// All logins.
    private static Map<User, HttpSession> logins = new HashMap<User, HttpSession>();
    
    private Integer id;
    private String username;
    private int connectedGameSessionCode;  // use int instead of reference to reduce memory usage
    
    
    
    @Override
    public boolean equals(Object other)
    {
        return (other instanceof User) && (id != null) ? id.equals(((User) other).id) : (other == this);
    }

    @Override
    public int hashCode()
    {
        return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) 
    {
        HttpSession session = logins.remove(this);
        if (session != null)
        {
        	System.out.println("Logging in when account is already online, username: " + username);
            session.invalidate();
        }
        logins.put(this, event.getSession());
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event)
    {
        logins.remove(this);
    }

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public int getConnectedGameSession()
	{
		return connectedGameSessionCode;
	}

	public void setConnectedGameSession(int connectedGameSession)
	{
		this.connectedGameSessionCode = connectedGameSession;
	}
}
