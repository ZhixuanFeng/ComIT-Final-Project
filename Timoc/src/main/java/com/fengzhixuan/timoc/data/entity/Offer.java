package com.fengzhixuan.timoc.data.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "offer")
@SequenceGenerator(name = "offer_seq", sequenceName = "offer_seq", initialValue = 10000)
public class Offer
{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "offer_seq")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_name")
    private Player player;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(name = "indecks", columnDefinition = "TINYINT DEFAULT 0")
    private int indecks;

    @Column(name = "price", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int price;

    @Column(name = "expiry_date")
    private Date expDate;

    public Offer(Player player, Card card, int price)
    {
        this.player = player;
        this.card = card;
        this.indecks = card.getIndecks();
        this.price = price;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Card getCard()
    {
        return card;
    }

    public void setCard(Card card)
    {
        this.card = card;
    }

    public int getIndecks()
    {
        return indecks;
    }

    public void setIndecks(int indecks)
    {
        this.indecks = indecks;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public Date getExpDate()
    {
        return expDate;
    }

    public void setExpDate(Date expDate)
    {
        this.expDate = expDate;
    }
}
