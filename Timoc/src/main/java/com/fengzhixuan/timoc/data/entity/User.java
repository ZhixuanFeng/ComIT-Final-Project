package com.fengzhixuan.timoc.data.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Entity
@Table(name="user")
@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", initialValue = 10000)
public class User
{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private long id;

    @Column(name="username", nullable=false, length=20)
    @Length(min = 4, max = 20, message = "*Your username must be within 4 to 20 characters")
    @NotEmpty(message = "*Please provide a username")
    private String username;

    @Column(name="password", nullable=false)
    @Length(min = 6, message = "*Your password must be at least 6 characters")
    @NotEmpty(message = "*Please provide a password")
    private String password;

    @Column(name="email", nullable=false)
    @Pattern(regexp = "^[a-zA-Z0-9.\\-\\/+=@_ ]*$", message = "*Your email contains invalid character(s).")
    @Email(message = "*Please provide a valid email")
    @NotEmpty(message = "*Please provide an email")
    private String email;

    @Column(name="enabled", nullable=false, columnDefinition="BOOLEAN DEFAULT TRUE")
    private boolean enabled;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = false)
    private Player player;

    public User(){}

    public long getId()
    {
        return id;
    }

    public void setId(long id)
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

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public Set<Role> getRoles()
    {
        return roles;
    }

    public void setRoles(Set<Role> roles)
    {
        this.roles = roles;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }
}
