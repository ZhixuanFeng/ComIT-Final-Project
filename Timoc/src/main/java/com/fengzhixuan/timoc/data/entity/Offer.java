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
    @JoinColumn(name = "player_id")
    private Player player;

    // using @ManyToOne to avoid querying offer object when querying card object.  card and offer is actually one-to-one
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(name = "indecks", nullable=false, columnDefinition = "TINYINT DEFAULT 0")
    private int indecks;

    @Column(name = "suit", nullable=false, columnDefinition = "TINYINT DEFAULT 0")
    private int suit;

    @Column(name = "rank_num", nullable=false, columnDefinition = "TINYINT DEFAULT 0")
    private int rank;

    @Column(name="attack", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int attack;

    @Column(name="block", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int block;

    @Column(name="heal", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int heal;

    @Column(name="mana", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int mana;

    @Column(name="draw", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int draw;

    @Column(name="taunt", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int taunt;

    @Column(name="revive", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int revive;

    @Column(name="aoe", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int aoe;

    @Column(name = "price", columnDefinition = "INT DEFAULT 0")
    private int price;

    @Column(name = "expiry_date")
    private Date expDate;

    public Offer(){}

    public Offer(Player player, Card card, int price)
    {
        this.player = player;
        this.card = card;
        this.indecks = card.getIndecks();
        this.price = price;
        this.indecks = card.getIndecks();
        this.suit = card.getSuit();
        this.rank = card.getRank();
        this.attack = card.getAttack();
        this.block = card.getBlock();
        this.heal = card.getHeal();
        this.mana = card.getMana();
        this.aoe = card.getAoe();
        this.draw = card.getDraw();
        this.revive = card.getRevive();
        this.taunt = card.getTaunt();
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

    public int getSuit()
    {
        return suit;
    }

    public void setSuit(int suit)
    {
        this.suit = suit;
    }

    public int getRank()
    {
        return rank;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
    }

    public int getAttack()
    {
        return attack;
    }

    public void setAttack(int attack)
    {
        this.attack = attack;
    }

    public int getBlock()
    {
        return block;
    }

    public void setBlock(int block)
    {
        this.block = block;
    }

    public int getHeal()
    {
        return heal;
    }

    public void setHeal(int heal)
    {
        this.heal = heal;
    }

    public int getMana()
    {
        return mana;
    }

    public void setMana(int mana)
    {
        this.mana = mana;
    }

    public int getDraw()
    {
        return draw;
    }

    public void setDraw(int draw)
    {
        this.draw = draw;
    }

    public int getTaunt()
    {
        return taunt;
    }

    public void setTaunt(int taunt)
    {
        this.taunt = taunt;
    }

    public int getRevive()
    {
        return revive;
    }

    public void setRevive(int revive)
    {
        this.revive = revive;
    }

    public int getAoe()
    {
        return aoe;
    }

    public void setAoe(int aoe)
    {
        this.aoe = aoe;
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
