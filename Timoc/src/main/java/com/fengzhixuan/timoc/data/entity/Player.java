package com.fengzhixuan.timoc.data.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="player")
public class Player
{
    @Id
    @Column(name="id")
    private long id;

    @Column(name="name", nullable=false, length=31)
    private String name;

    @Column(name="level", nullable=false, columnDefinition="TINYINT DEFAULT 1")
    private int level;

    @Column(name="gold", nullable=false, columnDefinition="INT DEFAULT 0")
    private int gold;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = false)
    private CardCollection cardCollection;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Offer> offers = new ArrayList<>();

    public Player () {}

    public Player (User user)
    {
        this.name = user.getUsername();
        this.level = 0;
        this.user = user;
        user.setPlayer(this);
    }

    @Override
    public String toString()
    {
        return "Player_In_Deck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getGold()
    {
        return gold;
    }

    public void setGold(int gold)
    {
        this.gold = gold;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public CardCollection getCardCollection()
    {
        return cardCollection;
    }

    public void setCardCollection(CardCollection cardCollection)
    {
        this.cardCollection = cardCollection;
    }

    public List<Offer> getOffers()
    {
        return offers;
    }

    public void setOffers(List<Offer> offers)
    {
        this.offers = offers;
    }
}
