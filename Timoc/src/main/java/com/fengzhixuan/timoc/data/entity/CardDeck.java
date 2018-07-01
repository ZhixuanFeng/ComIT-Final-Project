package com.fengzhixuan.timoc.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "card_deck")
public class CardDeck
{
    @Id
    @Column(name = "id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Player player;

    @OneToMany(mappedBy = "cardDeck", cascade = CascadeType.ALL, orphanRemoval = true)
    private Card[] cards = new Card[52];

    public CardDeck(Player player)
    {
        this.player = player;
        player.setCardDeck(this);
    }

    public CardDeck() {}

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

    public Card[] getCards()
    {
        return cards;
    }

    public void setCards(Card[] cards)
    {
        this.cards = cards;
    }
}
