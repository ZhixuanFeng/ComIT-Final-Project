package com.fengzhixuan.timoc.data.entity;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "card_deck")
public class CardDeck
{
    @Id
    @Column(name = "id")
    private long id;

    /*@OneToMany(mappedBy = "cardDeck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> allCards = new ArrayList<>();*/
    @OneToMany(mappedBy="cardDeck", fetch = FetchType.LAZY)
    @MapKey(name="indecks")
    private Map<Integer, Card> cards;

    public CardDeck(long id)
    {
        this.id = id;
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

    public Map<Integer, Card> getCards()
    {
        return cards;
    }

    public void setCards(Map<Integer, Card> cards)
    {
        this.cards = cards;
    }
}
