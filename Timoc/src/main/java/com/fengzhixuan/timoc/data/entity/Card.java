package com.fengzhixuan.timoc.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.data.enums.CardOwnerType;
import com.fengzhixuan.timoc.data.enums.CardSuit;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="card")
@SequenceGenerator(name = "card_seq", sequenceName = "card_seq",  initialValue = 20000)
public class Card
{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq")
    private long id;

    @Column(name="owner_type", nullable=false, columnDefinition="TINYINT(4) UNSIGNED DEFAULT 0")
    private int ownerType;

    @Column(name="quality", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int quality;

    @Column(name="indecks", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int indecks;  // indecks = suit * 13 + rank - 1

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_collection_id")
    @JsonBackReference
    private CardCollection cardCollection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_deck_id")
    @JsonBackReference
    private CardDeck cardDeck;

    // using @OneToMany to avoid querying offer object when querying card object.  card and offer is actually one-to-one
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Offer> offers = new ArrayList<>();

    // only used by starter cards
    public Card(int indecks, int attack, int block, int heal, int mana, int aoe, int draw, int revive, int taunt)
    {
        id = indecks + 1;
        this.indecks = indecks;
        this.attack = attack;
        this.block = block;
        this.heal = heal;
        this.mana = mana;
        this.draw = draw;
        this.taunt = taunt;
        this.revive = revive;
        this.aoe = aoe;

        setOwnerType(CardOwnerType.AllPlayers);
        quality = 0;
    }

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

    @JsonIgnore
    public CardOwnerType getOwnerTypeEnum()
    {
        switch (ownerType)
        {
            case 0:
                return CardOwnerType.AllPlayers;
            case 1:
                return CardOwnerType.Player_In_Deck;
            case 2:
                return CardOwnerType.Player_Not_In_Deck;
            case 3:
                return CardOwnerType.Market;
            case 4:
                return CardOwnerType.Enemy;
            case 5:
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

    public int getIndecks()
    {
        return indecks;
    }

    public void setIndecks(int indecks)
    {
        this.indecks = indecks;
    }

    public void setIndex(int suit, int rank)
    {
        this.indecks = suit * 13 + rank - 1;
    }

    @JsonIgnore
    public int getSuit()
    {
        return indecks / 13;
    }

    @JsonIgnore
    public CardSuit getSuitEnum()
    {
        switch (getSuit())
        {
            case 0:
                return CardSuit.Diamond;
            case 1:
                return CardSuit.Club;
            case 2:
                return CardSuit.Heart;
            case 3:
                return CardSuit.Spade;
            default:
                return CardSuit.NA;
        }
    }

    @JsonIgnore
    public int getRank()
    {
        return indecks - getSuit() * 13 + 1;
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

    public CardCollection getCardCollection()
    {
        return cardCollection;
    }

    public void setCardCollection(CardCollection cardCollection)
    {
        this.cardCollection = cardCollection;
    }

    public CardDeck getCardDeck()
    {
        return cardDeck;
    }

    public void setCardDeck(CardDeck cardDeck)
    {
        this.cardDeck = cardDeck;
    }

    @JsonIgnore
    public List<Offer> getOffers()
    {
        return offers;
    }

    public void setOffers(List<Offer> offers)
    {
        this.offers = offers;
    }

    @Override
    public String toString()
    {
        return "Card{" +
                "id=" + id +
                ", ownerType=" + ownerType +
                ", quality=" + quality +
                ", suit=" + getSuit() +
                ", rank=" + getRank() +
                ", attack=" + attack +
                ", block=" + block +
                ", heal=" + heal +
                ", mana=" + mana +
                ", draw=" + draw +
                ", taunt=" + taunt +
                ", revive=" + revive +
                ", aoe=" + aoe +
                '}';
    }
}