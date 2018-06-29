package com.fengzhixuan.timoc.data.entity;

import com.fengzhixuan.timoc.data.enums.CardOwnerType;
import com.fengzhixuan.timoc.data.enums.CardSuit;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name="card")
public class Card
{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "card_seq")
    private long id;

    @Column(name="owner_type", nullable=false, columnDefinition="TINYINT(4) UNSIGNED DEFAULT 0")
    private int ownerType;

    @Column(name="quality", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int quality;

    @Column(name="suit", nullable=false, columnDefinition = "TINYINT(3) UNSIGNED DEFAULT 0")
    private int suit;

    @Column(name="rank_num", nullable=false, columnDefinition = "TINYINT UNSIGNED DEFAULT 0")
    private int rank;

    @Column(name="attack", nullable=false, columnDefinition = "TINYINT(4) UNSIGNED DEFAULT 0")
    private int attack;

    @Column(name="block", nullable=false, columnDefinition = "TINYINT(4) UNSIGNED DEFAULT 0")
    private int block;

    @Column(name="heal", nullable=false, columnDefinition = "TINYINT(4) UNSIGNED DEFAULT 0")
    private int heal;

    @Column(name="mana", nullable=false, columnDefinition = "TINYINT(4) UNSIGNED DEFAULT 0")
    private int mana;

    @Column(name="draw", nullable=false, columnDefinition = "TINYINT(4) UNSIGNED DEFAULT 0")
    private int draw;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Player player;

    public Card() {}

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public int getOwnerType()
    {
        return ownerType;
    }

    public void setOwnerType(int ownerType)
    {
        this.ownerType = ownerType;
    }

    public CardOwnerType getOwnerTypeEnum()
    {
        switch (ownerType)
        {
            case 0:
                return CardOwnerType.AllPlayers;
            case 1:
                return CardOwnerType.Player;
            case 2:
                return CardOwnerType.Enemy;
            case 3:
                return CardOwnerType.NoOwner;
            default:
                return CardOwnerType.AllPlayers;
        }
    }

    public void setOwnerType(CardOwnerType type)
    {
        ownerType = type.ordinal();
    }

    public int getQuality()
    {
        return quality;
    }

    public void setQuality(int quality)
    {
        this.quality = quality;
    }

    public int getSuit()
    {
        return suit;
    }

    public void setSuit(int suit)
    {
        this.suit = suit;
    }

    public CardSuit getSuitEnum()
    {
        switch (suit)
        {
            case 0:
                return CardSuit.Spade;
            case 1:
                return CardSuit.Heart;
            case 2:
                return CardSuit.Club;
            case 3:
                return CardSuit.Diamond;
            default:
                return CardSuit.Spade;
        }
    }

    public void setSuit(CardSuit cardSuit)
    {
        suit = cardSuit.ordinal();
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

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }
}