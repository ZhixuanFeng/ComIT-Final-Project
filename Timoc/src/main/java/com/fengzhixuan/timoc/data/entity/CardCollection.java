package com.fengzhixuan.timoc.data.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "card_collection")
public class CardCollection
{
    @Id
    @Column(name = "id")
    private long id;  // will be the same as user id

    @OneToMany(mappedBy = "cardCollection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards = new ArrayList<>();

    public CardCollection(long id)
    {
        this.id = id;
    }

    public CardCollection() {}

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public List<Card> getCards()
    {
        return cards;
    }

    public void setCards(List<Card> cards)
    {
        this.cards = cards;
    }
}
