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
		this.id = (long) id;
		this.username = username;
	}

	// All logins.
    private static Map<User, HttpSession> logins = new HashMap<User, HttpSession>();
    
    private Long id;
    private String username;
    
    
    
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

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
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
}
