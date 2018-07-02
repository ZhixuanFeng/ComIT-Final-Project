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

    @Column(name = "price", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int price;

    @Column(name = "expiry_date")
    private Date expDate;

    @Column(name = "buyer_id")
    private long buyerID;

    public Offer(Player player, Card card, int price)
    {
        this.player = player;
        this.card = card;
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

    public long getBuyerID()
    {
        return buyerID;
    }

    public void setBuyerID(long buyerID)
    {
        this.buyerID = buyerID;
    }
}
